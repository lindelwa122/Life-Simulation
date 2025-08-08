package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveEast extends ActionNeuron {
    public MoveEast(Creature creature) {
        super(creature, "move_east_output");
    }

    @Override
    public void activate() {
        Coords current = this.creature.getCurrentPosition();
        Coords newPosition = new Coords(current.x()-1, current.y());

        World world = this.creature.getWorld();
        world.moveCreature(creature, newPosition);
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
