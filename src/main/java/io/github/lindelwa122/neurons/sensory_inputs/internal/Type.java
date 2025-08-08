package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Type extends Neuron {
    public Type(Creature creature) {
        super(creature);
    }

    @Override
    public double value() {
        return this.creature.getType().element() == FundamentalElements.CARNYXIS 
            ? 1 
            : 0;
    }
}
