package com.broker.lab11.interfaces;

import com.broker.lab11.models.Message;

public interface SocketPublisher {
    void sendMessage(Message message);
    void start(); // metoda pentru loop-ul de citire mesaje de la user
}
