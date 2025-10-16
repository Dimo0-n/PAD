package com.broker.partea1.interfaces;

import java.net.Socket;

public interface SocketBroker {
    void start();
    void subscribe(String topic, Socket subscriberSocket);
}
