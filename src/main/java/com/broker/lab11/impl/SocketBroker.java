package com.broker.lab11.impl;

import com.broker.lab11.interfaces.MessageBroker;
import com.broker.lab11.models.Message;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketBroker implements MessageBroker {
    private final int listenPort;
    private final String receiverHost;
    private final int receiverPort;

    private final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    public SocketBroker(int listenPort, String receiverHost, int receiverPort) {
        this.listenPort = listenPort;
        this.receiverHost = receiverHost;
        this.receiverPort = receiverPort;
    }

    @Override
    public void forwardMessage(Message message) {
        try (Socket receiverSocket = new Socket(receiverHost, receiverPort);
             PrintWriter writer = new PrintWriter(receiverSocket.getOutputStream(), true)) {
            writer.println(message.getBody());
            System.out.println("[Broker] Mesaj redirecționat: " + message.getBody());
        } catch (IOException e) {
            System.err.println("[Broker] Eroare la trimiterea către receiver: " + e.getMessage());
        }
    }

    public void start() {

        startWorkerThread();

        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("[Broker] Ascult pe portul " + listenPort);

            while (true) {
                Socket senderSocket = serverSocket.accept();

                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(senderSocket.getInputStream()))) {

                        String json = reader.readLine();

                        if (json == null || json.isBlank()) {
                            System.err.println("[Broker] Mesaj invalid primit — ignorat.");
                            return;
                        }

                        messageQueue.add(new Message("info", json));
                        System.out.println("[Broker] Mesaj adăugat în coadă: " + json);


                    } catch (IOException e) {
                        System.err.println("[Broker] Eroare thread: " + e.getMessage());
                    } finally {
                        try {
                            senderSocket.close();
                        } catch (IOException ignored) {}
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startWorkerThread() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    if (!messageQueue.isEmpty()) {
                        Message msg = messageQueue.poll();
                        if (msg != null) {
                            forwardMessage(msg);
                        }
                    }

                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("[Broker] Worker thread întrerupt.");
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
        System.out.println("[Broker] Worker thread pornit pentru procesarea mesajelor din coadă.");
    }
}
