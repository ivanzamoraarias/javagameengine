package com.scene;

public interface Scene {
    void startScene();
    void updateScene();
    void endScene();

    void setSceneState();
    SceneState getSceneState();

}
