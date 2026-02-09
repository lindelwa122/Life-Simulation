package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class MoveForward extends ActionNeuron {
    public MoveForward(Creature creature) {
        super(creature, "move_forward_output");
    }

    @Override
    public void activate() {
        Coords previous = this.creature.getPreviousPosition();
        Coords current = this.creature.getCurrentPosition();

        int diffX = current.x() - previous.x();
        int diffY = current.y() - previous.y();

        Coords newPosition = new Coords(current.x()+diffX, current.y()+diffY);
        World world = this.creature.getWorld();
        world.moveCreature(creature, newPosition);
    }
}
