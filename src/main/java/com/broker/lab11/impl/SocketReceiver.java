package com.broker.lab11.impl;

import com.broker.lab11.interfaces.MessageReceiver;
import com.broker.lab11.models.Message;
import java.io.*;
import java.net.*;

public class SocketReceiver implements MessageReceiver {
    private final int port;

    public SocketReceiver(int port) {
        this.port = port;
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println("[Receiver] Mesaj primit: " + message.getBody());
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Receiver] Ascult pe portul " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String json = reader.readLine();
                Message msg = new Message("info", json);
                receiveMessage(msg);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
