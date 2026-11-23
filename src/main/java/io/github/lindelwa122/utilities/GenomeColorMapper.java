package io.github.lindelwa122.utilities;

import java.awt.Color;
import java.util.List;

import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.neurons.internal.InternalNeuron;

public class GenomeColorMapper {
    private static final double WEIGHT_MAX = 4.0; // weights are generated in [-4, 4]
    private static final Color DEFAULT_COLOR = new Color(128, 128, 128); // fallback gray

    public static Color computeVisualColor(Genome genome) {
        if (genome == null || genome.getGenes().isEmpty()) {
            return DEFAULT_COLOR;
        }

        List<Gene> genes = genome.getGenes();

        // Compute simple statistics
        double avgAbsWeight = genes.stream().mapToDouble(g -> Math.abs(g.getWeight())).average().orElse(0.0) / WEIGHT_MAX; // [0,1]
        double avgWeight = genes.stream().mapToDouble(Gene::getWeight).average().orElse(0.0) / WEIGHT_MAX; // [-1,1]
        double variance = 0.0;
        double mean = genes.stream().mapToDouble(Gene::getWeight).average().orElse(0.0);
        for (Gene g : genes) {
            double d = g.getWeight() - mean;
            variance += d * d;
        }
        variance = genes.size() > 0 ? Math.sqrt(variance / genes.size()) / WEIGHT_MAX : 0.0; // normalized stddev [0,1]

        long internalCount = genes.stream().filter(g -> (g.getSource() instanceof InternalNeuron) || (g.getSink() instanceof InternalNeuron)).count();
        double internalFraction = (double) internalCount / (double) genes.size();

        // Stable hash for hue
        int hash = Math.abs(genome.toString().hashCode());
        float hue = (hash % 360) / 360.0f;

        // Slightly rotate hue based on sign of avg weight and internal fraction
        hue = (hue + (float) (avgWeight * 0.1) + (float) (internalFraction * 0.05)) % 1.0f;

        // Saturation boosted by variance and avgAbsWeight, clamp to [0.4, 1.0]
        float saturation = (float) Math.min(1.0, 0.4 + variance * 0.6 + avgAbsWeight * 0.3);

        // Brightness based on avgAbsWeight and variance so strong genomes are brighter
        float brightness = (float) Math.min(1.0, 0.55 + avgAbsWeight * 0.35 + variance * 0.1);

        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return new Color(rgb);
    }

    // legacy helpers removed — new mapping is HSB-based and more distinctive
    
}
