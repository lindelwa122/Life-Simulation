package io.github.lindelwa122.neurons.sensory_inputs.internal;

import java.util.Random;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Rand extends Neuron {
    Random random = new Random();
    
    public Rand(Creature creature) {
        super(creature, "rand_internal");
    }

    @Override
    public double value() {
        return this.random.nextDouble(1);
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
