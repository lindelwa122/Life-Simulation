package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class SetLibido extends ActionNeuron {
    protected SetLibido(Creature creature) {
        super(creature);
    }

    @Override
    public void activate() {
        double value = this.value();
        this.creature.setLibido((int) value * 100);
    }
}
