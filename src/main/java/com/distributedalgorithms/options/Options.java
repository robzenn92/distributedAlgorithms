/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.options;

import scala.Int;

import java.util.concurrent.TimeUnit;
import org.mariuszgromada.math.mxparser.*;



public class Options {

    /**
     * The number of Peers in the System.
     * This number do not include the monitor process, the root actor which has the role of the system and
     * other actors that are involved.
     *
     * Peers will be named following the canonical enumeration starting from 0 to N = MAX_PEERS - 1: peer0, peer1, ...
     */
    private static int MAX_PEERS = 2;

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
    private static float PROB_INTERNAL_EVENT = 0.5f;

    /**
     * The probability that a variable of the peer change or not.
     *
     */
    private static float PROB_CHANGE_VARIABLE = 0.75f;
    private static int MAX_INT = 100;

    /**
     * For the choice of labels of latex vertices
     * If false, it shows index of the events
     * If true, it shows the variable of the peer at that the moment
     *
     */
    private static boolean SHOW_VARIABLE = false;

    private static String LATTICE_OUTPUT_DOT_FILE_PATH = "src/main/java/com/distributedalgorithms/out/lattice.dot";
    private static String LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH = "src/main/java/com/distributedalgorithms/out/latticeWithVar.dot";

    /**
     * Simulation time options
     * DO NOT CHANGE FROM HERE
     * ---------------------
     */
    private static TimeUnit PRECISION_TIME_UNIT = TimeUnit.MILLISECONDS;

    private static int DELTA_TIME = 1000;
    private static TimeUnit DELTA_TIME_UNIT = TimeUnit.MILLISECONDS;

    private static int SIMULATION_TIME = 3;
    private static TimeUnit SIMULATION_TIME_UNIT = TimeUnit.SECONDS;

    private static String condition="x<y";

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
        return (getPRECISION_TIME_UNIT() == TimeUnit.NANOSECONDS) ? System.nanoTime() : System.currentTimeMillis();
    }

    public static boolean checkConfigurationFile() {

        boolean check = true;
        // The number of peers in the system has to be a reasonable number
        check = check && getMAX_PEERS() > 0 && getMAX_PEERS() < 100;
        // Probability has to be in between 0 and 1.0f
        check = check && getPROB_INTERNAL_EVENT() >= 0 && getPROB_INTERNAL_EVENT() <= 1.0f;
        // Probability has to be in between 0 and 1.0f
        check = check && getPROB_CHANGE_VARIABLE() >= 0 && getPROB_CHANGE_VARIABLE() <= 1.0f;
        // The value of the variables has to be a reasonable value
        check = check && getMAX_INT() > 0 && getMAX_INT() < Int.MaxValue();
        // PRECISION_TIME_UNIT in milliseconds or nanoseconds, others are not allowed
        check = check && (getPRECISION_TIME_UNIT() == TimeUnit.NANOSECONDS || getPRECISION_TIME_UNIT() == TimeUnit.MILLISECONDS);
        return check;
    }

    public static boolean getCondition(int x, int y){
        String tmp = condition;
        tmp = tmp.replace("x",String.valueOf(x));
        tmp = tmp.replace("y", String.valueOf(y));
        Expression e = new Expression(tmp);
        if ( e.calculate()==1.0) return true;
        else return false;
    }


    public static int getMAX_PEERS() {
        return MAX_PEERS;
    }

    public static void setMAX_PEERS(int MAX_PEERS) {
        Options.MAX_PEERS = MAX_PEERS;
    }


    public static float getPROB_INTERNAL_EVENT() {
        return PROB_INTERNAL_EVENT;
    }

    public static void setPROB_INTERNAL_EVENT(float PROB_INTERNAL_EVENT) {
        Options.PROB_INTERNAL_EVENT = PROB_INTERNAL_EVENT;
    }


    public static float getPROB_CHANGE_VARIABLE() {
        return PROB_CHANGE_VARIABLE;
    }

    public static void setPROB_CHANGE_VARIABLE(float PROB_CHANGE_VARIABLE) {
        Options.PROB_CHANGE_VARIABLE = PROB_CHANGE_VARIABLE;
    }

    public static int getMAX_INT() {
        return MAX_INT;
    }

    public static void setMAX_INT(int MAX_INT) {
        Options.MAX_INT = MAX_INT;
    }


    public static boolean isSHOW_VARIABLE() {
        return isShowVariable();
    }

    public static void setSHOW_VARIABLE(boolean SHOW_VARIABLE) {
        Options.setShowVariable(SHOW_VARIABLE);
    }

    public static String getLATTICE_OUTPUT_DOT_FILE_PATH() {
        return LATTICE_OUTPUT_DOT_FILE_PATH;
    }

    public static void setLATTICE_OUTPUT_DOT_FILE_PATH(String LATTICE_OUTPUT_DOT_FILE_PATH) {
        Options.LATTICE_OUTPUT_DOT_FILE_PATH = LATTICE_OUTPUT_DOT_FILE_PATH;
    }

    public static String getLATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH() {
        return LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH;
    }

    public static void setLATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH(String LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH) {
        Options.LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH = LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH;
    }

    public static TimeUnit getPRECISION_TIME_UNIT() {
        return PRECISION_TIME_UNIT;
    }

    public static void setPRECISION_TIME_UNIT(TimeUnit PRECISION_TIME_UNIT) {
        Options.PRECISION_TIME_UNIT = PRECISION_TIME_UNIT;
    }

    public static int getDELTA_TIME() {
        return DELTA_TIME;
    }

    public static void setDELTA_TIME(int DELTA_TIME) {
        Options.DELTA_TIME = DELTA_TIME;
    }

    public static TimeUnit getDELTA_TIME_UNIT() {
        return DELTA_TIME_UNIT;
    }

    public static void setDELTA_TIME_UNIT(TimeUnit DELTA_TIME_UNIT) {
        Options.DELTA_TIME_UNIT = DELTA_TIME_UNIT;
    }

    public static int getSIMULATION_TIME() {
        return SIMULATION_TIME;
    }

    public static void setSIMULATION_TIME(int SIMULATION_TIME) {
        Options.SIMULATION_TIME = SIMULATION_TIME;
    }

    public static TimeUnit getSIMULATION_TIME_UNIT() {
        return SIMULATION_TIME_UNIT;
    }

    public static void setSIMULATION_TIME_UNIT(TimeUnit SIMULATION_TIME_UNIT) {
        Options.SIMULATION_TIME_UNIT = SIMULATION_TIME_UNIT;
    }

    public static boolean isShowVariable() {
        return SHOW_VARIABLE;
    }

    public static void setShowVariable(boolean showVariable) {
        SHOW_VARIABLE = showVariable;
    }


    public static void setCondition(String condition) {
        Options.condition = condition;
    }
}