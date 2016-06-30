package com.distributedalgorithms.project;

import com.distributedalgorithms.options.Options;

/**
 * Created by Roberto on 30/06/16.
 */
public class VectorClock {

    private int vectors[];


    public VectorClock() {
        vectors = new int[Options.MAX_ACTORS];
        System.out.print(vectors.toString());
    }

    public VectorClock(int index, int value) {
        vectors = new int[Options.MAX_ACTORS];
        vectors[index] = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
