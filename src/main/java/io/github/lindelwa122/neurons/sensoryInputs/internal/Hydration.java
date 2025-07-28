package io.github.lindelwa122.neurons.sensoryInputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Hydration extends Neuron {
    public Hydration(Creature creature) {
        super(creature);
    }

    @Override
    public double value() {
        return (double) this.creature.getHydrationLevel() / 100;
    }
}
