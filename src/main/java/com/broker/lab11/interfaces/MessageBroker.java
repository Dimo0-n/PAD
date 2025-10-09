package com.broker.lab11.interfaces;

import com.broker.lab11.models.Message;

public interface MessageBroker {
    void forwardMessage(Message message);
}
