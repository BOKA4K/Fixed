package com.example.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerConnection {
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public void connect(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        writer = new PrintWriter(socket.getOutputStream(), true, UTF_8);
    }

    public void sendMessage(String message) {
        writer.flush();
        writer.println(message);
    }

    public String receiveMessage() throws IOException {
        return reader.readLine();
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
