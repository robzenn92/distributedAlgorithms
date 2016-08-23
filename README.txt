
When you execute the jar file you may choose between:
- start the execution with the default configuration
- modify the number of the peers, but only with 2 peer will be created and will be evaluated the lattice
- modify the duration of the simulation 
- modify the delta time between one event and another of the same peer
- modify the probability of an internal event with respect to a message exchange.
- modify the probability that the peers variables will change.
- modify the label of the resulting lattice
- modify the predicate to be evaluated

When execution is started you can see the events and the exchange of messages between the peer. 
After a few seconds, in the "/out" folder you can find the lattice.dot and latticeWithVar.dot 
Run this command to create the image of the lattice          
		dot -Tpng lattice.dot > lattice.png  

And this command to create the image of the lattice  with the filled vertex where the predicate is true
		dot -Tpng latticeWithVar.dot > latticeWithVar.png 

At the end, you can change the predicate or modify the label of the latticeWithVar and recreate it with the previus command. 
