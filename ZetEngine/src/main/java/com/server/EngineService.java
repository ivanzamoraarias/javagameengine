package com.server;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/zetengine")
public class EngineService  {

    @OnMessage
    public String handleTextMessage(String message) {
        System.out.println("New Text Message Received");
        return message;
    }

    @OnMessage(maxMessageSize = 1024000)
    public byte[] handleBinaryMessage(byte[] buffer) {
        System.out.println("New Binary Message Received");
        return buffer;
    }

}
