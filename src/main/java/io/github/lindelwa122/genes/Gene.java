package io.github.lindelwa122.genes;

import io.github.lindelwa122.neurons.Neuron;

public class Gene {
    private int sourceType;
    private Neuron source;
    private int sinkType;
    private Neuron sink;
    private double weight;

    public Gene(int sourceType, Neuron source, int sinkType, Neuron sink, double weight) {
        this.sourceType = sourceType;
        this.source = source;
        this.sinkType = sinkType;
        this.sink = sink;
        this.weight = weight;
    }
    
    
}
