/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */
package com.distributedalgorithms.messages;

import com.distributedalgorithms.project.VectorClock;

import java.io.Serializable;
import java.util.UUID;
import static java.util.UUID.randomUUID;

public class Message implements Serializable {

    private UUID id;
    private Object data;
    private VectorClock vc;

    public Message(VectorClock vc) {
        this.id = randomUUID();
        this.data = "A beautiful content";
        this.vc = vc;
    }

    public UUID getId() {
        return id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public VectorClock getVectorClock() {
        return vc;
    }

    public void setVectorClock(VectorClock vc) {
        this.vc = vc;
    }
}
