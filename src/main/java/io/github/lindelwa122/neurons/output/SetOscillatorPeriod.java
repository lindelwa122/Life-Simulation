package io.github.lindelwa122.neurons.output;

import java.util.List;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.utilities.Utilities;

public class SetOscillatorPeriod extends ActionNeuron {
    protected SetOscillatorPeriod(Creature creature) {
        super(creature);
    }

    @Override
    public void activate() {
        int newOscillatorPeriod = (int) Utilities.pickRandom(List.of(10, 15, 30, 50));
        this.creature.setOscillatorPeriod(newOscillatorPeriod);
    }
}
