package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class BlockageNorth extends Neuron {
    public BlockageNorth(Creature creature) {
        super(creature, "blockage_north_external");
    }

    @Override
    public double value() {
        Coords current = this.creature.getCurrentPosition();
        Coords northCoords = new Coords(current.x(), current.y() - (1 * World.POINT_SIZE));

        World world = this.creature.getWorld();
        boolean isNotOccupied = world.canMoveCreature(creature, northCoords);

        if (isNotOccupied) return 0; else return 1;
    }

}

