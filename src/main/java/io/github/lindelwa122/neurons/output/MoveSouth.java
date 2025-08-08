package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveSouth extends ActionNeuron {
    protected MoveSouth(Creature creature) {
        super(creature);
    }

    @Override
    public void activate() {
        Coords current = this.creature.getCurrentPosition();
        Coords newPosition = new Coords(current.x(), current.y()+1);

        World world = this.creature.getWorld();
        world.moveCreature(creature, newPosition);
    }
}
