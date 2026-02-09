package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PopulationSouth extends Neuron {
    private static final int RANGE_TILES = 20;
    private static final double MAX_WEIGHT;

    static {
        double s = 0.0;
        for (int d = 1; d <= RANGE_TILES; d++) s += 1.0 / d;
        MAX_WEIGHT = s;
    }

    public PopulationSouth(Creature creature) {
        super(creature, "population_south_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();

        double sum = 0.0;

        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;

            Coords oc = world.getCreatureCoords(other);
            int dx = oc.x() - curr.x();
            int dy = oc.y() - curr.y();

            double distTiles = Math.hypot(dx, dy) / (double) World.POINT_SIZE;
            if (distTiles > RANGE_TILES || distTiles < 0.0001) continue;

            // South sector: other is below (larger y) and roughly more vertical than horizontal
            if (dy > 0 && Math.abs(dy) >= Math.abs(dx)) {
                double weight = 1.0 / Math.max(1.0, distTiles);
                sum += weight;
            }
        }

        double normalized = Math.min(1.0, sum / MAX_WEIGHT);
        return normalized;
    }
}
