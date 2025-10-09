package com.broker.lab11.impl;

import com.broker.lab11.interfaces.SocketPublisher;
import com.broker.lab11.models.Message;

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
            writer.println(message.toString());
            System.out.println("[Publisher] Mesaj trimis: " + message);
        } catch (IOException e) {
            System.err.println("[Publisher] Eroare: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Publisher] Introdu topicul și mesajul (format: topic:mesaj), 'exit' pentru a ieși:");

        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            String[] parts = input.split(":", 2);
            if (parts.length < 2) {
                System.out.println("Format invalid, folosește: topic:mesaj");
                continue;
            }

            String topic = parts[0].trim();
            String body = parts[1].trim();
            sendMessage(new Message(topic, body));
        }
    }
}