package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveWest extends ActionNeuron {
    public MoveWest(Creature creature) {
        super(creature, "move_west_output");
    }

    public void activate() {
        Coords current = this.creature.getCurrentPosition();
        Coords newPosition = new Coords(current.x()+(1*World.POINT_SIZE), current.y());

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
