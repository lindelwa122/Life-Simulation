package io.github.lindelwa122.genes;

import io.github.lindelwa122.neurons.Neuron;

public record NeuralConnection(Neuron neuron, double weight, Neuron sink) {

}
