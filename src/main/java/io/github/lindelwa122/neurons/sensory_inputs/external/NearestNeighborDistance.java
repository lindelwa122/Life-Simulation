package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class NearestNeighborDistance extends Neuron {
    private static final int MAX_CHECK_RADIUS = 50; // in tiles

    public NearestNeighborDistance(Creature creature) {
        super(creature, "nearest_neighbor_distance");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords myPos = this.creature.getCurrentPosition();
        double best = Double.MAX_VALUE;

        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;
            Coords oPos = other.getCurrentPosition();
            double dx = oPos.x() - myPos.x();
            double dy = oPos.y() - myPos.y();
            double dist = Math.sqrt(dx*dx + dy*dy);
            if (dist < best) best = dist;
        }

        if (best == Double.MAX_VALUE) return 1.0; // no neighbors -> far

        // Normalize distance to [0,1] where 1.0 = very close (zero distance), 0.0 = far (> max)
        double maxDist = Math.sqrt(Math.pow(world.getWidth(), 2) + Math.pow(world.getHeight(), 2));
        double normalized = 1.0 - Math.min(best / maxDist, 1.0);
        return normalized;
    }
}
