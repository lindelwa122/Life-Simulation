package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;

public class KillForwardNeighbor extends ActionNeuron {
    public KillForwardNeighbor(Creature creature) {
        super(creature, "kill_forward_neighbor_output");
    }

    @Override
    public void activate() {
        Coords currentPosition = this.creature.getCurrentPosition();
        Coords previousPosition = this.creature.getPreviousPosition();
        int dx = currentPosition.x() - previousPosition.x();
        int dy = currentPosition.y() - previousPosition.y();
        Coords targetPosition = new Coords(currentPosition.x() + dx, currentPosition.y() + dy);
        
        World world = this.creature.getWorld();
        world.killCreatureAt(targetPosition);

    }

}
