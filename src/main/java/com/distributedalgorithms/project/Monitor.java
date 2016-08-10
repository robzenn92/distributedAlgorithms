/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.distributedalgorithms.messages.EndMessage;
import com.distributedalgorithms.messages.StartMessage;
import com.distributedalgorithms.options.Options;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class Monitor extends UntypedActor {

    private long startTime;

    private int endMessagesReceived = 0;

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ArrayList<Event> eventsList[] = new ArrayList[Options.MAX_PEERS];
    private FileOutputStream fop;

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof StartMessage) {

            // Catch when the start message is delivered.
            // It is better and more precise (of course more expansive) if we measure the execution time in Nanoseconds.
            this.startTime = Options.getCurrentTime();

            log.info("Start listening at " + startTime);
            log.info("I will receive from everybody an EndMessage in " + Options.SIMULATION_TIME + " " + Options.SIMULATION_TIME_UNIT + ".");
        }
        else if (message instanceof EndMessage) {

            eventsList[((EndMessage) message).getId()] = ((EndMessage) message).getEvents();
            endMessagesReceived++;

            // Check if EndMessages arrived from all the peers
            if (endMessagesReceived == Options.MAX_PEERS) {

                System.out.println("\n\n" + eventsList[0].toString() + "\n\n" + eventsList[1].toString() + "\n\n");
                buildLattice(0, 0, eventsList[0], eventsList[1]);
            }
        }
        else {
            unhandled(message);
        }
    }

    private void buildLattice(int index0, int index1, ArrayList<Event> l0, ArrayList<Event> l1) {

        DirectedGraph<String, DefaultEdge> lattice = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0, event1;
            event0 = l0.get(index0);
            event1 = l1.get(index1);

            boolean pairwiseInconsistent = event0.getVectorClock().isPairwiseInconsistent(event1.getVectorClock());
            if (!pairwiseInconsistent) {

                String node = String.valueOf(index0) + String.valueOf(index1);
                lattice.addVertex(node);

                buildLatticeRec(index0 + 1, index1, l0, l1, lattice, node);
                buildLatticeRec(index0, index1 + 1, l0, l1, lattice, node);
            }

            System.out.println(lattice.toString());

        }
    }

    private void buildLatticeRec(int index0, int index1, ArrayList<Event> l0, ArrayList<Event> l1, DirectedGraph<String, DefaultEdge> lattice, String parent){

        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0, event1;
            event0 = l0.get(index0);
            event1 = l1.get(index1);

            boolean pairwiseInconsistent = event0.getVectorClock().isPairwiseInconsistent(event1.getVectorClock());
            if (!pairwiseInconsistent) {
                String node = String.valueOf(index0) + String.valueOf(index1);
                lattice.addVertex(node);
                lattice.addEdge(parent, node);

                buildLatticeRec(index0 + 1, index1, l0, l1, lattice, node);
                buildLatticeRec(index0, index1 + 1, l0, l1, lattice, node);
            }
        }
    }

    private void writeLatticeOnFile() {

        FileOutputStream fop =  null;
        File file;
        String content = "This is the text content";

        try {

            file = new File("../lattice.txt");
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}