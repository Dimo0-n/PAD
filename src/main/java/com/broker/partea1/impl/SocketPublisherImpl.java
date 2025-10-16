package com.broker.partea1.impl;

import com.broker.partea1.interfaces.SocketPublisher;
import com.broker.partea1.models.Message;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketPublisherImpl implements SocketPublisher {

    private final String brokerHost;
    private final int brokerPort;

    public SocketPublisherImpl(String brokerHost, int brokerPort) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
    }

    @Override
    public void sendMessage(Message message) {
        try (Socket socket = new Socket(brokerHost, brokerPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            writer.println(message.getType() + ":" + message.getBody());
            System.out.println("[Publisher] Mesaj trimis: " + message.getType() + ":" + message.getBody());

        } catch (IOException e) {
            System.err.println("[Publisher] Eroare: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Publisher] Introdu mesajele în format topic:mesaj, 'exit' pentru a ieși:");

        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            String[] parts = input.split(":", 2);
            if (parts.length < 2) {
                System.out.println("Format invalid, folosește: topic:mesaj");
                continue;
            }

            sendMessage(new Message(parts[0].trim(), parts[1].trim()));
        }
    }
}
