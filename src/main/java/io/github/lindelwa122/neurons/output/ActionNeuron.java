package io.github.lindelwa122.neurons.output;

import java.util.Collections;
import java.util.List;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public abstract class ActionNeuron extends Neuron {
    protected List<Neuron> inputNeurons;

    protected ActionNeuron(Creature creature) {
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

    public abstract void activate();
}
