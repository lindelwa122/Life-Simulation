package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class SetLibido extends ActionNeuron {
    public SetLibido(Creature creature) {
        super(creature, "set_libido");
    }

    @Override
    public void activate() {
        double value = this.value();
        this.creature.setLibido((int) value * 100);
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
