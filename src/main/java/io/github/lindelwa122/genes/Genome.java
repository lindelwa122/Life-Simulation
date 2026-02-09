package io.github.lindelwa122.genes;

import java.text.MessageFormat;
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
    
    // Optimized structures for fast forward pass
    private Map<Neuron, Integer> neuronToIndex;
    private Neuron[] indexToNeuron;
    private int numNeurons;
    private int numSensory;
    private int numAction;
    private List<ActionNeuron> actionNeuronList;
    
    // Pre-computed connection structure
    private int[][][] connectionStructure; // [layer][connection][sourceIdx, sinkIdx]
    private double[][] connectionWeights;   // [layer][connection]
    private boolean structureBuilt = false;

    public Genome(Gene ...genes) {
        Collections.addAll(this.genes, genes);
    }

    public void removeGene() {
        if (this.genes.size() > 1) {
            int idx = (int) (Math.random() * this.genes.size());
            this.genes.remove(idx);
            structureBuilt = false; // Need to rebuild
        }
    }

    public void addGene(Creature creature) {
        Gene newGene = Gene.createGene(creature, List.of(new InternalNeuron(creature)));
        if (!this.genes.contains(newGene)) {
            this.genes.add(newGene);
            structureBuilt = false; // Need to rebuild
        }
    }

    public void addGenes(Gene ...genes) {
        Collections.addAll(this.genes, genes);
        structureBuilt = false; // Need to rebuild
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
        buildOptimizedStructure();
    }

    public static Genome createGenome(Creature creature, int maxGenes) {
        Genome genome = new Genome();
        while (genome.size() < maxGenes) {
            for (int i = 0; i < maxGenes; i++) {
                Gene gene = Gene.createGene(creature, List.of(new InternalNeuron(creature)));
                if (genome.contains(gene)) continue;
                genome.addGenes(gene);
            }
        }

        genome.formLayers();
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
            layersList.add(layer);
        }

        return layersList;
    }
    
    private void buildOptimizedStructure() {
        if (layers == null || layers.isEmpty()) return;
        
        // Build neuron index mapping
        neuronToIndex = new HashMap<>();
        List<Neuron> allNeurons = new ArrayList<>();
        numSensory = 0;
        numAction = 0;
        actionNeuronList = new ArrayList<>();
        
        // Collect all unique neurons
        for (List<NeuralConnection> layer : layers) {
            for (NeuralConnection conn : layer) {
                Neuron source = conn.neuron();
                Neuron sink = conn.sink();
                
                if (!neuronToIndex.containsKey(source)) {
                    neuronToIndex.put(source, allNeurons.size());
                    allNeurons.add(source);
                    if (isSensoryInput(source)) numSensory++;
                }
                
                if (!neuronToIndex.containsKey(sink)) {
                    neuronToIndex.put(sink, allNeurons.size());
                    allNeurons.add(sink);
                    if (sink instanceof ActionNeuron) {
                        numAction++;
                        actionNeuronList.add((ActionNeuron) sink);
                    }
                }
            }
        }
        
        numNeurons = allNeurons.size();
        indexToNeuron = allNeurons.toArray(Neuron[]::new);
        
        // Build connection structure
        connectionStructure = new int[layers.size()][][];
        connectionWeights = new double[layers.size()][];
        
        for (int layerIdx = 0; layerIdx < layers.size(); layerIdx++) {
            List<NeuralConnection> layer = layers.get(layerIdx);
            int numConnections = layer.size();
            
            connectionStructure[layerIdx] = new int[numConnections][2];
            connectionWeights[layerIdx] = new double[numConnections];
            
            for (int connIdx = 0; connIdx < numConnections; connIdx++) {
                NeuralConnection conn = layer.get(connIdx);
                int sourceIdx = neuronToIndex.get(conn.neuron());
                int sinkIdx = neuronToIndex.get(conn.sink());
                
                connectionStructure[layerIdx][connIdx][0] = sourceIdx;
                connectionStructure[layerIdx][connIdx][1] = sinkIdx;
                connectionWeights[layerIdx][connIdx] = conn.weight();
            }
        }
        
        structureBuilt = true;
    }

    private boolean isSensoryInput(Neuron n) {
        return !(n instanceof ActionNeuron) && !(n instanceof InternalNeuron);
    }

    private static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    public Map<ActionNeuron, Double> calculateOutputValues() {
        if (!structureBuilt) {
            buildOptimizedStructure();
        }
        
        if (numNeurons == 0) return new HashMap<>();
        
        // Activations array
        double[] activations = new double[numNeurons];
        
        // Load sensory inputs
        for (int i = 0; i < numNeurons; i++) {
            Neuron neuron = indexToNeuron[i];
            if (isSensoryInput(neuron)) {
                activations[i] = neuron.value();
            }
        }
        
        // Forward propagate through layers
        for (int layerIdx = 0; layerIdx < connectionStructure.length; layerIdx++) {
            int[][] connections = connectionStructure[layerIdx];
            double[] weights = connectionWeights[layerIdx];
            
            // Accumulate weighted inputs
            for (int connIdx = 0; connIdx < connections.length; connIdx++) {
                int sourceIdx = connections[connIdx][0];
                int sinkIdx = connections[connIdx][1];
                double weight = weights[connIdx];
                
                activations[sinkIdx] += activations[sourceIdx] * weight;
            }
            
            // Apply activation function to neurons that received input in this layer
            // (only to sinks in this layer that aren't sensory inputs)
            for (int connIdx = 0; connIdx < connections.length; connIdx++) {
                int sinkIdx = connections[connIdx][1];
                Neuron sinkNeuron = indexToNeuron[sinkIdx];
                
                if (!isSensoryInput(sinkNeuron)) {
                    // Apply tanh only once per neuron per layer
                    // We need to track which neurons we've already activated
                    // For simplicity, we'll apply it in next step
                }
            }
        }
        
        // Apply tanh to all non-sensory neurons
        for (int i = 0; i < numNeurons; i++) {
            if (!isSensoryInput(indexToNeuron[i])) {
                activations[i] = Math.tanh(activations[i]);
            }
        }
        
        // Extract action neuron outputs
        Map<ActionNeuron, Double> outputs = new HashMap<>(numAction);
        for (ActionNeuron actionNeuron : actionNeuronList) {
            int idx = neuronToIndex.get(actionNeuron);
            double value = sigmoid(activations[idx]);
            outputs.put(actionNeuron, value);
        }
        
        return outputs;
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
        ActionNeuron neuron = this.getActionNeuronToBeActivated();
        if (neuron != null) neuron.activate();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (Gene gene : this.genes) {
            output.append(MessageFormat.format("{0} -> {1}", gene.getSource(), gene.getSink()));
            output.append(" | ");
        }

        return output.toString();
    }
}
