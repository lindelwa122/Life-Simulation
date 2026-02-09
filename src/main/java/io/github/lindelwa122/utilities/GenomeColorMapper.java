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

        double sumR = 0.0, sumG = 0.0, sumB = 0.0;
        double totalWeight = 0.0;

        List<Gene> genes = genome.getGenes();
        for (Gene gene : genes) {
            double weight = gene.getWeight();
            double normWeight = Math.min(1.0, Math.abs(weight) / WEIGHT_MAX);

            Neuron source = gene.getSource();
            Neuron sink = gene.getSink();

            // Determine roles: source -> red, sink -> blue, internal -> green
            double r = getRoleRed(source) * normWeight;
            double g = getRoleGreen(source, sink) * normWeight;
            double b = getRoleBlue(sink) * normWeight;

            // Accumulate weighted by norm weight
            sumR += r * normWeight;
            sumG += g * normWeight;
            sumB += b * normWeight;
            totalWeight += normWeight;
        }

        // Normalize by total weight
        if (totalWeight > 0.0) {
            double finalR = sumR / totalWeight;
            double finalG = sumG / totalWeight;
            double finalB = sumB / totalWeight;

            return applyVisibilityConstraints(finalR, finalG, finalB);
        }

        return DEFAULT_COLOR;
    }

    private static double getRoleRed(Neuron neuron) {
        // Source contribution is always red, regardless of type
        return 1.0;
    }

    private static double getBlueG(Neuron neuron) {
        // Sink contribution is always blue, regardless of type
        return 1.0;
    }

    private static double getRoleGreen(Neuron source, Neuron sink) {
        // Green if either source or sink is internal
        if (source instanceof InternalNeuron || sink instanceof InternalNeuron) {
            return 1.0;
        }
        return 0.0;
    }

    private static double getRoleBlue(Neuron neuron) {
        // Sink contribution is always blue, regardless of type
        return 1.0;
    }

    private static Color applyVisibilityConstraints(double r, double g, double b) {
        // Convert to HSB for saturation/brightness boost
        float[] hsb = Color.RGBtoHSB(
            (int) Math.round(r * 255),
            (int) Math.round(g * 255),
            (int) Math.round(b * 255),
            null
        );

        // Enforce minimum saturation and brightness
        hsb[1] = Math.max(hsb[1], 0.45f); // saturation >= 0.45
        hsb[2] = Math.max(hsb[2], 0.45f); // brightness >= 0.45

        // Convert back to RGB
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return new Color(rgb);
    }
}
