package com.server;

import com.datastructures.GoPool;
import com.gameobject.GameObject;
import java.util.Optional;
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

    public GameObject getGameObjectById(String id) {
        Optional<GameObject> answer = gameObjects.pool.stream().filter(item ->item.id== id).findFirst();
        if (!answer.isPresent())
            return null;

        return answer.get();
    }

    private void runGameLoop(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Update();
                //System.out.println("=====================================FUNCIONAAAAAA");
            }
        }, 0, 16);
    }
}
