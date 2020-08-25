package com.gameobject;

import com.component.Component;
import com.enums.ComponentType;

import java.util.HashMap;
import java.util.Hashtable;

public class GameObject implements Entity, Comparable<GameObject> {
    public String id;
    public boolean isEnabled;
    private Hashtable<String, Component> componentsMap;

    public GameObject() {
        componentsMap = new Hashtable<>();
    }

    @Override
    public void update() {
        for (Component i: componentsMap.values()) {
            i.update();
        }
    }

    @Override
    public void addComponent(Component c) {
        this.componentsMap.put(c.getClass().getSimpleName(), c);
    }

    @Override
    public HashMap<ComponentType, Component> getComponentsMap() {
        return null;
    }

    @Override
    public Component getComponent(Component cls) {
        return null;
    }

    @Override
    public int compareTo(GameObject o) {
        return 0;
    }
}
