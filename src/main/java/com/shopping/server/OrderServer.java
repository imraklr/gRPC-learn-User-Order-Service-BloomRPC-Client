package com.shopping.server;

import com.shopping.service.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServer {
    private static final Logger logger = Logger.getLogger(UserServer.class.getName());
    private Server server;

    public void startServer() {
        // Ensure that the OrderServiceImpl class is hosted on a port
        int port = 50053;
        try {
            server = ServerBuilder
                    .forPort(port)
                    .addService(new OrderServiceImpl())
                    .build()
                    .start();
            logger.info("Server started on port "+ port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Clean server shutdown in case JVM was shutdown/killed");
                OrderServer.this.stopServer();
            }));
        }catch (IOException IOe) {
            logger.log(Level.SEVERE, "Server did not start", IOe);
        }
    }

    public void stopServer() {
        try {
            if(server!=null) {
                // Issue a command so that no new request will be accepted after this
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            }
        }catch (InterruptedException Ie) {
            logger.log(Level.SEVERE, "Server shutdown interrupted", Ie);
        }
    }

    public void blockUntilShutdown() {
        try {
            if(server!=null) {
                server.awaitTermination();
            }
        } catch(InterruptedException Ie) {
            logger.log(Level.SEVERE, "Failed to block until shutdown", Ie);
        }
    }

    /**
     * We can always run the server by running this main method but if we want to run it using
     * cmd line like using: java -jar <path of jar>,
     * we need to :
     * changes in POM.xml
     * add a plugin to create a fat jar
     * add the main method class in the maven POM file
     * add the resources folder to your fat jar -> We need the resources folder because we have the
     * `initialize.sql`, the database script that we want to run as a part of the setup. The application
     * won't run without `initialize.sql` file.
     * This method will not work if we have two main methods within same project. Industry projects are
     * placed in different repos.
     * @param args
     */
    public static void main(String... args) {
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown(); // block the main thread to keep the server running
    }
}
