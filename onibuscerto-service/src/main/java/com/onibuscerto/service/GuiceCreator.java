package com.onibuscerto.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sitebricks.SitebricksModule;
import com.onibuscerto.service.apps.RouteApp;

public class GuiceCreator extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new SitebricksModule() {
           @Override
           protected void configureSitebricks() {
               scan(RouteApp.class.getPackage());
           }
        });
    }
}
