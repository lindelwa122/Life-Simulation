package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class BlockageLongRangeForward extends Neuron {
    private static final int RANGE = 10; // fixed long-range in tiles

    public BlockageLongRangeForward(Creature creature) {
        super(creature, "blockage_longrange_forward_external");
    }

    @Override
    public double value() {
        Coords prev = this.creature.getPreviousPosition();
        Coords curr = this.creature.getCurrentPosition();
        int dx = curr.x() - prev.x();
        int dy = curr.y() - prev.y();

        // If we have no heading information (prev == curr), treat as "not occupied" (0.0).
        if (dx == 0 && dy == 0) {
            return 0.0;
        }

        for (int d = 1; d <= RANGE; d++) {
            Coords probe = new Coords(curr.x() + dx * d, curr.y() + dy * d);
            boolean occupied = !this.creature.getWorld().canMoveCreature(this.creature, probe);
            if (occupied) {
                return 1.0; // occupied within range
            }
        }

        return 0.0; // nothing occupied within range
    }

}
