package io.github.lindelwa122.neurons.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class InternalNeuron extends Neuron {
    List<Neuron> inputNeurons = new ArrayList<>();

    protected InternalNeuron(Creature creature) {
        super(creature);
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

}
