package com.broker.lab11.impl;

import com.broker.lab11.interfaces.SocketBroker;
import com.broker.lab11.models.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketBrokerImpl implements SocketBroker {

    private final int listenPort;
    private final Map<String, List<Socket>> subscribers = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final ExecutorService connectionPool = Executors.newFixedThreadPool(50);

    public SocketBrokerImpl(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public void subscribe(String topic, Socket subscriberSocket) {
        subscribers.computeIfAbsent(topic, t -> Collections.synchronizedList(new ArrayList<>()))
                .add(subscriberSocket);
        System.out.println("[Broker] Subscriber abonat la topic '" + topic + "' de pe " + subscriberSocket.getRemoteSocketAddress());
    }

    @Override
    public void start() {
        startWorkerThread();

        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("[Broker] Ascult pe portul " + listenPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectionPool.submit(() -> handleClient(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            if (line == null || line.isBlank()) return;

            if (line.startsWith("SUBSCRIBE:")) {
                String topic = line.substring("SUBSCRIBE:".length()).trim();
                subscribe(topic, socket); // păstrăm socket-ul deschis!
            } else {
                String topic = extractTopic(line);
                messageQueue.put(new Message(topic, line));
                System.out.println("[Broker] Mesaj adăugat în coadă: " + line);
                socket.close(); // doar aici închidem socket-ul client dacă nu e subscriber
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("[Broker] Eroare client: " + e.getMessage());
        }
    }

    private void startWorkerThread() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    Message msg = messageQueue.take();
                    List<Socket> targets = subscribers.getOrDefault(msg.getType(), Collections.emptyList());

                    for (Socket s : targets) {
                        try {
                            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                            writer.println(msg.getBody());
                        } catch (IOException e) {
                            System.err.println("[Broker] Eroare la trimitere: " + e.getMessage());
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("[Broker] Worker thread întrerupt.");
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private String extractTopic(String json) {
        int start = json.indexOf("\"type\":\"") + 8;
        int end = json.indexOf("\"", start);
        return start >= 0 && end > start ? json.substring(start, end) : "info";
    }
}