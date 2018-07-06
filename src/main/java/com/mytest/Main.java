package com.mytest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.util.Optional;
import java.util.Properties;
import javax.inject.Provider;
import play.Application;
import play.Environment;
import play.Mode;
import play.api.Play;
import play.core.j.JavaModeConverter;
import play.core.server.ServerConfig;
import play.core.server.ServerProvider;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Results;
import play.routing.RoutingDsl;
import play.server.Server;
import scala.compat.java8.OptionConverters;

public class Main {

    public static class RouterProvider implements Provider<play.api.routing.Router> {

        private final RoutingDsl routingDsl;

        @Inject
        public RouterProvider(final RoutingDsl routingDsl) {
            this.routingDsl = routingDsl;
        }

        @Override
        public play.api.routing.Router get() {

            this.routingDsl.GET("/test").routeTo(() -> {
                System.out.println("Controller - Thread: " + Thread.currentThread().getId());
                return Results.ok("Hello");
            });

            return this.routingDsl.build().asScala();
        }
    }

    public static void main(final String[] args) {
        final Mode mode = Mode.DEV;
        final String address = "0.0.0.0";
        final int port = 8080;

        final Config baseConfig = ConfigFactory.load();
        final Config conf = com.typesafe.config.ConfigFactory.parseResources("app.conf").withFallback(baseConfig);

        final Environment environment = new Environment(mode);
        final Application application = new GuiceApplicationBuilder()
            .loadConfig(conf)
            .in(environment)
            .overrides(new AbstractModule() {

                @Override
                protected void configure() {
                    bind(play.api.routing.Router.class).toProvider(RouterProvider.class);
                }
            })
            .build();

        new Server(ServerProvider.defaultServerProvider().createServer(
            new ServerConfig(
                new File("."),
                OptionConverters.toScala(Optional.of(port)),
                OptionConverters.toScala(Optional.empty()),
                address,
                JavaModeConverter.asScalaMode(mode),
                new Properties(),
                application.asScala().configuration()),
            application.asScala()));

        Play.start(application.asScala());

    }
}
