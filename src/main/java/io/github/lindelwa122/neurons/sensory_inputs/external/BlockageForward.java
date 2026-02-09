package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;

public class BlockageForward extends Neuron {
    public BlockageForward(Creature creature) {
        super(creature, "blockage_forward_external");
    }

    @Override
    public double value() {
        Coords prevPosition = this.creature.getPreviousPosition();
        Coords currentPosition = this.creature.getCurrentPosition();
        int deltaX = currentPosition.x() - prevPosition.x();
        int deltaY = currentPosition.y() - prevPosition.y();
        Coords forwardCoords = new Coords(currentPosition.x() + deltaX, currentPosition.y() + deltaY);

        boolean isNotOccupied = this.creature.getWorld().canMoveCreature(this.creature, forwardCoords);
        return isNotOccupied ? 0.0 : 1.0;
    }

}
