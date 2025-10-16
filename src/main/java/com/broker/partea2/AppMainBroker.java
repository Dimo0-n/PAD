package com.broker.partea2;

import com.broker.partea2.impl.GRPCBrokerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class AppMainBroker {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new GRPCBrokerImpl())
                .build()
                .start();

        System.out.println("[Broker gRPC] Started on port 50051");
        server.awaitTermination();
    }
}
