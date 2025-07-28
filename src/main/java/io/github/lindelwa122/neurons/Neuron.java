package io.github.lindelwa122.neurons;

import io.github.lindelwa122.mbcs.Creature;

public abstract class Neuron {
    protected Creature creature;

    protected Neuron(Creature creature) {
        this.creature = creature;
    }

    public abstract double value();
}
