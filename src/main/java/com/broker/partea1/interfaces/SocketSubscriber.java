package com.broker.partea1.interfaces;

import com.broker.partea1.models.Message;

public interface SocketSubscriber {
    void receiveMessage(Message message);
    void start();
}
