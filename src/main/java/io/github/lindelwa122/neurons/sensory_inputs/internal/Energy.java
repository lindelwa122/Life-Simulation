package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Energy extends Neuron {
    public Energy(Creature creature) {
        super(creature, "energy");
    } 

    @Override
    public double value() {
        return (double) this.creature.getEnergy() / 100;
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
