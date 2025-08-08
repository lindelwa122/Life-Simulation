package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class SetFear extends ActionNeuron {
    public SetFear(Creature creature) {
        super(creature, "set_fear_output");
    }

    @Override
    public void activate() {
        double value = this.value();
        this.creature.setFear((int) value * 100);
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
