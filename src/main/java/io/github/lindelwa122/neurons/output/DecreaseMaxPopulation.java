package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class DecreaseMaxPopulation extends ActionNeuron {
    public DecreaseMaxPopulation(Creature creature) {
        super(creature, "decrease_max_population_output");
    }

    @Override
    public void activate() {
        this.creature.getWorld().decreaseMaxPopulation();
    }

}
