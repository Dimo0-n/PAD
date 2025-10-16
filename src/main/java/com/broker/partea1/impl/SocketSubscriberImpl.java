package com.broker.partea1.impl;

import com.broker.partea1.interfaces.SocketSubscriber;
import com.broker.partea1.models.Message;

import java.io.*;
import java.net.Socket;

public class SocketSubscriberImpl implements SocketSubscriber {

    private final String brokerHost;
    private final int brokerPort;
    private final String topic;

    public SocketSubscriberImpl(String brokerHost, int brokerPort, String topic) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
        this.topic = topic;
    }

    @Override
    public void start() {
        try {
            Socket socket = new Socket(brokerHost, brokerPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println("SUBSCRIBE:" + topic);

            System.out.println("[Subscriber] Ascult mesaje pe topic: " + topic);

            String line;
            while ((line = reader.readLine()) != null) {
                receiveMessage(new Message(topic, line));
            }

        } catch (IOException e) {
            System.err.println("[Subscriber] Eroare: " + e.getMessage());
        }
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println("[Subscriber] Mesaj primit pe topic '" + topic + "': " + message.getBody());
    }
}
