package com.distributedalgorithms.project;

import com.distributedalgorithms.options.Options;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import scala.collection.mutable.Set;

class ProcessVertex {

    private String name = "";

    public ProcessVertex(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

public class WaitForGraph {

    DirectedGraph<ProcessVertex, DefaultEdge> wfg = new DefaultDirectedGraph<ProcessVertex, DefaultEdge>(DefaultEdge.class);

    public WaitForGraph() {
        for (int i = 0; i < Options.MAX_ACTORS; i++) {
            ProcessVertex v = new ProcessVertex("Process" + String.valueOf(i));
            wfg.addVertex(v);
        }
    }

    public DefaultEdge update(ProcessVertex source, ProcessVertex destination, boolean waiting) {
        return waiting ? wfg.addEdge(source, destination) : wfg.removeEdge(source, destination);
    }

    public boolean hasCycles() {
        CycleDetector detector = new CycleDetector(this.wfg);
        return detector.detectCycles();
    }

    public Set<ProcessVertex> detectCycle() {
        CycleDetector detector = new CycleDetector(this.wfg);
        return (Set<ProcessVertex>) detector.findCycles();
    }

    @Override
    public String toString() {
        return "List of vertexes: " + wfg.vertexSet() + "\nList of edges: " + wfg.edgeSet();
    }
}
