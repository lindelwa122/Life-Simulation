package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class SetFear extends ActionNeuron {
    protected SetFear(Creature creature) {
        super(creature);
    }

    @Override
    public void activate() {
        double value = this.value();
        this.creature.setFear((int) value * 100);
    }
}
