package io.github.lindelwa122.neurons.output;

import java.util.ArrayList;
import java.util.List;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class MoveRandom extends ActionNeuron {
    public MoveRandom(Creature creature) {
        super(creature, "move_random_output");
    }

    @Override
    public void activate() {
        List<Coords> possibleNextCoords = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                possibleNextCoords.add(new Coords(dx, dy));
            }
        }

        Coords nextPosition = (Coords) Utilities.pickRandom(List.of(possibleNextCoords));
        World world = this.creature.getWorld();
        world.moveCreature(creature, nextPosition);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
