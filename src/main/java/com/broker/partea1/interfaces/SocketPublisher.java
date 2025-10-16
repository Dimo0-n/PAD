package com.broker.partea1.interfaces;

import com.broker.partea1.models.Message;

public interface SocketPublisher {
    void sendMessage(Message message);
    void start(); // metoda pentru loop-ul de citire mesaje de la user
}
