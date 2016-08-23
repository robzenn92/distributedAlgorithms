# README

Just run 'java -jar project.jar'

When you execute the jar file you can choose between:
- start the simulation with the default configurations
- modify the number of the peers. Pay attention, only with 2 peers the lattice will be created and evaluated.
- modify the duration of the simulation.
- modify the delta time between one event and another for all the peers
- modify the probability of an internal event with respect to a message exchange.
- modify the probability that the peers variables will change.
- modify the labels of the resulting lattice
- modify the predicate to be evaluated

When execution is started you can see the events and the exchange of messages between the peer.
At the end of the simulation, in the "out" folder you will find the lattice.dot and latticeWithVar.dot
Run this command to create the image of the lattice
		dot -Tpng lattice.dot > lattice.png

And this command to create the image of the lattice with the filled vertex where the predicate is true
		dot -Tpng latticeWithVar.dot > latticeWithVar.png

When the simulation ends, you can always change the predicate or modify the label of the latticeWithVar and recreate it without running a new simulation.

If you want to recompile it you can also use ant commands and maven in order to download all the dependencies.