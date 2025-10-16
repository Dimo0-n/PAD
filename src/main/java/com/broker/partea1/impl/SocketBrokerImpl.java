package com.broker.partea1.impl;

import com.broker.partea1.interfaces.SocketBroker;
import com.broker.partea1.models.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketBrokerImpl implements SocketBroker {

    private final int listenPort;
    private final Map<String, List<Socket>> subscribers = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

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
        startMessageDispatcher();

        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("[Broker] Ascult pe portul " + listenPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClient(clientSocket));
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
                subscribe(topic, socket);

                // Păstrează socketul activ pentru a primi mesaje
                while (!socket.isClosed()) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }

            } else {
                String topic = extractTopic(line);
                messageQueue.put(new Message(topic, line));
                System.out.println("[Broker] Mesaj primit și adăugat în coadă: " + line);
                socket.close(); // publisher poate închide socketul
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("[Broker] Eroare client: " + e.getMessage());
        }
    }

    private void startMessageDispatcher() {
        Thread dispatcher = new Thread(() -> {
            while (true) {
                try {
                    Message msg = messageQueue.take();
                    List<Socket> targets = subscribers.getOrDefault(msg.getType(), Collections.emptyList());

                    synchronized (targets) {
                        Iterator<Socket> it = targets.iterator();
                        while (it.hasNext()) {
                            Socket s = it.next();
                            try {
                                PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                                writer.println(msg.getBody());
                            } catch (IOException e) {
                                it.remove();
                                try { s.close(); } catch (IOException ignored) {}
                                System.err.println("[Broker] Subscriber inactiv eliminat");
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("[Broker] Dispatcher thread întrerupt");
                }
            }
        });
        dispatcher.setDaemon(true);
        dispatcher.start();
    }

    private String extractTopic(String line) {
        String[] parts = line.split(":", 2);
        return parts.length > 1 ? parts[0].trim() : "info";
    }
}
