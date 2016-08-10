/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */
package com.distributedalgorithms.messages;

import akka.actor.ActorRef;
import com.distributedalgorithms.options.Options;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Description of the class: StartMessage
 *
 * These messages are sent by the monitor towards all the peers. They are used in order to START the simulation.
 *
 * The contribution of start messages to the system is twofold. First, they allow peers to set their vector clock
 * to the initial value (each entry with value zero). Second, they are used to let peers know each others,
 * building a fully connected graph between all the nodes in the system.
 * These messages do not cause any update of processes' Vector clocks.
 */
public class StartMessage {

    // Contains the list of peers which will be part of the system
    // Used in order to build a fully connected graph between all the nodes in the network
    private ArrayList<ActorRef> peers = new ArrayList<ActorRef>(Options.MAX_PEERS - 1);

    // Time and Time units define the duration of the simulation of each peer.
    private int endTime;
    private TimeUnit simulationTimeUnits;

    public StartMessage(ArrayList<ActorRef> peers, int endTime, TimeUnit simulationTimeUnits) {
        this.peers = peers;
        this.endTime = endTime;
        this.simulationTimeUnits = simulationTimeUnits;
    }

    public ArrayList<ActorRef> getPeers() {
        return this.peers;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public TimeUnit getSimulationTimeUnits() {
        return this.simulationTimeUnits;
    }
}