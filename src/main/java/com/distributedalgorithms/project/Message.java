package com.distributedalgorithms.project;

import java.io.Serializable;

/**
 * Created by Roberto on 30/06/16.
 */
public class Message implements Serializable {

    private VectorClock vc = new VectorClock();
    private Object data;

    public Message() {
        data = new String("ABC");
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public VectorClock getVc() {
        return vc;
    }

    public void setVc(VectorClock vc) {
        this.vc = vc;
    }
}
