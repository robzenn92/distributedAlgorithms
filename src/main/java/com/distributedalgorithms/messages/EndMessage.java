/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.messages;

import com.distributedalgorithms.project.Event;

import java.util.ArrayList;

/**
 * Description of the class: EndMessage
 *
 * These messages are sent by the monitor towards all the peers. They are used in order to END the simulation.
 * These messages do not cause any update of processes' Vector clocks.
 */
public class EndMessage {

    // The id of the sender
    private int id;

    // Contains the list of events of a single process which will be sent to the monitor
    // in order to build the lattice.
    private ArrayList<Event> events;

    // Contructor accepts the id of the actor who send the EndMessage and the list of events each peer has.
    public EndMessage(int id, ArrayList<Event> events) {
        this.id = id;
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public int getId () { return this.id; }
}