package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class PopulationLongRangeForward extends Neuron {
    private static final int RANGE_TILES = 40; // long-range
    private static final double MAX_WEIGHT;
    private static final double COS45 = Math.cos(Math.PI / 4.0);

    static {
        double s = 0.0;
        for (int d = 1; d <= RANGE_TILES; d++) s += 1.0 / d;
        MAX_WEIGHT = s;
    }

    public PopulationLongRangeForward(Creature creature) {
        super(creature, "population_longrange_forward_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        Coords prev = this.creature.getPreviousPosition();

        int hx = curr.x() - prev.x();
        int hy = curr.y() - prev.y();
        double hlen = Math.hypot(hx, hy);
        if (hlen < 0.0001) return 0.0;

        double sum = 0.0;

        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;

            Coords oc = world.getCreatureCoords(other);
            int vx = oc.x() - curr.x();
            int vy = oc.y() - curr.y();
            double vlen = Math.hypot(vx, vy);
            double distTiles = vlen / (double) World.POINT_SIZE;
            if (distTiles > RANGE_TILES || vlen < 0.0001) continue;

            double cosAngle = (vx * hx + vy * hy) / (vlen * hlen);
            if (cosAngle >= COS45) {
                double weight = 1.0 / Math.max(1.0, distTiles);
                sum += weight;
            }
        }

        double normalized = Math.min(1.0, sum / MAX_WEIGHT);
        return normalized;
    }
}
