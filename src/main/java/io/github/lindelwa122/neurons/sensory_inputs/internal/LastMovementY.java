package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class LastMovementY extends Neuron {
    public LastMovementY(Creature creature) {
        super(creature, "last_movement_y_internal");
    }

    @Override
    public double value() {
        return (double) this.creature.getCurrentPosition().y() / 
            this.creature.getWorldHeight();
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
