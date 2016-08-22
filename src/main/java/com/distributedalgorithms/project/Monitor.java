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
import java.util.*;

class Monitor extends UntypedActor {

    private int endMessagesReceived = 0;

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ArrayList<Event> eventsList[] = new ArrayList[Options.MAX_PEERS];
    private FileOutputStream fop;

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof StartMessage) {

            // Catch when the start message is delivered.
            // It is better and more precise (of course more expansive) if we measure the execution time in Nanoseconds.
            log.info("Start listening at " + Options.getCurrentTime());
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

        DirectedGraph<ProcessVertex, DefaultEdge> lattice = new DefaultDirectedGraph<ProcessVertex, DefaultEdge>(DefaultEdge.class);
        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0 = l0.get(index0);
            Event event1 = l1.get(index1);

            boolean pairwiseInconsistent = event0.getVectorClock().isPairwiseInconsistent(event1.getVectorClock());
            if (!pairwiseInconsistent) {

                ProcessVertex node = new ProcessVertex(String.valueOf(index0) , String.valueOf(index1));
                lattice.addVertex(node);

                buildLatticeRec(index0 + 1, index1, l0, l1, lattice, node);
                buildLatticeRec(index0, index1 + 1, l0, l1, lattice, node);
            }

            System.out.println(lattice.toString());
            writeLatticeOnFile(lattice,l0,l1);
        }
    }

    private void buildLatticeRec(int index0, int index1, ArrayList<Event> l0, ArrayList<Event> l1, DirectedGraph<ProcessVertex, DefaultEdge> lattice, ProcessVertex parent){

        if (index0 < l0.size() && index1 < l1.size()) {

            Event event0 = l0.get(index0);
            Event event1 = l1.get(index1);

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


    private void writeLatticeOnFile(DirectedGraph<ProcessVertex, DefaultEdge> lattice, ArrayList<Event> l0, ArrayList<Event> l1) {


        Vector<ProcessVertex> ris = new Vector<ProcessVertex>();
        for (ProcessVertex pv:lattice.vertexSet()) {
            ris.add(pv);
        };
        Collections.sort(ris);


        int somma = Integer.parseInt(ris.lastElement().getFirst())+Integer.parseInt(ris.lastElement().getSecond())+1;
        Vector<String> level = new Vector<String>();    //vettore contenente i vertici del lattice divisi in livelli



        //inizializzo il vettore con stringhe vuote
        for (int i = 0; i < somma; i++) {
            level.add("");
        }


        //livello iniziale, indice 00
        String content= "digraph item_set {\n" +
                "\n" +
                "// set edge attribute\n" +
                "edge [dir = none tailport = \"s\" headport = \"n\"]\n" +
                "splines=false\n" +
                "\n";


        String color="";
        int possibly = 0;

        for (int i = 0; i < ris.size(); i++) {
            somma = Integer.parseInt(ris.get(i).getFirst())+Integer.parseInt(ris.get(i).getSecond());
            int x = l0.get(Integer.parseInt(ris.get(i).getFirst())).getVariable(); //varible peer0
            int y = l1.get(Integer.parseInt(ris.get(i).getSecond())).getVariable(); //variable peer1
            if (Options.getCondition(x,y)){
                possibly++;
                color+=ris.get(i).toInt()+",";
            }
            String last="";
            if (Options.SHOW_VARIABLE){
                last= x + "-" + y +"\"];\n";
            }else{
                last=ris.get(i).toString() +"\"];\n";
            }
            level.set(somma, level.get(somma)+ ris.get(i).toInt() + " [label = \""+last);
        }

        for (int i = 0; i < level.size(); i++) {
            content+="// the "+(i+1)+"o layer\n"+level.get(i)+"\n";
        }

        //inserisco per ogni vertice i suoi figli
        for (int i = 0; i < ris.size(); i++) {
            ProcessVertex tmp = ris.get(i);
            if(tmp.hasParent()) {
                content+=tmp.toInt() + " -> " + tmp.getParentsString()+"\n";
            }
        }

        String content_with_color= content;
        if (color.length()>0){
            content_with_color +=color.substring(0,color.length()-1)+"[style=filled fillcolor=\"red\"]\n";
            System.out.println("There are "+ possibly +" global states that satisfying the predicate");
        }
        content_with_color+="}";
        content+="}";


        writeonFile(content,Options.LATTICE_OUTPUT_DOT_FILE_PATH);

        writeonFile(content_with_color,Options.LATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH);

        if ( definitely(ris,l0,l1) ){
            System.out.print("The distributed computation satisfies Definitely");
        } else{
            System.out.print("The distributed computation NOT satisfies Definitely");
        }

    }


    public void writeonFile(String content, String path){
        FileOutputStream fop =  null;
        File file;

        try {

            file = new File(path);
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
    
    public boolean definitely(Vector<ProcessVertex> list, ArrayList<Event> l0, ArrayList<Event> l1){
        Vector<ProcessVertex> last = new Vector<ProcessVertex>();
        Vector<ProcessVertex> current = new Vector<ProcessVertex>();
        ProcessVertex event00 = list.get(0);

        int level_max = Integer.parseInt(list.lastElement().getFirst())+Integer.parseInt(list.lastElement().getSecond());
        int x = l0.get(0).getVariable(); //varible peer0
        int y = l1.get(0).getVariable(); //variable peer1
        if (! Options.getCondition(x,y)){
            last.add(event00);
        }
        int level = 0;
        //System.out.println(last.get(0).toString());
        while (!last.isEmpty()){
            // inserisco in current tutti i vertici raggiungibili
            for (ProcessVertex tmp: last) {
                for (ProcessVertex parent: tmp.getParents()) {
                    x = l0.get(Integer.parseInt(parent.getFirst())).getVariable(); //varible peer0
                    y = l1.get(Integer.parseInt(parent.getSecond())).getVariable(); //variable peer1
                    if (! Options.getCondition(x,y)){
                        current.add(parent);
                    }
                }
            }
            if (current.isEmpty() & level==level_max){
                return false;
            }
            level++;
            last.removeAllElements();
            for (ProcessVertex tmp: current) {
                last.add(tmp);
            }
            current.removeAllElements();
        }
        return true;
        
    }
}