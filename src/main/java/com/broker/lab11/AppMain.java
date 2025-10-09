package com.broker.lab11;

import com.broker.lab11.impl.SocketBroker;
import com.broker.lab11.impl.SocketReceiver;
import com.broker.lab11.impl.SocketSender;

import java.util.concurrent.*;

public class AppMain {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(3);

        exec.submit(() -> new SocketReceiver(5002).start());
        exec.submit(() -> new SocketBroker(5001, "localhost", 5002).start());

        exec.submit(() -> new SocketSender("localhost", 5001).start());

    }
}
