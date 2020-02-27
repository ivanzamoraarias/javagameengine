package com.scene;

public class SceneManager {

    private SceneState state;

    public void connectScene(){
        this.state.state = SceneState.StateType.CONNECTING;
        // engine stablish a conectio to the clients
    }
    public void loadScene() {
        this.state.state = SceneState.StateType.LOADING;
        // load assets to the clients
    }
    public void startScene(){
        this.state.state = SceneState.StateType.START;
        // Set objects to the start state and start scene
    }
    public void updateServer(){
        this.state.state = SceneState.StateType.RECEIVING;
        this.state.state = SceneState.StateType.UPDATING;
        //this.state.state = SceneState.StateType.RECEIVING;
        // scene is updated , particular scene logic
    }
    public void updateClient(){
        this.state.state = SceneState.StateType.SENDING;
        // scene updated in the client side, particular scene logic
        // maybe shard update
    }
   

    public void finishScene() {

        this.state.state = SceneState.StateType.FINISHED;
    }

    private void setState(SceneState state){}
    public SceneState getState() {
        return this.state;
    }
}
