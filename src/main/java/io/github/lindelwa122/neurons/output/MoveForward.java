package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveForward extends ActionNeuron {
    public MoveForward(Creature creature) {
        super(creature, "move_forward_output");
    }

    public void activate() {
        Coords previous = this.creature.getPreviousPosition();
        Coords current = this.creature.getCurrentPosition();

        int diffX = current.x() - previous.x();
        int diffY = current.y() - previous.y();

        Coords newPosition = new Coords(current.x()+(diffX*World.POINT_SIZE), current.y()+(diffY*World.POINT_SIZE));
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
