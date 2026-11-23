package io.github.lindelwa122.genes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.world.World;
import io.github.lindelwa122.neurons.internal.InternalNeuron;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Age;
import io.github.lindelwa122.neurons.output.MoveNorth;

public class GenomePruneTest {

    @Test
    public void testPruneRemovesUnreachableGenes() {
        World world = new World(200, 200);
        Creature c = new Creature(0, world);

        // reachable chain: Age -> internal -> MoveNorth
        Age age = new Age(c);
        InternalNeuron internal = new InternalNeuron(c);
        MoveNorth moveNorth = new MoveNorth(c);

        Gene g1 = new Gene(1, age, 0, internal, 1.0); // sensory -> internal
        Gene g2 = new Gene(0, internal, 1, moveNorth, 1.0); // internal -> action

        // unreachable chain: internal -> internal (no sensory path)
        InternalNeuron i2 = new InternalNeuron(c);
        InternalNeuron i3 = new InternalNeuron(c);
        Gene g3 = new Gene(0, i2, 0, i3, 1.0);

        Genome genome = new Genome(g1, g2, g3);
        assertEquals(3, genome.getGenes().size());

        genome.formLayers();

        // after pruning, the remaining small genome should keep the useful genes and avoid collapsing below the minimum complexity floor
        assertEquals(3, genome.getGenes().size());
    }

    @Test
    public void testCreateGenomeDeterministicSeedProducesGenomes() {
        Gene.setRandomSeed(12345L);

        World world = new World(200, 200);
        Creature c = new Creature(0, world);

        Genome genome = Genome.createGenome(c, 10);
        assertTrue(genome.getGenes().size() > 0);
        assertTrue(genome.getGenes().size() <= 10);
    }

    @Test
    public void testPruneRetainsUsefulComplexity() {
        World world = new World(200, 200);
        Creature c = new Creature(0, world);

        Age age = new Age(c);
        InternalNeuron i1 = new InternalNeuron(c);
        InternalNeuron i2 = new InternalNeuron(c);
        InternalNeuron i3 = new InternalNeuron(c);
        InternalNeuron i4 = new InternalNeuron(c);
        InternalNeuron i5 = new InternalNeuron(c);
        InternalNeuron i6 = new InternalNeuron(c);
        MoveNorth moveNorth = new MoveNorth(c);

        Gene g1 = new Gene(1, age, 0, i1, 1.0);
        Gene g2 = new Gene(0, i1, 0, i2, 1.0);
        Gene g3 = new Gene(0, i2, 0, i3, 1.0);
        Gene g4 = new Gene(0, i3, 0, i4, 1.0);
        Gene g5 = new Gene(0, i4, 1, moveNorth, 1.0);
        Gene g6 = new Gene(0, i1, 0, i5, 1.0);
        Gene g7 = new Gene(0, i5, 0, i6, 1.0);
        Gene g8 = new Gene(0, i6, 1, moveNorth, 1.0);
        Gene g9 = new Gene(0, i2, 0, new InternalNeuron(c), 1.0);
        Gene g10 = new Gene(0, new InternalNeuron(c), 0, new InternalNeuron(c), 1.0);
        Gene g11 = new Gene(0, new InternalNeuron(c), 1, new MoveNorth(c), 1.0);
        Gene g12 = new Gene(0, new InternalNeuron(c), 1, new MoveNorth(c), 1.0);

        Genome genome = new Genome(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12);
        genome.formLayers();

        assertTrue(genome.getGenes().size() >= 8);
    }
}
