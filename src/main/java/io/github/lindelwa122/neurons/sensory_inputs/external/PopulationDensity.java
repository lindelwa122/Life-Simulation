package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PopulationDensity extends Neuron {
    private static final int RANGE_TILES = 20;
    private static final double MAX_COUNT = Math.PI * RANGE_TILES * RANGE_TILES;

    public PopulationDensity(Creature creature) {
        super(creature, "population_density_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();

        double count = 0.0;
        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;

            Coords oc = world.getCreatureCoords(other);
            double distTiles = Math.hypot(oc.x() - curr.x(), oc.y() - curr.y()) / (double) World.POINT_SIZE;
            if (distTiles <= RANGE_TILES) count += 1.0;
        }

        double normalized = Math.min(1.0, count / MAX_COUNT);
        return normalized; // 0.0 = empty area, 1.0 = very dense
    }
}
