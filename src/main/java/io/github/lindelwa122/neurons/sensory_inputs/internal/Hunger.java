package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Hunger extends Neuron {
    public Hunger(Creature creature) {
        super(creature, "hunger_internal");
    }

    @Override
    public double value() {
        return (double) this.creature.getHungerLevel() / 100;
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
