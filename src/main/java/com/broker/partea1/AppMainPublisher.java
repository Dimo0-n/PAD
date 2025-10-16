package com.broker.partea1;

import com.broker.partea1.impl.SocketPublisherImpl;

public class AppMainPublisher {
    public static void main(String[] args) {
        SocketPublisherImpl publisher = new SocketPublisherImpl("localhost", 5001);
        System.out.println("[Publisher] Pornit...");
        publisher.start();
    }
}
