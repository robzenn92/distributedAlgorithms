/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: 123456.
 */

package com.distributedalgorithms.project;

import akka.actor.*;
import akka.dispatch.BoundedMessageQueueSemantics;
import akka.dispatch.RequiresMessageQueue;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.distributedalgorithms.options.Options;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Random;
public class HelloAkkaJava {

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
    static class StartMessage {

        // Contains the list of peers which will be part of the system
        // Used in order to build a fully connected graph between all the nodes in the network
        private ArrayList<ActorRef> peers = new ArrayList<ActorRef>(Options.MAX_PEERS - 1);

        StartMessage(ArrayList<ActorRef> peers) {
            this.peers = peers;
        }

        ArrayList<ActorRef> getPeers() {
            return this.peers;
        }
    }

    /**
     * Description of the class: EndMessage
     *
     * These messages are sent by the monitor towards all the peers. They are used in order to END the simulation.
     * These messages do not cause any update of processes' Vector clocks.
     */
    static class EndMessage {

        // Contains the list of events of a single process which will be sent to the monitor
        // in order to build the lattice.
        private ArrayList<Event> events;

        // The id of the process sender
        private int id;

        public EndMessage(ArrayList<Event> events, int id) {
            this.events = events;
            this.id = id;
        }

        ArrayList<Event> getEvents() {
            return this.events;
        }

        int getId () { return this.id; }
    }

    /**
     * Description of the class: Peer
     * This class contains methods for handle the peer behaviour inside the network.
     */
     private static class Peer extends UntypedActor implements RequiresMessageQueue<BoundedMessageQueueSemantics> {

        private int id;
        private final ActorRef monitor;
        private boolean running = false;
        private ArrayList<ActorRef> neighbours = new ArrayList<ActorRef>(Options.MAX_PEERS - 1);
        private ArrayList<Event> events = new ArrayList<Event>();
        private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        public Peer(int id, ActorRef monitor) {
            this.id = id;
            this.monitor = monitor;
        }

        public void onReceive(Object message) {

            if (message instanceof StartMessage) {

                // I am running..
                this.running = true;

                // Start up the fully connected graph network:
                // I received the list of all the peers in the network, I need to remove myself from the list
                this.neighbours = (ArrayList<ActorRef>) ((StartMessage) message).getPeers().clone();
                this.neighbours.remove(getSelf());

                // Set up my VectorClock to the default value.
                events.add(new Event(id));

                // Start a random execution:
                // Up to this point every peer in the system has executed one local event just to set its Vector clock.
                // Now, I start sending asynchronous messages to random peers, till the monitor decides to stop me.
                startRandomExecution();
                stopRandomExecution();

            } else if (message instanceof Message) {

                // Get the VectorClock of my last event
                // Raise a new local event and update my VectorClock based on the previous one and the VectorClock
                // of the peer which sent me this Message.
                VectorClock last = events.get(events.size() - 1).getVectorClock();
                VectorClock current = new VectorClock(id, last, ((Message) message).getVectorClock());
                Event e = new Event(id, current);
                events.add(e);

                log.info(getSelf().path() + " had last vc = " + last + " | received message " + ((Message) message).getId() + " with vc = " + ((Message) message).getVectorClock() + " from " + getSender().path() + " | now has vc = " + current);

            } else if (message instanceof EndMessage) {

                this.running = false;

            } else {

                unhandled(message);
            }
        }

        private void startRandomExecution() {

            Random rand;    // Random generator

            int i = 0;

            while(running) {


                if (i == 3) {
                    return;
                }
                i++;

                // For each neighbour
                for(final ActorRef p:this.neighbours) {

                    // Get random integer between 0 and DELTA_TIME.
                    // This will be used to schedule an event that will be executed between the next 0 and DELTA_TIME TIME_UNIT.
                    // Values defined in the Options class.
                    rand = new Random();
                    int scheduled = rand.nextInt(Options.DELTA_TIME);
                    FiniteDuration duration = FiniteDuration.create(scheduled, Options.TIME_UNIT);

                    // I schedule what to do (send message or execute internal event) in the next future.
                    getContext().system().scheduler().scheduleOnce(
                        duration,
                        new Runnable() {
                            public void run() {

                                // Get random real number between 0 and 1.
                                // This will be the probability we will use to decide if the process
                                // has to execute an internal event rather than send a message to one of its neighbours.
                                Random rand = new Random();
                                float resulting_prob = rand.nextFloat();

                                // Whatever choice I made, a new event is executed and it VectorClock has to be updated.
                                VectorClock last = events.get(events.size() - 1).getVectorClock();
                                VectorClock current = new VectorClock(id, last);
                                Event e = new Event(id, current);

                                // As result, we have chosen to execute an internal event
                                if (resulting_prob < Options.PROB_INTERNAL_EVENT) {

                                    // I just want to know remember that this was an internal event
                                    e.setDescription("INTERNAL EVENT");
                                    log.info("My last VC = " + last + " | Now my VC = " + current + " | I executed an INTERNAL EVENT");
                                }
                                else { // As result, we have chosen to send a message to one of our neighbours

                                    // The message is prepared and sent to p (the neighbour selected), the event is logged.
                                    e.setDescription("SEND EVENT");
                                    Message m = new Message(current);
                                    p.tell(m, getSelf());
                                    log.info("My last VC = " + last + " | Now my VC = " + current + " | I SENT message " + m.getId() + " to " + p.path());
                                }

                                // I append my last event to my history.
                                events.add(e);
                            }
                        }, getContext().dispatcher()
                    );
                }
            }
        }

        private void stopRandomExecution() {

            FiniteDuration duration = FiniteDuration.create(Options.SIMULATION_TIME, Options.TIME_UNIT);

            // I schedule to send to the monitor an EndMessage which contains the list of events.
            getContext().system().scheduler().scheduleOnce(
                    duration,
                    new Runnable() {
                        public void run() {
                            monitor.tell(new EndMessage(events, id), getSelf());
                        }
                    }, getContext().dispatcher()
            );
        }
    }

    public static void main(String[] args) {

        int numberOfActors = Options.MAX_PEERS;
        ArrayList<ActorRef> peers = new ArrayList<ActorRef>(numberOfActors);

        try {

            // Create the ActorSystem
            final ActorSystem system = ActorSystem.create("System");

            // Create the monitor, which will build the lattice and shutdown the system
            final ActorRef monitor = system.actorOf(Props.create(Monitor.class), "monitor");

            // Populate array of processes
            ActorRef peer;
            for (int i = 0; i < numberOfActors; i++) {
                peer = system.actorOf(Props.create(Peer.class, i, monitor), "peer" + String.valueOf(i));
                peers.add(peer);
            }

            ActorSelection selection = system.actorSelection("/user/*");
            selection.tell(new StartMessage(peers), monitor);

        } catch (Exception ex) {
            System.out.println("Got a timeout waiting for reply from an actor");
            ex.printStackTrace();
        }
    }
}
