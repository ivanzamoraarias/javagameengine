package com.datastructures;


import com.gameobject.GameObject;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class GoPool {
    int numberOfObjects;
    public ArrayList<GameObject> pool;

    public GoPool() {

        pool = new ArrayList();

    }

    public GoPool(int n) {
        this.numberOfObjects = n;
        pool = new ArrayList<GameObject>(n);
    }


    void createPool(int size) throws IllegalAccessException, InstantiationException {
        this.dealocatePool();
        this.numberOfObjects = size - 1;

        for (int i = 0; i < size; i++) {
            this.pool.add(new GameObject());
        }
    }

    void dealocatePool() {
        pool.clear();
    }

    GameObject getFirstAvailable() {

        Optional<GameObject> answer =
                pool.stream()
                        .filter(item -> item.isEnabled == true)
                        .findFirst();

        if (!answer.isPresent())
            return null;

        return answer.get();
    }

}
