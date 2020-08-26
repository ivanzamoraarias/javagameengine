package com.server;

import com.component.Component;
import com.component.ComponentsEnum;
import com.enums.ComponentType;
import com.gameobject.GameObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.updater.ObjectUpdater;
import com.updater.ObjectUpdaterCreator;
import io.reactivex.rxjava3.core.Flowable;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Class.forName;

@ServerEndpoint(value = "/zetengine")
public class EngineService  {
    @OnMessage
    public String handleTextMessage(String message) {
        // run the program --> gradlew appRun
        System.out.println("New Text Message Received");

        Gson playerObject = new Gson();

        Map model = playerObject.fromJson(message, Map.class);
        //Map componentProperties = playerObject.fromJson(model.get("componentProperties"), Map.class);

        System.out.println("Lets test this sht "+model.get("componentProperties").getClass().toString());



        String objectId = (String) model.get("gameObjectId");

        Flowable.fromCallable(() -> {
            Engine engine = Engine.getInstance();
            System.out.println("GOT ENGINE");
            return engine;
        }).switchMap((Engine engine) -> {
            System.out.println("GOT ENGINE----"+engine==null);
                    return Flowable.just(
                            engine.getGameObjectById(objectId)
                    );
                }
        ).concatMap(gameObject -> {
            System.out.println("GOT ENGINE=====");
                    Class componentClass = Class.forName((String)model.get("componetType"));
            System.out.println("GOT ENGINE=== class parsed");
                    return Flowable.just(
                            gameObject.getComponent(componentClass)
                            );
                }
        ).switchMap(component -> {
            component.setUpdateMap(model);
            return Flowable.empty();
        }).subscribe();

        String ardillaResponse = "la ardilla esta en la posilga";


        return ardillaResponse;
    }

    @OnMessage(maxMessageSize = 1024000)
    public byte[] handleBinaryMessage(byte[] buffer) {
        System.out.println("New Binary Message Received");
        return buffer;
    }

    @OnOpen
    public String handleOnOpen(){
        Engine.getInstance();

        String response = "Engine up and running";

        return response;

    }

}
