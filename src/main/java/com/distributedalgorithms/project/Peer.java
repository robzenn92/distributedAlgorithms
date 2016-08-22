/**
 * Project for the "Distributed Algorithms" course
 * Academic Year: 2015/2016
 * Zen Roberto, Student ID: 171182.
 * Bof Michele, Student ID: NaN.
 */

package com.distributedalgorithms.project;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.BoundedMessageQueueSemantics;
import akka.dispatch.RequiresMessageQueue;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.distributedalgorithms.messages.EndMessage;
import com.distributedalgorithms.messages.Message;
import com.distributedalgorithms.messages.StartMessage;

import com.distributedalgorithms.options.Options;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Random;

/**
 * Description of the class: Peer
 * This class contains methods for handle the peer behaviour inside the network.
 */
class Peer extends UntypedActor implements RequiresMessageQueue<BoundedMessageQueueSemantics> {

    // The id of the peer and a reference to the monitor, in order to sent message to him.
    private int id;
    private final ActorRef monitor;

    // Define the start time and the end time of the simulation based on the local peer time.
    private long startTime;
    private long endTime;

    // The list of all the peers I can communicate with (that's why neighbours) except me.
    private ArrayList<ActorRef> neighbours = new ArrayList<ActorRef>(Options.getMAX_PEERS() - 1);
    private boolean ignoreNextStartMessages = false;

    private int variable;

    // My history of events.
    private ArrayList<Event> events = new ArrayList<Event>();

    // A simple log instance.
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private boolean blocked = false;

    // Constructor accepts the peer ID (the position he has in the list of peers stored by the monitor) and a
    // reference to the monitor.
    public Peer(int id, ActorRef monitor) {
        this.id = id;
        this.monitor = monitor;
    }

    public void onReceive(Object message) {

        // I received the first StartMessage either from the System or form another peer.
        if (message instanceof StartMessage && !ignoreNextStartMessages) {

            // I know that from now on I have to ignore the next StartMessages that have been sent from other peers.
            ignoreNextStartMessages = true;

            // Catch when the start message is delivered.
            this.startTime = Options.getCurrentTime();

            // Convert the duration time in Nanoseconds based both on the duration itself and the time unit
            // defined in the Option class.
            // As written here: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html
            // For example, to convert 10 minutes to milliseconds, use: TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)
            this.endTime = startTime + Options.getPRECISION_TIME_UNIT().convert(
                    ((StartMessage) message).getEndTime(),
                    ((StartMessage) message).getSimulationTimeUnits()
            );

            // Start up the fully connected graph network:
            // I received the list of all the peers in the network, I need to remove myself from the list
            this.neighbours = (ArrayList<ActorRef>) ((StartMessage) message).getPeers().clone();
            this.neighbours.remove(getSelf());

            Random rand = new Random();
            int start_value = rand.nextInt(Options.getMAX_INT());
            this.variable= start_value;

            // Set up my VectorClock to the default value.
            events.add(new Event(id, start_value));

            // Let the other peers the I am ready.
            // Common knowledge: let the other know that I can start the simulation.
            for(ActorRef neighbour:this.neighbours) {
                neighbour.tell(message, getSelf());
            }

            // Start a random execution:
            // Up to this point every peer in the system has executed the initialization: one local event just to set its Vector clock.
            // Now, I start sending asynchronous messages to random peers, till the simulation ends (currentTime = endTime).
            startRandomExecution();

        } else if (message instanceof Message) { // I received a Message from another peer in the Network.

            // Get the VectorClock of my last event
            VectorClock last = events.get(events.size() - 1).getVectorClock();

            // Update my VectorClock based on the previous one and the last VectorClock
            // of the peer which sent me this Message.
            VectorClock current = new VectorClock(id, last, ((Message) message).getVectorClock());
            modifyVar();

            // Raise a new local event with the current VectorClock, and add it to my history.
            Event e = new Event(id, current, this.variable);
            events.add(e);

            log.info("My last VC = " + last + ", Now my VC = " + current + ". I received message" + ((Message) message).getId() +
                    " with VC = " + ((Message) message).getVectorClock() + " from " + getSender().path());

        } else {

            unhandled(message);
        }
    }

