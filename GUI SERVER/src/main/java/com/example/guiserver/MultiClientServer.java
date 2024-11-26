package com.example.guiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class MultiClientServer {
    Socket clientSocket;
    private ServerSocket serverSocket;
    public static List<ClientHandler> clients=new ArrayList<>();
    public MultiClientServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            System.out.println(clients.size());
            try {
                clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
