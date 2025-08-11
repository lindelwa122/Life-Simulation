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

    private List<Object> convertToObjects(List<Coords> coords) {
        List<Object> objects = new ArrayList<>();
        for (Coords coord : coords) objects.add(coord);
        return objects;
    }

    @Override
    public void activate() {
        List<Coords> possibleNextCoords = new ArrayList<>();
        Coords cur = this.creature.getCurrentPosition();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                possibleNextCoords.add(new Coords(cur.x()+(dx*World.POINT_SIZE), cur.y()+(dy*World.POINT_SIZE)));
            }
        }

        Coords nextPosition = (Coords) Utilities.pickRandom(this.convertToObjects(possibleNextCoords));
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
