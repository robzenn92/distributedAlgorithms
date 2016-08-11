/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import akka.actor.*;
import java.util.ArrayList;

import com.distributedalgorithms.messages.StartMessage;
import com.distributedalgorithms.options.Options;
import scala.concurrent.ExecutionContext;

public class Simulator {

    public static void main(String[] args) {

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

//            final ExecutionContext ex = system.dispatchers().lookup("my-dispatcher");

            // Populate the array of peers.
            ArrayList<ActorRef> peers = new ArrayList<ActorRef>(Options.MAX_PEERS);
            for (int i = 0; i < Options.MAX_PEERS; i++) {
                peers.add(system.actorOf(Props.create(Peer.class, i, monitor).withDispatcher("my-dispatcher"), "peer" + String.valueOf(i)));
            }

            // Select everybody in the system via path selection: "/user/*" = peers and monitor.
            ActorSelection selection = system.actorSelection("/user/*");

            // Send to everybody the first StartMessage.
            // This is used in order to tell everybody to run the simulation and let them know about its duration.
            // For example if you want to run a simulation that last 10 seconds you need to define the following configuration:
            // Options.SIMULATION_TIME = 10
            // Options.SIMULATION_TIME_UNIT = TimeUnit.SECONDS
            selection.tell(new StartMessage(peers, Options.SIMULATION_TIME, Options.SIMULATION_TIME_UNIT), null);

        } catch (Exception ex) {

            System.out.println("Got the following exception.");
            ex.printStackTrace();
        }
    }
}
