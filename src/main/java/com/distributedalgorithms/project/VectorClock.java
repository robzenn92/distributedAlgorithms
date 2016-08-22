/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import com.distributedalgorithms.options.Options;

public class VectorClock {

    private int vectors[];
    private int id_process;

    VectorClock(int id) {
        setId_process(id);
        vectors = new int[Options.getMAX_PEERS()];
    }

    VectorClock(int id, VectorClock last) {
        this(id);
        update(last);
    }

    VectorClock(int id, VectorClock last, VectorClock msg) {
        this(id);
        update(last, msg);
    }

    private void update(VectorClock last) {
        for (int i = 0; i < Options.getMAX_PEERS(); i++) {
            vectors[i] = last.getElement(i);
        }
        vectors[last.getId_process()] += 1;
    }

    private void update(VectorClock last, VectorClock msg){
        for (int i = 0; i < Options.getMAX_PEERS(); i++) {
            vectors[i] = Math.max(last.getElement(i), msg.getElement(i));
        }
        vectors[last.getId_process()] += 1;
    }

    public boolean isConcurrent(VectorClock vc){

        int j = vc.getId_process();
        return this.getElement(id_process) > vc.getElement(id_process) && vc.getElement(j) > this.getElement(j);
    }

    public boolean isPairwiseInconsistent(VectorClock vc){

        int j = vc.getId_process();
        return this.getElement(id_process) < vc.getElement(id_process) || vc.getElement(j) < this.getElement(j);
    }

    @Override
    public String toString() {
        String ris="[";
        for (int i = 0; i < (vectors.length - 1) ; i++) {
            ris+= vectors[i] + ",";
        }
        ris += vectors[vectors.length - 1] + "]";
        return ris;
    }

    public int getElement(int index){
        return vectors[index];
    }

    public int[] getVectors(){
        return vectors;
    }

    public int getId_process() {
        return id_process;
    }

    public void setId_process(int id_process) {
        this.id_process = id_process;
    }
}
