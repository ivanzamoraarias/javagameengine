package com.scene;

public class SceneState {
    public enum StateType {
        CONNECTING,
        LOADING,
        START,
        SENDING,
        RECEIVING,
        UPDATING,
        FINISHING,
        FINISHED
    }
    public StateType state;
    public int currentFrame;
    public int clientFrame;

}
