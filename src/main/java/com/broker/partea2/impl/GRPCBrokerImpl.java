package com.broker.partea2.impl;

import com.broker.partea2.BrokerOuterClass.Ack;
import com.broker.partea2.BrokerOuterClass.Message;
import com.broker.partea2.BrokerOuterClass.SubscriptionRequest;
import com.broker.partea2.BrokerServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.*;

public class GRPCBrokerImpl extends BrokerServiceGrpc.BrokerServiceImplBase {

    private final Map<String, List<StreamObserver<Message>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(Message request, StreamObserver<Ack> responseObserver) {
        String topic = request.getTopic();
        List<StreamObserver<Message>> targets = subscribers.getOrDefault(topic, Collections.emptyList());

        synchronized (targets) {
            Iterator<StreamObserver<Message>> it = targets.iterator();
            while (it.hasNext()) {
                StreamObserver<Message> obs = it.next();
                try {
                    obs.onNext(request);
                } catch (Exception e) {
                    it.remove();
                }
            }
        }

        Ack ack = Ack.newBuilder().setSuccess(true).build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribe(SubscriptionRequest request, StreamObserver<Message> responseObserver) {
        String topic = request.getTopic();
        subscribers.computeIfAbsent(topic, t -> Collections.synchronizedList(new ArrayList<>()))
                .add(responseObserver);
        System.out.println("[Broker gRPC] Subscriber abonat pe topic: " + topic);
    }
}
