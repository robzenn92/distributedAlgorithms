/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: 123456.
 */

package com.distributedalgorithms.project;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.distributedalgorithms.options.Options;
import com.distributedalgorithms.project.HelloAkkaJava.StartMessage;
import com.distributedalgorithms.project.HelloAkkaJava.EndMessage;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

class Monitor extends UntypedActor {

    // private WaitForGraph wfg = new WaitForGraph();

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private int endMessagesReceived = 0;
    private ArrayList<Event> eventsList[] = new ArrayList[Options.MAX_PEERS];
    private FileOutputStream fop;

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof StartMessage) {

            log.info("Start listening..");
            /* DO STUFF */
        }
        else if (message instanceof EndMessage) {

            eventsList[endMessagesReceived] = ((EndMessage) message).getEvents();
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

        DirectedGraph<ProcessVertex, DefaultEdge> lattice = new DefaultDirectedGraph<ProcessVertex, DefaultEdge>(DefaultEdge.class);

        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0, event1;
            event0 = l0.get(index0);
            event1 = l1.get(index1);

            boolean pairwiseInconsistent = event0.getVectorClock().isPairwiseInconsistent(event1.getVectorClock());
            if (!pairwiseInconsistent) {

                ProcessVertex node = new ProcessVertex(String.valueOf(index0) , String.valueOf(index1));
                lattice.addVertex(node);

                buildLatticeRec(index0 + 1, index1, l0, l1, lattice, node);
                buildLatticeRec(index0, index1 + 1, l0, l1, lattice, node);
            }

            System.out.println(lattice.toString());
            writeLatticeOnFile(lattice);

        }
    }

    private void buildLatticeRec(int index0, int index1, ArrayList<Event> l0, ArrayList<Event> l1, DirectedGraph<ProcessVertex, DefaultEdge> lattice, ProcessVertex parent){

        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0, event1;
            event0 = l0.get(index0);
            event1 = l1.get(index1);

            boolean pairwiseInconsistent = event0.getVectorClock().isPairwiseInconsistent(event1.getVectorClock());
            if (!pairwiseInconsistent) {
                ProcessVertex node = new ProcessVertex(String.valueOf(index0) , String.valueOf(index1));
                lattice.addVertex(node);
                lattice.addEdge(parent, node);
                parent.addParent(node);

                buildLatticeRec(index0 + 1, index1, l0, l1, lattice, node);
                buildLatticeRec(index0, index1 + 1, l0, l1, lattice, node);
            }
        }
    }

    private void writeLatticeOnFile(DirectedGraph<ProcessVertex, DefaultEdge> lattice) {
        Vector<ProcessVertex> ris = new Vector<ProcessVertex>();
        for (ProcessVertex pv:lattice.vertexSet()) {
            ris.add(pv);
        };
        Collections.sort(ris);
        for (int i = 0; i< ris.size(); i++){
            System.out.println(ris.get(i).toString());
        }

        ProcessVertex test = new ProcessVertex("6","4");
        ProcessVertex test1   = new ProcessVertex("5","4");
        System.out.print(test.equals(test1));

        FileOutputStream fop =  null;
        File file;


//        Vector<String> level = new Vector<String>(index0+index1+1);
//
//        //inizializzo il vettore con stringhe vuote
//        for (int i = 0; i < index0+index1+1; i++) {
//            level.add("");
//        }
//        //livello iniziale, indice 00
//        level.set(0, "digraph item_set {\n" +
//                "\n" +
//                "// set edge attribute\n" +
//                "edge [dir = none tailport = \"s\" headport = \"n\"]\n" +
//                "splines=false\n" +
//                "\n" +
//                "// the 1o layer\n" +
//                "00 [label = \"00\"];\n");
//
//
//        for (int i = 1; i < vec.size(); i++) {
//            index0 = vec.get(i)/10;
//            index1 = vec.get(i)%10;
//            int somma = index0+index1;
//            level.set(somma, level.get(somma)+ index0 + index1 + " [label = \""+ index0 + index1 +"\"];\n");
//        }
//
//        System.out.print(level.get(0));
//        for (int i = 1; i < level.size(); i++) {
//            System.out.println("// the "+(i+1)+"o layer");
//            System.out.print(level.get(i));
//            System.out.println();
//        }

//        try {
//
//            file = new File("../lattice.dot");
//            fop = new FileOutputStream(file);
//
//            // if file doesnt exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            // get the content in bytes
//            byte[] contentInBytes = content.getBytes();
//
//            fop.write(contentInBytes);
//            fop.flush();
//            fop.close();
//
//            System.out.println("Done");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fop != null) {
//                    fop.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }



}