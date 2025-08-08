package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Gender extends Neuron {
    public Gender(Creature creature) {
        super(creature, "gender_internal");
    }

    @Override
    public double value() {
        return this.creature.getGender();
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
