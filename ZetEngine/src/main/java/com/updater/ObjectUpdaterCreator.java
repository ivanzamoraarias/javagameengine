package com.updater;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class ObjectUpdaterCreator implements InstanceCreator<ObjectUpdater> {
    public ObjectUpdater createInstance(Type T){
        ObjectUpdater object = new ObjectUpdater();
        return object;
    }
}
