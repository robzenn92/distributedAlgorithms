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
import sun.jvm.hotspot.debugger.posix.elf.ELFSectionHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Description of the class: Monitor
 * This class contains methods for handle the monitor behaviour inside the network.
 * Monitor receives a StartMessage from the actor system and the it will wait until the peers send EndMessages.
 * Once the simulation is ended, an evaluation of the given non-stable predicate is performed.
 * Graphical results will be given to the user.
 */
class Monitor extends UntypedActor {

    // Count the number of EndMessages received by the peers
    private int endMessagesReceived = 0;

    // A simple log instance.
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // A list of events histories, one for each peer
    // We will consider only if the number of peers in the actual system is equal to 2.
    private ArrayList<Event> eventsList[] = new ArrayList[Options.getMAX_PEERS()];

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof StartMessage) {

            // Catch when the start message is delivered.
            // It is better and more precise (of course more expansive) if we measure the execution time in Nanoseconds.
            log.info("Start listening at " + Options.getCurrentTime());
            log.info("I will receive from everybody an EndMessage in " + Options.getSIMULATION_TIME() + " " + Options.getSIMULATION_TIME_UNIT() + ".");
        }
        else if (message instanceof EndMessage) {

            eventsList[((EndMessage) message).getId()] = ((EndMessage) message).getEvents();
            endMessagesReceived++;

            // Check if EndMessages arrived from all the peers
            if (endMessagesReceived == Options.getMAX_PEERS()) {

                log.info("I received EndMessages from all the peers.");

                if(Options.getMAX_PEERS() == 2) {
                    log.info("There are only 2 peers. I start creating the lattice. Then I will run the evaluation.");
                    buildLattice(0, 0, eventsList[0], eventsList[1]);
                }else{
                    System.out.println("END of SIMULATION");
                }

            }
        }
        else {
            unhandled(message);
        }
    }

    /**
     * Starts by the first event of each history and analyses whether these events are pairwise inconsistent.
     * If so, nothing can be done, otherwise the lattice is built step by step incrementing one index of one history at time.
     * Once the lattice is created, the structure is stored in a file with the .dot extension.
     * @param index0 index of the first list of events
     * @param index1 index of the second list of events
     * @param l0 first list of events
     * @param l1 second list of events
     */
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

    /**
     * Starts by the first event of each history and analyses whether these events are pairwise inconsistent.
     * If so, nothing can be done, otherwise the lattice is built step by step incrementing one index of one history at time.
     * @param index0 index of the first list of events
     * @param index1 index of the second list of events
     * @param l0 first list of events
     * @param l1 second list of events
     * @param lattice the lattice partially created up to previous recursive invocation.
     * @param parent the ancestor of the current nodes in the lattice
     */
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

    /**
     * Create the content of the dot file for the creation of the image lattice with graphviz
     * @param lattice the lattice created with the buildLattice function.
     * @param l0 first list of events
     * @param l1 second list of events
     */
    private void writeLatticeOnFile(DirectedGraph<ProcessVertex, DefaultEdge> lattice, ArrayList<Event> l0, ArrayList<Event> l1) {

        // In ris vector there are the Vertex in order of the level
        Vector<ProcessVertex> ris = new Vector<ProcessVertex>();
        for (ProcessVertex pv:lattice.vertexSet()) {
            ris.add(pv);
        }
        Collections.sort(ris);

        // the somma variable is the maximum level of the lattice
        int somma = Integer.parseInt(ris.lastElement().getFirst())+Integer.parseInt(ris.lastElement().getSecond())+1;
        Vector<String> level = new Vector<String>();

        // Initialize the level vector with empty strings
        for (int i = 0; i < somma; i++) {
            level.add("");
        }

        // Set the configuration of the file
        String content= "digraph item_set {\n" +
                "\n" +
                "// set edge attribute\n" +
                "edge [dir = none tailport = \"s\" headport = \"n\"]\n" +
                "splines=false\n" +
                "\n";


        // For each vertex create the String "id [label='#event_peer0 - #event_peer1'];" or "id [label='var x - var y'];",
        // according to the choice of the user, and add it in the correct level of the vector
        for (int i = 0; i < ris.size(); i++) {
            somma = Integer.parseInt(ris.get(i).getFirst())+Integer.parseInt(ris.get(i).getSecond());
            int x = l0.get(Integer.parseInt(ris.get(i).getFirst())).getVariable(); //varible peer0
            int y = l1.get(Integer.parseInt(ris.get(i).getSecond())).getVariable(); //variable peer1

            String last="";
            if (Options.isSHOW_VARIABLE()){
                last= x + "-" + y +"\"];\n";
            }else{
                last=ris.get(i).toString() +"\"];\n";
            }

            level.set(somma, level.get(somma)+ ris.get(i).toInt() + " [label = \""+last);
        }

        for (int i = 0; i < level.size(); i++) {
            content+="// the "+(i+1)+"o layer\n"+level.get(i)+"\n";
        }

        // For each vertex create the String of the parents such as "0 -> 10, 01;" that represent the lattice edges
        for (int i = 0; i < ris.size(); i++) {
            ProcessVertex tmp = ris.get(i);
            if(tmp.hasParent()) {
                content+=tmp.toInt() + " -> " + tmp.getParentsString()+"\n";
            }
        }


        content+="}";
        writeonFile(content,Options.getLATTICE_OUTPUT_DOT_FILE_PATH());

        do {
            if (!possibly(ris, l0, l1)) {
                System.out.println("There are NOT global states that satisfying the predicate");
            }

            if (definitely(ris, l0, l1)) {
                System.out.println("The distributed computation satisfies Definitely");
            } else {
                System.out.println("The distributed computation NOT satisfies Definitely");
            }


        }while (secondMenu());
        System.out.println("END");
    }


    public void writeonFile(String content, String path) {

        FileOutputStream fop =  null;
        File file;

        try {

            file = new File(path);
            fop = new FileOutputStream(file);

            // If file doesn't exist, then create it.
            if (!file.exists()) {
                file.createNewFile();
            }

            // Get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("The file is written in "+path);

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


    /**
     * Create the content of the dot file for the creation of the image lattice with graphviz
     * @param list the ordered list of the vertex in the lattice
     * @param l0 first list of events
     * @param l1 second list of events
     */
    public boolean possibly(Vector<ProcessVertex> list, ArrayList<Event> l0, ArrayList<Event> l1){
        int somma = Integer.parseInt(list.lastElement().getFirst())+Integer.parseInt(list.lastElement().getSecond())+1;
        Vector<String> level = new Vector<String>();

        // Initialize the level vector with empty strings
        for (int i = 0; i < somma; i++) {
            level.add("");
        }

        // Set the configuration of the file
        String content= "digraph item_set {\n" +
                "\n" +
                "// set edge attribute\n" +
                "edge [dir = none tailport = \"s\" headport = \"n\"]\n" +
                "splines=false\n" +
                "\n";


        // For each vertex create the String "id [label='#event_peer0 - #event_peer1'];" or "id [label='var x - var y'];",
        // according to the choice of the user, and add it in the correct level of the vector
        for (int i = 0; i < list.size(); i++) {
            somma = Integer.parseInt(list.get(i).getFirst())+Integer.parseInt(list.get(i).getSecond());
            int x = l0.get(Integer.parseInt(list.get(i).getFirst())).getVariable(); //varible peer0
            int y = l1.get(Integer.parseInt(list.get(i).getSecond())).getVariable(); //variable peer1

            String last="";
            if (Options.isSHOW_VARIABLE()){
                last= x + "-" + y +"\"];\n";
            }else{
                last=list.get(i).toString() +"\"];\n";
            }

            level.set(somma, level.get(somma)+ list.get(i).toInt() + " [label = \""+last);
        }

        for (int i = 0; i < level.size(); i++) {
            content+="// the "+(i+1)+"o layer\n"+level.get(i)+"\n";
        }

        // For each vertex create the String of the parents such as "0 -> 10, 01;" that represent the lattice edges
        for (int i = 0; i < list.size(); i++) {
            ProcessVertex tmp = list.get(i);
            if(tmp.hasParent()) {
                content+=tmp.toInt() + " -> " + tmp.getParentsString()+"\n";
            }
        }
        // This string contains the vertex that satisfy the predicate
        String color="";
        // The possibly variable is the number of global states that satisfy the predicate
        int possibly = 0;

        for (int i = 0; i < list.size(); i++) {
            int x = l0.get(Integer.parseInt(list.get(i).getFirst())).getVariable(); // the variable peer0
            int y = l1.get(Integer.parseInt(list.get(i).getSecond())).getVariable(); // the variable peer1
            if (Options.getCondition(x, y)) {
                possibly++;
                color += list.get(i).toInt() + ",";
            }
        }

        if (color.length()>0){
            content +=color.substring(0,color.length()-1)+"[style=filled fillcolor=\"red\"]\n";
            System.out.println("There are "+ possibly +" global states that satisfying the predicate");
            content+="}";
            writeonFile(content, Options.getLATTICE_WITH_VARIABLE_OUTPUT_DOT_FILE_PATH());
            return true;
        }else{
            return false;
        }



    }

    /**
     * Create the content of the dot file for the creation of the image lattice with graphviz
     * @param list the ordered list of the vertex in the lattice
     * @param l0 first list of events
     * @param l1 second list of events
     */
    public boolean definitely(Vector<ProcessVertex> list, ArrayList<Event> l0, ArrayList<Event> l1){
        Vector<ProcessVertex> last = new Vector<ProcessVertex>(); // Contains the vertex that NOT satisfy the predicate in the last level
        Vector<ProcessVertex> current = new Vector<ProcessVertex>(); // Contains the vertex that NOT satisfy the predicate in the current level
        ProcessVertex event00 = list.get(0);

        int level_max = Integer.parseInt(list.lastElement().getFirst())+Integer.parseInt(list.lastElement().getSecond());
        int x = l0.get(0).getVariable(); // the variable of peer0 at start time
        int y = l1.get(0).getVariable(); // the variable of peer0 at start time
        // If the first event satisfy the predicate the definitely is true, else add the event at the vector 'last'
        if (! Options.getCondition(x,y)){
            last.add(event00);
        }
        int level = 0;

        while (!last.isEmpty()){
            // for each vertex in last, Add in the current Vector the parents that not satisfy the predicate
            for (ProcessVertex tmp: last) {
                for (ProcessVertex parent: tmp.getParents()) {
                    x = l0.get(Integer.parseInt(parent.getFirst())).getVariable(); //varible peer0
                    y = l1.get(Integer.parseInt(parent.getSecond())).getVariable(); //variable peer1
                    if (! Options.getCondition(x,y)){
                        current.add(parent);
                    }
                }
            }
            // when arrived at the bottom of lattice and if the vector current is empty then the definitely is false
            if (current.isEmpty() && level==level_max){
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


    public boolean secondMenu(){
        boolean exit=false;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("What would you like to do?");
            System.out.println("1 - Change the label of the lattice");
            System.out.println("2 - Change the predicate to be evaluated");
            System.out.println("3 - Recreate the lattice with new options");
            System.out.println("0 - Exit");

            switch (sc.nextInt()) {
                case 1:
                    System.out.println("Would you show the variable in the lattice? (true or false)");
                    while (!sc.hasNextBoolean()) {
                        System.out.println("Insert an integer please!");
                        sc.nextLine();
                    }
                    Options.setSHOW_VARIABLE(sc.nextBoolean());
                    break;
                case 2:
                    sc.nextLine();
                    System.out.println("Insert the NEW predicate, such as x<y");
                    Options.setCondition(sc.nextLine());
                    break;
                case 3:
                    exit=true;
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("The number is incorrect");
                    exit = false;
                    break;
            }
        }while (!exit);


        return true;
    }
}