package com.component;

import com.gameobject.GameObject;
import com.sun.xml.internal.ws.api.pipe.Engine;

import java.time.temporal.Temporal;

public class ComponentParameters {
    public Engine engine;
    public GameObject gameObject;
    public Temporal period;
    public Temporal deadline;
}
