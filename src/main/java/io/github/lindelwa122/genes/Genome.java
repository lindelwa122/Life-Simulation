package io.github.lindelwa122.genes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.neurons.internal.InternalNeuron;
import io.github.lindelwa122.neurons.output.ActionNeuron;

public class Genome {
    private List<Gene> genes = new ArrayList<>();
    private List<List<NeuralConnection>> layers;

    public Genome(Gene ...genes) {
        Collections.addAll(this.genes, genes);
    }

    public void addGene(Gene gene) {
        genes.add(gene);
    }

    public void addGenes(Gene ...genes) {
        Collections.addAll(this.genes, genes);
    }

    public List<Gene> getGenes() {
        return this.genes;
    }

    public int size() {
        return this.genes.size();
    }

    public boolean contains(Gene gene) {
        return this.genes.contains(gene);
    }

    public void formLayers() {
        this.layers = this.layerConnections();
    }

    public static Genome createGenome(Creature creature, int maxGenes) {
        Genome genome = new Genome();
        while (genome.size() < maxGenes) {
            for (int i = 0; i < maxGenes; i++) {
                Gene gene = Gene.createGene(creature, List.of(new InternalNeuron(creature)));
                if (genome.contains(gene)) continue;
                genome.addGene(gene);
            }
        }

        return genome;
    }

    public List<List<NeuralConnection>> layerConnections() {
        List<List<NeuralConnection>> layersList = new ArrayList<>();

        // Layer 0 will always contain input neurons (sensory inputs)
        List<NeuralConnection> layer0 = new ArrayList<>();

        for (Gene gene : this.genes) {
            if (gene.containsSensoryInput()) {
                Neuron sensoryInput = gene.getSource();
                Neuron sink = gene.getSink();
                double weight = gene.getWeight();

                layer0.add(new NeuralConnection(sensoryInput, weight, sink));
            }
        }

        if (layer0.isEmpty()) return layersList;

        layersList.add(layer0);

        while (true) {
            List<NeuralConnection> prevLayer = layersList.getLast();
            
            List<NeuralConnection> layer = new ArrayList<>();
            for (Gene gene : this.genes) {
                boolean dependsOnPrevLayer = prevLayer.stream().anyMatch(conn -> conn.sink() == gene.getSource());
                if (dependsOnPrevLayer) {
                    Neuron source = gene.getSource();
                    Neuron sink = gene.getSink();
                    double weight = gene.getWeight();

                    layer.add(new NeuralConnection(source, weight, sink));
                }
            }

            if (layer.isEmpty()) break;
        }

        return layersList;
    }

    private boolean isSensoryInput(Neuron n) {
        return !(n instanceof ActionNeuron) && !(n instanceof InternalNeuron);
    }

    private static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    public Map<ActionNeuron, Double> calculateOutputValues() {
        Map<ActionNeuron, Double> actionNeurons = new HashMap<>();
        List<Map<Neuron, Double>> calculatedValues = new ArrayList<>();

        for (List<NeuralConnection> layer : this.layers) {
            for (NeuralConnection conn : layer) {
                Neuron neuron = conn.neuron();

                if (this.isSensoryInput(neuron)) {
                    double value = neuron.value() * conn.weight();
                    Map<Neuron, Double> map = new HashMap<>();
                    map.put(conn.sink(), value);
                    calculatedValues.add(map);
                }

                else if (neuron instanceof ActionNeuron actionNeuron) {
                    List<Map<Neuron, Double>> inputs = calculatedValues.stream()
                        .filter(map -> {
                            for (Neuron n : map.keySet()) {
                                if (n.equals(actionNeuron)) return true;
                            }
                            return false;
                        })
                        .toList();

                    if (inputs.isEmpty()) continue;
                    
                    double sum = inputs.stream()
                    .flatMap(map -> map.values().stream())
                    .mapToDouble(Double::doubleValue)
                    .sum();
                    
                    double result = Math.tanh(sum) * conn.weight();
                    actionNeurons.put(actionNeuron, sigmoid(result));
                }
                
                else {
                    List<Map<Neuron, Double>> inputs = calculatedValues.stream()
                    .filter(map -> {
                        for (Neuron n : map.keySet()) {
                            if (n.equals(neuron)) return true;
                        }
                        return false;
                    })
                    .toList();
                    
                    if (inputs.isEmpty()) continue;

                    double sum = inputs.stream()
                        .flatMap(map -> map.values().stream())
                        .mapToDouble(Double::doubleValue)
                        .sum();

                    Map<Neuron, Double> map = new HashMap<>();
                    map.put(neuron, Math.tanh(sum) * conn.weight());
                    calculatedValues.add(map);
                }
            }
        }

        return actionNeurons;
    }

    public ActionNeuron getActionNeuronToBeActivated() {
        Map<ActionNeuron, Double> mappedPossibleOutputs = this.calculateOutputValues();

        Optional<Map.Entry<ActionNeuron, Double>> maxEntry = mappedPossibleOutputs.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntry.isPresent()) {
            return maxEntry.get().getKey();
        }
        else {
            return null;
        }
    }

    public void activateActionNeuron() {
        this.getActionNeuronToBeActivated().activate();
    }
}
