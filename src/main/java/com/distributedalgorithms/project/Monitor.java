package com.distributedalgorithms.project;

import akka.actor.UntypedActor;

/**
 * Created by Roberto on 30/06/16.
 */
public class Monitor extends UntypedActor {

    public WaitForGraph wfg = new WaitForGraph();

    public void onReceive(Object message) {

        // Send the current greeting back to the sender
        if (message instanceof Message) {
//            System.out.println("I am a client! I sent a message! ");
//            getSender().tell(new Message(), getSelf());
        }
        else unhandled(message);
    }
}