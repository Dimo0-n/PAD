package com.broker.lab11.impl;

import com.broker.lab11.interfaces.MessageSender;
import com.broker.lab11.models.Message;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketSender implements MessageSender {
    private final String brokerHost;
    private final int brokerPort;

    public SocketSender(String brokerHost, int brokerPort) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
    }

    @Override
    public void sendMessage(Message message) {
        try (Socket socket = new Socket(brokerHost, brokerPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(message.toString());
            System.out.println("[Sender] Mesaj trimis: " + message);
        } catch (IOException e) {
            System.err.println("[Sender] Eroare: " + e.getMessage());
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Sender] Introdu mesajele tale (exit pentru a ieși):");
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;
            sendMessage(new Message("info", input));
        }
    }
}
