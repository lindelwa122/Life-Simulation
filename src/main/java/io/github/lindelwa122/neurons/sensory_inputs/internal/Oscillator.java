package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Oscillator extends Neuron {
    public Oscillator(Creature creature) {
        super(creature, "oscillator_internal");
    }

    @Override
    public double value() {
        return this.creature.getOscillator();
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
