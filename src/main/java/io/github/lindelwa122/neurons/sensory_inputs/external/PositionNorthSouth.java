package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PositionNorthSouth extends Neuron {
    public PositionNorthSouth(Creature creature) {
        super(creature, "position_northsouth_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();

        int yTile = curr.y() / World.POINT_SIZE;
        int maxYTile = world.getHeight() / World.POINT_SIZE - 1;
        if (maxYTile <= 0) return 1.0;

        // 1.0 at northernmost (yTile == 0), 0.0 at southernmost (yTile == maxYTile)
        double normalized = 1.0 - ((double) yTile / (double) maxYTile);
        if (normalized < 0.0) normalized = 0.0;
        if (normalized > 1.0) normalized = 1.0;
        return normalized;
    }
}
