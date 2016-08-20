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

    public boolean hasParent(){ return this.parent.size()!=0; }

    public String getParentsString(){
        String ris="{";
        for (ProcessVertex pv:this.parent) {
            ris+=pv.toInt()+",";
        }
        return ris.substring(0,ris.length()-1)+"}";
    }

    public Vector<ProcessVertex> getParents(){
        return this.parent;
    }

    @Override
    public String toString() {
        return this.peer0+"-"+this.peer1;
    }

    public int toInt() {
        return Integer.parseInt(this.peer0+this.peer1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ProcessVertex)) {
            return false;
        }
        else return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Integer.parseInt(this.peer0);
        hash = 71 * hash + Integer.parseInt(this.peer1);
        return hash;
    }

    public int compareTo(ProcessVertex pv) {
        return this.toInt()-pv.toInt();
    }
}
