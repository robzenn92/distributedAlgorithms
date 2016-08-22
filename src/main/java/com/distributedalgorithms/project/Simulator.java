/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import akka.actor.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.distributedalgorithms.messages.StartMessage;
import com.distributedalgorithms.options.Options;

public class Simulator {

    public static void main(String[] args) {

        // Shows the main menu.
        menu();

        // Check if all the value defined in the configuration file are allowed.
        if (!Options.checkConfigurationFile()) {
            System.out.println("Options.java: Some of the values you defined in it are not allowed.");
            System.exit(0);
        }

        // Configurations defined are allowed.
        System.out.println("Options defined in Options.java are allowed. Loaded. Done.");

        try {

            // Create the ActorSystem
            final ActorSystem system = ActorSystem.create("System");

            // Create the monitor, which will build the lattice and run the evaluation on it.
            // The monitor has been define in Monitor.class.
            final ActorRef monitor = system.actorOf(Props.create(Monitor.class).withDispatcher("my-dispatcher"), "monitor");

            // Populate the array of peers.
            ArrayList<ActorRef> peers = new ArrayList<ActorRef>(Options.getMAX_PEERS());
            for (int i = 0; i < Options.getMAX_PEERS(); i++) {
                peers.add(system.actorOf(Props.create(Peer.class, i, monitor).withDispatcher("my-dispatcher"), "peer" + String.valueOf(i)));
            }

            // Select everybody in the system via path selection: "/user/*" = peers and monitor.
            ActorSelection selection = system.actorSelection("/user/*");

            // Send to everybody the first StartMessage.
            // This is used in order to tell everybody to initialize the simulation and let them know about its duration.
            // For example if you want to run a simulation that last 10 seconds you need to define the following configuration:
            // Options.SIMULATION_TIME = 10
            // Options.SIMULATION_TIME_UNIT = TimeUnit.SECONDS
            selection.tell(new StartMessage(peers, Options.getSIMULATION_TIME(), Options.getSIMULATION_TIME_UNIT()), null);

        } catch (Exception ex) {

            System.out.println("Got the following exception.");
            ex.printStackTrace();
        }
    }

    /**
     * Shows the main menu of the project.
     * It allows the users to change some configurations and customize the simulation options based on the user needs.
     * In this menu it is possible to change the duration of the simulation, the number of peers, some probabilities and the predicate
     * that has to be evaluated at the end of the simulation.
     */
    public static void menu() {

        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        do {

            System.out.println("What do you want change?");
            System.out.println("0 - Nothing. I want to start the simulation.");
            System.out.println("1 - The numbers of peers in the simulation.");
            System.out.println("2 - The duration of the simulation.");
            System.out.println("3 - The delta time between one event and another.");
            System.out.println("4 - The probability of an internal event with respect to a message exchange.");
            System.out.println("5 - The probability that the peers variables will change.");
            System.out.println("6 - The label of the resulting lattice.");
            System.out.println("7 - The predicate to be evaluated.");

            switch (sc.nextInt()) {

                case 0:
                    exit = true;
                    break;

                case 1:
                    System.out.println("How many peer do you want in the simulation?");
                    System.out.println("You will see the lattice only with a number of peer equal to 2.");
                    while (!sc.hasNextInt()) {
                        System.out.println("Please, insert an integer!");
                        sc.nextLine();
                    }
                    Options.setMAX_PEERS(sc.nextInt());
                    break;

                case 2:
                    System.out.println("How many seconds do you want to prolong the simulation time?");
                    while (!sc.hasNextInt()) {
                        System.out.println("Insert an integer please!");
                        sc.nextLine();
                    }
                    Options.setSIMULATION_TIME(sc.nextInt());
                    break;

                case 3:
                    System.out.println("How many milliseconds do you want to prolong the delta time?");
                    while (!sc.hasNextInt()) {
                        System.out.println("Insert an integer please!");
                        sc.nextLine();
                    }
                    Options.setDELTA_TIME(sc.nextInt());
                    break;

                case 4:
                    System.out.println("How likely do you want an internal event? (From 0 to 1)");
                    while (!sc.hasNextFloat()) {
                        System.out.println("Insert a float please from 0 to 1!");
                        sc.nextLine();
                    }
                    float prob = sc.nextFloat();
                    if (prob > 1) {
                        prob = 1;
                    } else if (prob < 0) {
                        prob = 0;
                    }
                    Options.setPROB_INTERNAL_EVENT(prob);
                    break;

                case 5:
                    System.out.println("How likely do you want change the variable? (From 0 to 1)");
                    while (!sc.hasNextFloat()) {
                        System.out.println("Insert a float please from 0 to 1!");
                        sc.nextLine();
                    }
                    prob = sc.nextFloat();
                    if (prob > 1) {
                        prob = 1;
                    } else if (prob < 0) {
                        prob = 0;
                    }
                    Options.setPROB_CHANGE_VARIABLE(prob);


                    System.out.println("Which maximum value can have the object variable?");
                    while (!sc.hasNextInt()) {
                        System.out.println("Insert an integer please!");
                        sc.nextLine();
                    }
                    Options.setMAX_INT(sc.nextInt());
                    break;

                case 6:
                    System.out.println("In lattice, Would you view the value of the variable? (true or false)");
                    while (!sc.hasNextBoolean()) {
                        System.out.println("Insert an integer please!");
                        sc.nextLine();
                    }
                    Options.setSHOW_VARIABLE(sc.nextBoolean());
                    break;

                case 7:
                    sc.nextLine();
                    System.out.println("Insert the predicate, such as x<y");
                    Options.setCondition(sc.nextLine());
                    break;

                default:
                    System.out.println("The number is incorrect");
                    exit = false;
                    break;
            }
        }while ( !exit );
    }
}
