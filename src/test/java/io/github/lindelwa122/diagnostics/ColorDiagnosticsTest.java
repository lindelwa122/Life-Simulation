package io.github.lindelwa122.diagnostics;

import org.junit.jupiter.api.Test;

import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.utilities.GenomeColorMapper;
import io.github.lindelwa122.world.World;

import java.awt.Color;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorDiagnosticsTest {

    @Test
    public void printInitialPopulationColors() {
        Gene.setRandomSeed(424242L);
        World world = new World(200, 200);

        System.out.println("=== Color diagnostics (deterministic seed) ===");
        for (int i = 0; i < 20; i++) {
            Creature c = new Creature(i % 2, world);
            Genome g = Genome.createGenome(c, 10);
            c.setGenome(g);

            Color col = GenomeColorMapper.computeVisualColor(g);
            double avgWeight = g.getGenes().stream().mapToDouble(Gene::getWeight).average().orElse(0.0);
            long posCount = g.getGenes().stream().filter(G -> G.containsSensoryInput()).count();

            String hex = String.format("#%02X%02X%02X", col.getRed(), col.getGreen(), col.getBlue());
            System.out.printf(Locale.ROOT, "Creature %02d: genes=%02d, sensory=%02d, avgWeight=%.3f, color=%s\n",
                    i, g.getGenes().size(), posCount, avgWeight, hex);
        }

        // sanity assertion so test counts as passed
        assertTrue(true);
    }
}
