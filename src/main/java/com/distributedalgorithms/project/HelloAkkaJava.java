package com.distributedalgorithms.project;

import akka.actor.*;
import com.distributedalgorithms.options.Options;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Created by Roberto on 23/06/16.
 */
public class HelloAkkaJava {

    /**
     * Client Class
     */
    public static class Client extends UntypedActor {

        public void onReceive(Object message) {

            // Send the current greeting back to the sender
            if (message instanceof Message) {
                System.out.println("I am a client! I sent a message! ");
                getSender().tell(new Message(), getSelf());
            } else unhandled(message);
        }
    }

    /**
     * Server Class
     */
    public static class Server extends UntypedActor {

        public void onReceive(Object message) {
            if (message instanceof Message) {
                System.out.println("I am a server! I got this message: ");
                System.out.println(((Message) message).getData());
            }
        }
    }

    public static void main(String[] args) {

        int numberOfActors = Options.MAX_ACTORS;
        ArrayList<ActorRef> clients = new ArrayList<ActorRef>(numberOfActors);
        ArrayList<ActorRef> servers = new ArrayList<ActorRef>(numberOfActors);

        try {

            // Create the 'GOD' actor system
            final ActorSystem system = ActorSystem.create("GOD");

            // Populate array of processes
            for (int i = 0; i < numberOfActors; i++) {
                clients.add(system.actorOf(Props.create(Client.class), "c" + String.valueOf(i + 1)));
                servers.add(system.actorOf(Props.create(Monitor.class), "s" + String.valueOf(i + 1)));
            }

            system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), clients.get(0), new Message(), system.dispatcher(), servers.get(0));

        } catch (Exception ex) {
            System.out.println("Got a timeout waiting for reply from an actor");
            ex.printStackTrace();
        }
    }
}
