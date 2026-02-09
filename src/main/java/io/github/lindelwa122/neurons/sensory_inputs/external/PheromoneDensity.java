package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PheromoneDensity extends Neuron {
    private static final int RANGE_TILES = 20;
    private static final double MAX_PHEROMONE = 100.0; // normalization cap

    public PheromoneDensity(Creature creature) {
        super(creature, "pheromone_density_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        int currTileX = curr.x() / World.POINT_SIZE;
        int currTileY = curr.y() / World.POINT_SIZE;

        double sum = 0.0;
        int count = 0;

        // Sample all tiles within RANGE_TILES
        for (int dx = -RANGE_TILES; dx <= RANGE_TILES; dx++) {
            for (int dy = -RANGE_TILES; dy <= RANGE_TILES; dy++) {
                double distTiles = Math.hypot(dx, dy);
                if (distTiles <= RANGE_TILES) {
                    Coords tileCoords = new Coords(currTileX + dx, currTileY + dy);
                    double pheromone = world.getPheromoneLevel(tileCoords);
                    sum += pheromone;
                    count++;
                }
            }
        }

        if (count == 0) return 0.0;

        double average = sum / (double) count;
        double normalized = Math.min(1.0, average / MAX_PHEROMONE);
        return normalized;
    }
}
