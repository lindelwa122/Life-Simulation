package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveBack extends ActionNeuron {
    public MoveBack(Creature creature) {
        super(creature, "move_back_output");
    }

    @Override
    public void activate() {
        Coords previousPosition = this.creature.getPreviousPosition();
        World world = this.creature.getWorld();
        world.moveCreature(creature, previousPosition); // Returns moved value
    }
}
