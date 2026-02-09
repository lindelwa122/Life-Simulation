package io.github.lindelwa122.simulation;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.Climate;
import io.github.lindelwa122.world.World;

public record CreationOfNewborn(World world, Creature parent1, Creature parent2, SimulationConfig config, Climate birthRegion) {

}
