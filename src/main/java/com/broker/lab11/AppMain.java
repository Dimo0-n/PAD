package com.broker.lab11;

import com.broker.lab11.impl.SocketBrokerImpl;
import com.broker.lab11.impl.SocketSubscriberImpl;
import com.broker.lab11.impl.SocketPublisherImpl;

public class AppMain {
    public static void main(String[] args) {
        SocketBrokerImpl broker = new SocketBrokerImpl(5001);
        new Thread(broker::start).start();

        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        SocketSubscriberImpl subscriber1 = new SocketSubscriberImpl("localhost", 5001, "info");
        SocketSubscriberImpl subscriber2 = new SocketSubscriberImpl("localhost", 5001, "alert");

        new Thread(subscriber1::start).start();
        new Thread(subscriber2::start).start();

        SocketPublisherImpl publisher = new SocketPublisherImpl("localhost", 5001);
        new Thread(publisher::start).start();
    }
}
