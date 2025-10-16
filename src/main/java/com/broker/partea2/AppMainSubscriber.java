package com.broker.partea2;

import com.broker.partea2.impl.GRPCSubscriberImpl;

public class AppMainSubscriber {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java AppMainSubscriber <topic1> <topic2> ...");
            return;
        }

        new GRPCSubscriberImpl("127.0.0.1", 50051, args);

        // Keep main thread alive
        while (true) {
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
