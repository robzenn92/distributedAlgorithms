/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

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
