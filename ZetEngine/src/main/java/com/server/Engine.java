package com.server;

import com.component.Component;
import com.datastructures.GoPool;
import com.gameobject.GameObject;
import com.sun.org.apache.xml.internal.utils.ObjectPool;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Engine {
    private static Engine ourInstance;
    //public ArrayList<GameObject> gameObjects;
    GoPool gameObjects;

    public static Engine getInstance() {
        if(ourInstance == null) {
            synchronized (Engine.class){
                if(ourInstance == null){
                    ourInstance = new Engine();
                    ourInstance.runGameLoop();
                }
            }
        }
        return ourInstance;
    }

    private Engine() {
        gameObjects = new GoPool(100);
    }

    public synchronized void Update() {
        for (GameObject object: gameObjects.pool) {
            object.update();
        }

    }

    private void runGameLoop(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Update();
            }
        }, 0, 16);
    }
}
