package com.distributedalgorithms.project;

/**
 * Created by Roberto on 09/08/16.
 */
class ProcessVertex {

    private String name = "";

    public ProcessVertex(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
