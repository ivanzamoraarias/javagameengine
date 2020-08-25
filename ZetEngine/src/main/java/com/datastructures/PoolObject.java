package com.datastructures;

public class PoolObject<T> {
    T instance;
    boolean isEnabled;

    PoolObject(T obj, boolean enabled){
        instance = obj;
        this.isEnabled = enabled;
    }

}
