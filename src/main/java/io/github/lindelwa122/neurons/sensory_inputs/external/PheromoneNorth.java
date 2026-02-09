package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PheromoneNorth extends Neuron {
    private static final int RANGE_TILES = 20;
    private static final double MAX_WEIGHT;

    static {
        double s = 0.0;
        for (int d = 1; d <= RANGE_TILES; d++) s += 1.0 / d;
        MAX_WEIGHT = s;
    }

    public PheromoneNorth(Creature creature) {
        super(creature, "pheromone_north_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        int currTileX = curr.x() / World.POINT_SIZE;
        int currTileY = curr.y() / World.POINT_SIZE;

        double sum = 0.0;

        // Scan tiles in north sector (above, dy < 0)
        for (int dy = 1; dy <= RANGE_TILES; dy++) {
            for (int dx = -dy; dx <= dy; dx++) {
                int checkTileX = currTileX + dx;
                int checkTileY = currTileY - dy;

                // Check if in valid bounds and more vertical than horizontal for north
                if (Math.abs(dy) >= Math.abs(dx)) {
                    Coords tileCoords = new Coords(checkTileX, checkTileY);
                    double pheromone = world.getPheromoneLevel(tileCoords);
                    double distTiles = Math.hypot(dx, dy);
                    double weight = 1.0 / Math.max(1.0, distTiles);
                    sum += pheromone * weight;
                }
            }
        }

        double normalized = Math.min(1.0, sum / MAX_WEIGHT);
        return normalized;
    }
}
