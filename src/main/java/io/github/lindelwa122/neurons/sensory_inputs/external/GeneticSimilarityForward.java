package io.github.lindelwa122.neurons.sensory_inputs.external;

import java.util.HashSet;
import java.util.Set;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.world.World;

public class GeneticSimilarityForward extends Neuron {
    private static final int RANGE_TILES = 40; // max range to search for forward neighbor
    private static final double COS45 = Math.cos(Math.PI / 4.0);

    public GeneticSimilarityForward(Creature creature) {
        super(creature, "genetic_similarity_forward_external");
    }

    @Override
    public double value() {
        World world = this.creature.getWorld();
        Coords curr = this.creature.getCurrentPosition();
        Coords prev = this.creature.getPreviousPosition();

        int hx = curr.x() - prev.x();
        int hy = curr.y() - prev.y();
        double hlen = Math.hypot(hx, hy);
        if (hlen < 0.0001) return 0.0; // no heading -> no similarity

        // Find nearest creature in forward direction within cone
        Creature nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Creature other : world.getCreaturesList()) {
            if (other == this.creature) continue;

            Coords oc = world.getCreatureCoords(other);
            int vx = oc.x() - curr.x();
            int vy = oc.y() - curr.y();
            double vlen = Math.hypot(vx, vy);
            double distTiles = vlen / (double) World.POINT_SIZE;

            if (distTiles > RANGE_TILES || vlen < 0.0001) continue;

            // Check if in forward cone (45° forward)
            double cosAngle = (vx * hx + vy * hy) / (vlen * hlen);
            if (cosAngle >= COS45) {
                if (distTiles < nearestDist) {
                    nearest = other;
                    nearestDist = distTiles;
                }
            }
        }

        // If no neighbor found, return 0
        if (nearest == null) return 0.0;

        // Compare genomes: compute Jaccard similarity of genes
        Genome myGenome = this.creature.getGenome();
        Genome otherGenome = nearest.getGenome();

        if (myGenome == null || otherGenome == null) return 0.0;

        double similarity = computeGeneticSimilarity(myGenome, otherGenome);
        return similarity;
    }

    private double computeGeneticSimilarity(Genome g1, Genome g2) {
        // Compute Jaccard similarity: count of matching genes / total unique genes
        // Match based on source and sink neuron IDs

        Set<String> g1GeneIds = new HashSet<>();
        Set<String> g2GeneIds = new HashSet<>();

        for (Gene gene : g1.getGenes()) {
            Neuron source = gene.getSource();
            Neuron sink = gene.getSink();
            String sourceId = source != null ? source.toString() : "null";
            String sinkId = sink != null ? sink.toString() : "null";
            g1GeneIds.add(sourceId + "_" + sinkId);
        }

        for (Gene gene : g2.getGenes()) {
            Neuron source = gene.getSource();
            Neuron sink = gene.getSink();
            String sourceId = source != null ? source.toString() : "null";
            String sinkId = sink != null ? sink.toString() : "null";
            g2GeneIds.add(sourceId + "_" + sinkId);
        }

        // Compute intersection and union
        Set<String> intersection = new HashSet<>(g1GeneIds);
        intersection.retainAll(g2GeneIds);

        Set<String> union = new HashSet<>(g1GeneIds);
        union.addAll(g2GeneIds);

        if (union.isEmpty()) return 0.0;

        return (double) intersection.size() / (double) union.size();
    }
}
