package com.broker.lab11.interfaces;

import com.broker.lab11.models.Message;

public interface SocketSubscriber {
    void receiveMessage(Message message);
    void start();
}
