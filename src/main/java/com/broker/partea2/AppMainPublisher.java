package com.broker.partea2;

import com.broker.partea2.impl.GRPCPublisherImpl;
import java.util.Scanner;

public class AppMainPublisher {

    public static void main(String[] args) {
        GRPCPublisherImpl publisher = new GRPCPublisherImpl("127.0.0.1", 50051);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter message (topic:content): ");
            String line = sc.nextLine();
            String[] parts = line.split(":", 2);
            String topic = parts[0].trim();
            String content = parts.length > 1 ? parts[1].trim() : "";
            publisher.sendMessage(topic, content);
        }
    }
}
