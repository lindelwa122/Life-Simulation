package io.github.lindelwa122.neurons.sensory_inputs.external;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.Climate;
import io.github.lindelwa122.world.World;

public class ClimateOnPosition extends Neuron {
    public ClimateOnPosition(Creature creature) {
        super(creature, "climate_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        Climate climate = world.getClimateOnCoords(curr);
        
        return switch (climate) {
            case COLD -> 0.2;
            case DRY -> 0.4;
            case HOT -> 0.6;
            case WET -> 0.8;
        };
    }
}
