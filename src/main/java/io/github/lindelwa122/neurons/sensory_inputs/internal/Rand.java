package io.github.lindelwa122.neurons.sensory_inputs.internal;

import java.util.Random;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Rand extends Neuron {
    Random random = new Random();
    
    public Rand(Creature creature) {
        super(creature);
    }

    @Override
    public double value() {
        return this.random.nextDouble(1);
    }
}
