/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import com.distributedalgorithms.options.Options;
import java.util.Random;

/**
 * Description of the class: Event
 *
 * Whatever action, which is relevant for the monitor, executed by a peer is stored and logged as an Event.
 * There is no real time clock, so an event is constituted by a vector clock, the id of the peer who raise the event
 * and a name which might be a description of the event itself.
 */
public class Event {

    private int peerId;                 // Id of the peer who executed the event
    private String description = "";    // The description of the event
    private VectorClock vc;             // Its VectorClock
    private int variable;               // For predicates

    public Event(int peerId, int value) {
        this.peerId = peerId;
        this.vc = new VectorClock(peerId);
        this.variable = value;
    }

    public Event(int peerId, VectorClock vc, int value) {
        this.peerId = peerId;
        this.vc = vc;
        this.variable = value;
    }

    // Getters and Setters

    public VectorClock getVectorClock() {
        return this.vc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return vc.toString();
    }


    public int getVariable() {
        return variable;
    }

}
