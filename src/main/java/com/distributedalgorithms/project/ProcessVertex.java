/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import java.util.Vector;

class ProcessVertex implements Comparable<ProcessVertex>{

    private String peer0 = "";
    private String peer1 = "";
    private Vector<ProcessVertex> parent = new Vector<ProcessVertex>();

    public ProcessVertex(String p0) {
        this.peer0 = p0;
    }

    public ProcessVertex(String p0, String p1) {
        this.peer0 = p0;
        this.peer1 = p1;
    }

    public String getFirst(){
        return this.peer0;
    }

    public String getSecond(){
        return this.peer1;
    }

    public void addParent(ProcessVertex pv){
        parent.add(pv);
    }

    @Override
    public String toString() {
        return this.peer0+this.peer1;
    }

    @Override
    public boolean equals(Object obj) {
        return peer0.equals(((ProcessVertex)obj).getFirst()) && peer1.equals(((ProcessVertex)obj).getSecond());
    }

    public int compareTo(ProcessVertex pv) {
        return Integer.parseInt(this.toString()) - Integer.parseInt(pv.toString());
    }
}
