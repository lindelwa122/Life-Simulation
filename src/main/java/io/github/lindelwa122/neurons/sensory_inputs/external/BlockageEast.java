package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class BlockageEast extends Neuron {
    public BlockageEast(Creature creature) {
        super(creature, "blockage_east_external");
    }

    @Override
    public double value() {
        Coords current = this.creature.getCurrentPosition();
        Coords eastCoords = new Coords(current.x()-(1*World.POINT_SIZE), current.y());

        World world = this.creature.getWorld();
        boolean isNotOccupied = world.canMoveCreature(creature, eastCoords);

        if (isNotOccupied) return 0; else return 1;
    }
    
}
