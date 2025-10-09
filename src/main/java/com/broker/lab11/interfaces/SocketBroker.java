package com.broker.lab11.interfaces;

import com.broker.lab11.models.Message;

import java.net.Socket;

public interface SocketBroker {
    void start();
    void subscribe(String topic, Socket subscriberSocket);
}
