package com.broker.partea2.impl;

import com.broker.partea2.BrokerOuterClass.Message;
import com.broker.partea2.BrokerOuterClass.SubscriptionRequest;
import com.broker.partea2.BrokerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GRPCSubscriberImpl {

        public GRPCSubscriberImpl(String host, int port, String... topics) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", port)
                    .usePlaintext()
                    .build();

        BrokerServiceGrpc.BrokerServiceStub asyncStub = BrokerServiceGrpc.newStub(channel);

        for (String topic : topics) {
            SubscriptionRequest req = SubscriptionRequest.newBuilder()
                    .setTopic(topic)
                    .build();

            asyncStub.subscribe(req, new StreamObserver<Message>() {
                @Override
                public void onNext(Message value) {
                    System.out.println("[Subscriber gRPC][" + topic + "] Received: " + value.getContent());
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    System.out.println("[Subscriber gRPC][" + topic + "] Stream completed.");
                }
            });

            System.out.println("[Subscriber gRPC] Subscribed to topic: " + topic);
        }
    }
}
