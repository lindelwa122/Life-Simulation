package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class NeighborCount extends Neuron {
    private static final int RADIUS_PIXELS = World.POINT_SIZE * 3; // count neighbors within ~3 tiles
    private static final int MAX_NORMALIZED = 8; // cap for normalization

    public NeighborCount(Creature creature) {
        super(creature, "neighbor_count");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords myPos = this.creature.getCurrentPosition();
        int count = 0;

        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;
            Coords oPos = other.getCurrentPosition();
            double dx = oPos.x() - myPos.x();
            double dy = oPos.y() - myPos.y();
            double distSq = dx*dx + dy*dy;
            if (distSq <= RADIUS_PIXELS * RADIUS_PIXELS) count++;
        }

        double normalized = Math.min((double) count / (double) MAX_NORMALIZED, 1.0);
        return normalized;
    }
}
