package com.broker.partea2.impl;

import com.broker.partea2.BrokerOuterClass.Ack;
import com.broker.partea2.BrokerOuterClass.Message;
import com.broker.partea2.BrokerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCPublisherImpl {

    private final BrokerServiceGrpc.BrokerServiceBlockingStub blockingStub;

    public GRPCPublisherImpl(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", port)
                .usePlaintext()
                .build();

        blockingStub = BrokerServiceGrpc.newBlockingStub(channel);
    }

    public void sendMessage(String topic, String content) {
        Message msg = Message.newBuilder().setTopic(topic).setContent(content).build();
        Ack ack = blockingStub.sendMessage(msg);
        System.out.println("[Publisher gRPC] Ack: " + ack.getSuccess());
    }
}
