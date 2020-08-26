package com.gameobject;

import com.enums.ComponentType;
import com.component.Component;

import java.util.HashMap;

public interface Entity {
    void update();
    void addComponent(Component c);
    HashMap<ComponentType, Component> getComponentsMap();
    Component getComponent(Class<? extends Component> cls);


}
