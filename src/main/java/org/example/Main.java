package org.example;

import jakarta.servlet.Filter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.filter.AnonymousAuthenticationFilter;
import org.example.filter.BasicAuthenticationFilter;
import org.example.filter.BearerAuthenticationFilter;
import org.example.listener.ContextLoadDestroyListener;
import org.example.server.Server;

import org.example.servlet.Servlet;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws LifecycleException, IOException {
        final Server server = new Server();
        final Connector connector = new Connector("HTTP/1.1");
        connector.setPort(9999);
        server.setConnector(connector);
        final Context context = server.creatContext("",
                Files.createDirectories(Paths.get("static"))
                        .toFile()
                        .getAbsolutePath());

        final ContextResource db = new ContextResource();
        db.setName("jdbc/db");
        db.setAuth("Container");
        db.setType(DataSource.class.getName());
        db.setProperty("url", "jdbc:postgresql://localhost:5432/db?user=app&password=pass");
        db.setProperty("maxTotal", "20");
        db.setProperty("maxIdle", "10");
        db.setCloseMethod("close");
        context.getNamingResources().addResource(db);

        context.addServletContainerInitializer(
                (c, ctx) -> ctx.addListener(
                        new ContextLoadDestroyListener()),
                null);

        registerFilter(context, new BasicAuthenticationFilter(), "basic");
        registerFilter(context, new BearerAuthenticationFilter(), "bearer");
        registerFilter(context, new AnonymousAuthenticationFilter(), "anon");

        Wrapper wrapper = context.createWrapper();
        wrapper.setServlet(new Servlet());
        wrapper.setName("front");
        wrapper.setLoadOnStartup(1);
        context.addChild(wrapper);
        context.addServletMappingDecoded("/", wrapper.getName());

        server.start();
    }
    private static void registerFilter(Context context, Filter filter, String name) {

        final FilterDef filterDef = new FilterDef();
        filterDef.setFilter(filter);
        filterDef.setFilterName(name);
        context.addFilterDef(filterDef);

        final FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterDef.getFilterName());
        filterMap.addURLPatternDecoded("/*");
        context.addFilterMap(filterMap);
    }
}
