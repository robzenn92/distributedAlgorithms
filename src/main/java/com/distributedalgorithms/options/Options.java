/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: 123456.
 */

package com.distributedalgorithms.options;

import java.util.concurrent.TimeUnit;

public final class Options {

    /**
     * The number of Peers in the System.
     * This number do not include the monitor process, the root actor which has the role of the system and
     * other actors that are involved.
     *
     * Peers will be named following the canonical enumeration starting from 0 to N = MAX_PEERS - 1: peer0, peer1, ...
     */
    public static final int MAX_PEERS = 2;

    /**
     * The probability that an internal event happens during the lifecycle of a process.
     * This probability is used in order to randomly decide if an internal event rather than a message passing take place.
     * Some examples follow:
     *
     * Consider a PROB_INTERNAL_EVENT = 0.75f.
     * This means that during an execution of a process, an internal event is more likely to happen with respect to
     * a send event with a probability of 75%.
     *
     * Consider a PROB_INTERNAL_EVENT = 0.5f.
     * This means that during an execution of a process, the probability that an internal event happens is the
     * same as the one for a send event.
     */
    public static final float PROB_INTERNAL_EVENT = 0.5f;

    public static final int DELTA_TIME = 2500;
    public static TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;


    public static final int SIMULATION_TIME = 5000;


}