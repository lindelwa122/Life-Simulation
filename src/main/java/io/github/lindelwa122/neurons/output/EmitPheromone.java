package io.github.lindelwa122.neurons.output;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.world.World;

public class EmitPheromone extends ActionNeuron {
    public EmitPheromone(io.github.lindelwa122.mbcs.Creature creature) {
        super(creature, "emit_pheromone_output");
    }

    @Override
    public void activate() {
        World world = this.creature.getWorld();
        Coords creaturePos = this.creature.getCurrentPosition();
        Coords tileCoordsPerTile = new Coords(creaturePos.x() / World.POINT_SIZE, creaturePos.y() / World.POINT_SIZE);
        world.addPheromone(tileCoordsPerTile, World.PHEROMONE_EMISSION);
    }
}
