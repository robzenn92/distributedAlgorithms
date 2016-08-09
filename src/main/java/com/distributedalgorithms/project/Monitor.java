package com.distributedalgorithms.project;

import akka.actor.UntypedActor;

/**
 * Created by Roberto on 30/06/16.
 */
class Monitor extends UntypedActor {

    private WaitForGraph wfg = new WaitForGraph();

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof Message) {
            System.out.println("I am the monitor! I received a message from " + getSender());
            getSender().tell(new Message(new VectorClock(0)), getSelf());
        }
        else if (message instanceof HelloAkkaJava.EndMessage) {
            System.out.println(getSender() + " finished.");
        }
        else {
            unhandled(message);
        }
    }
}