package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Fear extends Neuron {
    public Fear(Creature creature) {
        super(creature);
    }
    
    @Override
    public double value() {
        return (double) this.creature.getFear() / 100;
    }
}
