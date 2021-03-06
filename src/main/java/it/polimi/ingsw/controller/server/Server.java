package it.polimi.ingsw.controller.server;

import java.io.IOException;
import java.net.ServerSocket;

import static java.lang.Thread.sleep;


public class Server {
    public static int serverPort;
    private static int SLEEP_TIME = 10000;
    /**
     * Generates a ServerSocket and starts a new ServerAccepter thread.
     * If a connection error occurs it waits 10 seconds and then restarts
     * @throws InterruptedException InterruptedException
     */
    public void startServer() throws InterruptedException {
        ServerSocket serverSocket;
        ServerAccepter playersAccepter;
        while (true) {
            try {
                System.out.println("Server started");
                serverSocket = new ServerSocket(serverPort);
                playersAccepter = new ServerAccepter(serverSocket);
                playersAccepter.start();
                break;
            } catch (IOException e) {
                System.out.println("An error occurred, restarting in 10 seconds...");
                sleep(SLEEP_TIME);
            }
        }
    }
    public void startServer(int port) throws InterruptedException {
        serverPort = port;
        startServer();
    }
}
