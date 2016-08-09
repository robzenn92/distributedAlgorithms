/**
 * Project for the "Distributed Algorithms" course
 * Acadamic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: 123456.
 */

package com.distributedalgorithms.project;

/**
 * Description of the class: Event
 *
 * Whatever action, which is relevant for the monitor, executed by a peer is stored and logged as an Event.
 * There is no real time clock, so an event is constituted by a vector clock, the id of the peer who raise the event
 * and a name which might be a description of the event itself.
 */
class Event {

    private int peerId;         // Id of the peer who executed the event
    private String name = "";   // Name of the event, description
    private VectorClock vc;     // Event's VectorClock

    Event(int peerId) {
        this.peerId = peerId;
        this.vc = new VectorClock(peerId);
    }

    Event(int peerId, VectorClock vc) {
        this.peerId = peerId;
        this.vc = vc;
    }

    Event(int peerId, VectorClock vc, String name) {
        this.peerId = peerId;
        this.name = name;
        this.vc = vc;
    }

    VectorClock getVectorClock() {
        return this.vc;
    }

    @Override
    public String toString() {
        return vc.toString();
    }
}
