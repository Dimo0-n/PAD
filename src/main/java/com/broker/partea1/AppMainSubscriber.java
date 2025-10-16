package com.broker.partea1;

import com.broker.partea1.impl.SocketSubscriberImpl;

public class AppMainSubscriber {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Folosește: java ...AppMainSubscriber <topic1> [<topic2> ...]");
            return;
        }

        String brokerHost = "localhost";
        int brokerPort = 5001;

        for (String topic : args) {
            // creează un thread separat pentru fiecare topic
            new Thread(() -> {
                SocketSubscriberImpl subscriber = new SocketSubscriberImpl(brokerHost, brokerPort, topic);
                subscriber.start();
            }).start();
        }
    }
}
