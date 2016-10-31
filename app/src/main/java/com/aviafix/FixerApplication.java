package com.aviafix;

import com.aviafix.resources.OrdersResourse;
import com.aviafix.resources.PartsResourse;
import com.bendb.dropwizard.jooq.*;
import io.dropwizard.Application;
import io.dropwizard.db.*;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.aviafix.resources.FixerResource;
import com.aviafix.health.TemplateHealthCheck;


public class FixerApplication extends Application<FixerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new FixerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Fixer";
    }

    @Override
    public void initialize(final Bootstrap<FixerConfiguration> bootstrap) {
        // TODO: application initialization
        // added all below
        bootstrap.addBundle(new JooqBundle<FixerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(FixerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public JooqFactory getJooqFactory(FixerConfiguration configuration) {
                return configuration.getJooqFactory();
            }
        });
    }

    @Override
    public void run(final FixerConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        final FixerResource resource = new FixerResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );

        final OrdersResourse ordersResourse = new OrdersResourse();
        final PartsResourse partsResourse = new PartsResourse();

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());

        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(ordersResourse);
        environment.jersey().register(partsResourse);

    }

}
