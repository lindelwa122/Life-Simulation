package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PheromoneForward extends Neuron {
    private static final int RANGE_TILES = 20;
    private static final double MAX_WEIGHT;
    private static final double COS45 = Math.cos(Math.PI / 4.0);

    static {
        double s = 0.0;
        for (int d = 1; d <= RANGE_TILES; d++) s += 1.0 / d;
        MAX_WEIGHT = s;
    }

    public PheromoneForward(Creature creature) {
        super(creature, "pheromone_forward_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        Coords prev = this.creature.getPreviousPosition();

        int hx = curr.x() - prev.x();
        int hy = curr.y() - prev.y();
        double hlen = Math.hypot(hx, hy);
        if (hlen < 0.0001) return 0.0; // no heading

        int currTileX = curr.x() / World.POINT_SIZE;
        int currTileY = curr.y() / World.POINT_SIZE;

        double sum = 0.0;

        // Scan forward cone (45° forward)
        for (int dy = 1; dy <= RANGE_TILES; dy++) {
            for (int dx = -dy; dx <= dy; dx++) {
                int checkTileX = currTileX + dx;
                int checkTileY = currTileY + dy;

                double vlen = Math.hypot(dx, dy);
                double cosAngle = (dx * hx + dy * hy) / (vlen * hlen);
                if (cosAngle >= COS45) {
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
