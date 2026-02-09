package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.mbcs.Creature;

public class IncreaseMaxPopulation extends ActionNeuron {
    public IncreaseMaxPopulation(Creature creature) {
        super(creature, "increase_max_population_output");
    }

    @Override
    public void activate() {
        this.creature.getWorld().increaseMaxPopulation();
    }

}
