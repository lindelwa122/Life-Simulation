package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveWest extends ActionNeuron {
    protected MoveWest(Creature creature) {
        super(creature);
    }

    public void activate() {
        Coords current = this.creature.getCurrentPosition();
        Coords newPosition = new Coords(current.x()+1, current.y());

        World world = this.creature.getWorld();
        world.moveCreature(creature, newPosition);
    }
}
