package io.github.lindelwa122.neurons.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class InternalNeuron extends Neuron {
    List<Neuron> inputNeurons = new ArrayList<>();

    public InternalNeuron(Creature creature) {
        super(creature, "internal");
    }

    public void setInputs(Neuron ...neurons) {
        Collections.addAll(inputNeurons, neurons);
    }

    @Override
    public double value() {
        double sum = 0;

        for (Neuron neuron : inputNeurons) {
            sum += neuron.value();
        }

        return Math.tanh(sum);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
