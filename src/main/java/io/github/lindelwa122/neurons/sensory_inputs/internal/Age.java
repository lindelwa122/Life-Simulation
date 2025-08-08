package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Age extends Neuron {
    public Age(Creature creature) {
        super(creature, "age_internal");
    }

    @Override
    public double value() {
        return (double) this.creature.getAge() / 100;
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
