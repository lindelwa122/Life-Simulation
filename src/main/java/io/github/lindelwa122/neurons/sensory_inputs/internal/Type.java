package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class Type extends Neuron {
    public Type(Creature creature) {
        super(creature, "type_internal");
    }

    @Override
    public double value() {
        return this.creature.getType().element() == FundamentalElements.CARNYXIS 
            ? 1 
            : 0;
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
