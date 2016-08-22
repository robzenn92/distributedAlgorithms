/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.options;

import scala.Int;

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
    public static final float PROB_INTERNAL_EVENT = 0.75f;


    /**
     * The probability that a variable of the peer change or not.
     *
     */
    public static final float PROB_CHANGE_VARIABLE = 0.75f;
    public static final int MAX_INT = 100;

    /**
     * For the choice of labels of latex vertices
     * If false, it shows index of the events
     * If true, it shows the variable of the peer at that the moment
     *
     */
    public static final boolean SHOW_VARIABLE = true;

    public static final String LATTICE_OUTPUT_DOT_FILE_PATH = "src/main/java/com/distributedalgorithms/out/lattice.dot";
    public static final String LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH = "src/main/java/com/distributedalgorithms/out/latticeWithVar.dot";
    /**
     * Simulation time options
     * DO NOT CHANGE FROM HERE
     * ---------------------
     */

    public static final TimeUnit PRECISION_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static final int DELTA_TIME = 1000;
    public static final TimeUnit DELTA_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static final int SIMULATION_TIME = 3;
    public static final TimeUnit SIMULATION_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * DO NOT CHANGE UP HERE
     * ---------------------
     */

    /**
     * Get the Current Time in the system based on the preference we defined as PRECISION_TIME_UNIT.
     * It is better and more precise (although it is more expansive) if we measure time in Nanoseconds.
     *
     * @return a long value of the current time in the system
     */
    public static long getCurrentTime() {
        return (Options.PRECISION_TIME_UNIT == TimeUnit.NANOSECONDS) ? System.nanoTime() : System.currentTimeMillis();
    }

    public static boolean checkConfigurationFile() {

        boolean check = true;
        // The number of peers in the system has to be a reasonable number
        check = check && MAX_PEERS > 0 && MAX_PEERS < 100;
        // Probability has to be in between 0 and 1.0f
        check = check && PROB_INTERNAL_EVENT >= 0 && PROB_INTERNAL_EVENT <= 1.0f;
        // Probability has to be in between 0 and 1.0f
        check = check && PROB_CHANGE_VARIABLE >= 0 && PROB_CHANGE_VARIABLE <= 1.0f;
        // The value of the variables has to be a reasonable value
        check = check && MAX_INT > 0 && MAX_INT < Int.MaxValue();
        // PRECISION_TIME_UNIT in milliseconds or nanoseconds, others are not allowed
        check = check && (PRECISION_TIME_UNIT == TimeUnit.NANOSECONDS || PRECISION_TIME_UNIT == TimeUnit.MILLISECONDS);
        return check;
    }

    public static boolean getCondition(int x, int y){
        if ( x<(y-3) ) return true;
        else return false;
    }

}