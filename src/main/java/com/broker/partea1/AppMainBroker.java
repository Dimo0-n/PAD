package com.broker.partea1;

import com.broker.partea1.impl.SocketBrokerImpl;

public class AppMainBroker {
    public static void main(String[] args) {
        SocketBrokerImpl broker = new SocketBrokerImpl(5001);
        System.out.println("[Broker] Pornit pe portul 5001...");
        broker.start();
    }
}
