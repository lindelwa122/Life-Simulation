package io.github.lindelwa122.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.neurons.Neuron;

public class Brain {
    public static List<Neuron> topologicalSort(Genome genome) {
        Map<Neuron, List<Neuron>> graph = new HashMap<>();
        Map<Neuron, Integer> inDegree = new HashMap<>();

        for (Gene gene : genome.getGenes()) {
            Neuron source = gene.getSource();
            Neuron target = gene.getSink();
            
            // Add edge to the graph
            graph.computeIfAbsent(source, k -> new ArrayList<>()).add(target);

            // Update in-degree count
            inDegree.put(target, inDegree.getOrDefault(target, 0) + 1);

            // Ensure source neuron is in the in-degree map
            inDegree.putIfAbsent(source, inDegree.getOrDefault(source, 0));
        }

        // Initialize the queue with neurons that have in-degree 0
        Queue<Neuron> queue = new LinkedList<>();
        for (Map.Entry<Neuron, Integer>  entry: inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Neuron> sorted = new ArrayList<>();

        while (!queue.isEmpty()) {
            Neuron current = queue.poll();
            sorted.add(current);

            for (Neuron neighbour : graph.getOrDefault(current, new ArrayList<>())) {
                int degree = inDegree.get(neighbour) - 1;
                if (degree == 0) {
                    queue.add(neighbour);
                }
            }
        }

        // Detect cycle
        if (sorted.size() != inDegree.size()) {
            throw new IllegalStateException("Cycle detected in neural graph. Can't do topological sort");
        }

        return sorted;
    }
}
