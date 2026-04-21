package com.shopping.server;

import com.shopping.db.User;
import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());
    private Server server;

    public void startServer(){
        int port = 50051;

        try {
            server = ServerBuilder.forPort(port).addService(new UserServiceImpl()).build().start();

            logger.info("Server started, listening on " + port);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Clean server shutdown in case JVM is shutting down ");
                    try {
                        UserServer.this.stopServer();
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "Server shutdown interrupted", e);
                    }
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server did not start", e);
        }
    }

    public void stopServer() throws InterruptedException {
        if (server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UserServer server = new UserServer();
        server.startServer();
        server.blockUntilShutdown();
    }
}
