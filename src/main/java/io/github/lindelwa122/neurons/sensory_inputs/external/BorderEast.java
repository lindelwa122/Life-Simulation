package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class BorderEast extends Neuron {
    public BorderEast(Creature creature) {
        super(creature, "border_east_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();

        int xTile = curr.x() / World.POINT_SIZE;
        int maxXTile = world.getWidth() / World.POINT_SIZE - 1;
        if (maxXTile <= 0) return 1.0;

        double normalized = (double) xTile / (double) maxXTile;
        if (normalized < 0) normalized = 0.0;
        if (normalized > 1) normalized = 1.0;
        return normalized;
    }
}