    /**
     * Modifies the parameter value with a new random value between 0 and Options.MAX_INT based on the probability
     * defined in Options.PROB_CHANGE_VARIABLE.
     *
     *  depending on the given probability modify the peer's variable new value or the old one.
     */
    private void modifyVar() {

        // Get random real number between 0 and 1.
        // This will be the probability we will use to decide if the event
        // modify the variable of the peer or not.
        Random rand = new Random();
        float prob = rand.nextFloat();
        if (prob < Options.getPROB_CHANGE_VARIABLE()){
            rand = new Random();
            this.variable = rand.nextInt(Options.getMAX_INT());
        }
    }

    /**
     * Start sending asynchronous messages to random peers, till the simulation ends (currentTime = endTime).
     */
    private void startRandomExecution() {

        Random rand;            // Random generator
        long nextAction = 0;    // How long does it takes before the next action is executed
        long nearFuture;

        while (nextAction < (endTime - startTime)) {

            // Get random integer between 0 and DELTA_TIME.
            // This will be used to schedule an event that will be executed between the next 0 and DELTA_TIME DELTA_TIME_UNIT.
            // Values defined in the Options class.
            rand = new Random();
            nearFuture = rand.nextInt(Options.getDELTA_TIME());

            // The time scheduled is defined in term Options.DELTA_TIME and Options.DELTA_TIME_UNIT.
            // It might be the case that this time is defined in Options.SIMULATION_TIME_UNIT, convert it.
            // As written here: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html
            // For example, to convert 10 minutes to milliseconds, use: TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)
            if (Options.getDELTA_TIME_UNIT() != Options.getPRECISION_TIME_UNIT()) {
                nextAction += Options.getPRECISION_TIME_UNIT().convert(nearFuture, Options.getDELTA_TIME_UNIT());
            } else {
                nextAction += nearFuture;
            }

            // If the next action is scheduled after the end of the simulation,
            // exit from the loop and send the events history to the monitor.
            if (nextAction >= (endTime - startTime)) {
                break;
            }

            FiniteDuration future = FiniteDuration.create(nearFuture, Options.getDELTA_TIME_UNIT());

            // I schedule what to do (send message or execute internal event) in the next future.
            getContext().system().scheduler().scheduleOnce(
                    future,
                    new Runnable() {
                        public void run() {

                            try {

                                // Get random real number between 0 and 1.
                                // This will be the probability we will use to decide if the process
                                // has to execute an internal event rather than send a message to one of its neighbours.
                                Random rand = new Random();
                                float resulting_prob = rand.nextFloat();

                                // Whatever choice I made, a new event is executed and it VectorClock has to be updated.
                                VectorClock last = events.get(events.size() - 1).getVectorClock();
                                VectorClock current = new VectorClock(id, last);
                                modifyVar();
                                Event e = new Event(id, current, variable);

                                // As result, we have chosen to execute an internal event.
                                if (resulting_prob < Options.getPROB_INTERNAL_EVENT()) {

                                    // I just want to know remember that this was an internal event.
                                    e.setDescription("INTERNAL EVENT");
                                    log.info("My last VC = " + last + ", Now my VC = " + current + ". I executed an INTERNAL EVENT");

                                } else { // As result, we have chosen to send a message to one of our neighbours.

                                    // Chose randomly a recipient from the set of neighbours.
                                    rand = new Random();
                                    int recipientId = rand.nextInt(Options.getMAX_PEERS() - 1);
                                    ActorRef recipient = neighbours.get(recipientId);

                                    // The message is ready to be sent to selected recipient.
                                    e.setDescription("SEND EVENT");
                                    Message m = new Message(current);
                                    recipient.tell(m, getSelf());

                                    // The event is logged.
                                    log.info("My last VC = " + last + ", Now my VC = " +
                                            current + ". I sent message " + m.getId() + " to " + recipient.path());
                                }

                                // I append this last event to my history.
                                events.add(e);

                            } catch (Exception exc) {
                                exc.printStackTrace();
                            }
                        }
                    }, getContext().dispatcher()
            );
        }

        // After the computation I will send my event history to the monitor
        getContext().system().scheduler().scheduleOnce(Duration.create(nextAction, Options.getPRECISION_TIME_UNIT()),
                monitor, new EndMessage(id, events), getContext().dispatcher(), getSelf());
    }
}