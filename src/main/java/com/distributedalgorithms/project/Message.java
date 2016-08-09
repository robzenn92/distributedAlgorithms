package com.distributedalgorithms.project;

import java.io.Serializable;
import java.util.UUID;
import static java.util.UUID.randomUUID;

/**
 * Created by Roberto on 30/06/16.
 */
class Message implements Serializable {

    private UUID id;
    private Object data;
    private VectorClock vc;

    Message(VectorClock vc) {
        this.id = randomUUID();
        this.data = "ABC";
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
