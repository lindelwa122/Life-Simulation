package io.github.lindelwa122.neurons.sensory_inputs.internal;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class LastMovementX extends Neuron {
    public LastMovementX(Creature creature) {
        super(creature, "last_movement_x_internal");
    }

    @Override
    public double value() {
        return (double) this.creature.getCurrentPosition().x() / 
            this.creature.getWorldWidth();
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
