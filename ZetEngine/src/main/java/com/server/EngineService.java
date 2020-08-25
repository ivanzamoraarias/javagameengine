package com.server;

import com.component.ComponentsEnum;
import com.gameobject.GameObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.updater.ObjectUpdater;
import com.updater.ObjectUpdaterCreator;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/zetengine")
public class EngineService  {
    static ConcurrentHashMap<String, GameObject> gameObjectsStorage = new ConcurrentHashMap<>();
    @OnMessage
    public String handleTextMessage(String message) {
        // run the program --> gradlew appRun
        System.out.println("New Text Message Received");

        Gson playerObject = new Gson();

        Map model = playerObject.fromJson(message, Map.class);

        System.out.println("Lets test this sht "+model.get("componentProperties").getClass().toString());


        String objectId = (String) model.get("gameObjectId");

        Engine engine = Engine.getInstance();

        if(model.get("componetType") == ComponentsEnum.TRANSFORM){


        }


        String ardillaResponse = "la ardilla esta en la posilga";


        return ardillaResponse;
    }

    @OnMessage(maxMessageSize = 1024000)
    public byte[] handleBinaryMessage(byte[] buffer) {
        System.out.println("New Binary Message Received");
        return buffer;
    }

}
