package io.github.lindelwa122.genes;

import java.util.List;
import java.util.Random;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.neurons.internal.InternalNeuron;
import io.github.lindelwa122.neurons.output.MoveBack;
import io.github.lindelwa122.neurons.output.MoveEast;
import io.github.lindelwa122.neurons.output.MoveForward;
import io.github.lindelwa122.neurons.output.MoveNorth;
import io.github.lindelwa122.neurons.output.MoveRandom;
import io.github.lindelwa122.neurons.output.MoveSouth;
import io.github.lindelwa122.neurons.output.MoveWest;
import io.github.lindelwa122.neurons.output.SetFear;
import io.github.lindelwa122.neurons.output.SetLibido;
import io.github.lindelwa122.neurons.output.SetOscillatorPeriod;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Age;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Energy;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Fear;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Gender;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Hunger;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Hydration;
import io.github.lindelwa122.neurons.sensory_inputs.internal.LastMovementX;
import io.github.lindelwa122.neurons.sensory_inputs.internal.LastMovementY;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Libido;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Oscillator;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Rand;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Type;
import io.github.lindelwa122.utilities.Utilities;

public class Gene {
    private int sourceType;
    private Neuron source;
    private int sinkType;
    private Neuron sink;
    private double weight;

    private static Random random = new Random();

    public Gene(int sourceType, Neuron source, int sinkType, Neuron sink, double weight) {
        this.sourceType = sourceType;
        this.source = source;
        this.sinkType = sinkType;
        this.sink = sink;
        this.weight = weight;
    }

    public static List<Neuron> getSensoryNeurons(Creature creature) {
        return List.of(
            new Age(creature), 
            new Energy(creature), 
            new Fear(creature), 
            new Gender(creature), 
            new Hunger(creature), 
            new Hydration(creature), 
            new LastMovementX(creature), 
            new LastMovementY(creature), 
            new Libido(creature),
            new Oscillator(creature), 
            new Rand(creature), 
            new Type(creature)
        );
    }

    public static List<Neuron> getOutputNeurons(Creature creature) {
        return List.of(
           new MoveBack(creature),
           new MoveEast(creature),
           new MoveForward(creature),
           new MoveNorth(creature),
           new MoveRandom(creature),
           new MoveSouth(creature),
           new MoveWest(creature),
           new SetFear(creature),
           new SetLibido(creature),
           new SetOscillatorPeriod(creature)
        );
    }
    
    public static Gene createGene(Creature creature, List<InternalNeuron> internalNeurons) {
        int sourceType = Utilities.random(1);

        Neuron source;
        if (sourceType == 0) {
            source = (Neuron) Utilities.pickRandom(List.of(internalNeurons));
        } 
        else {
            source = (Neuron) Utilities.pickRandom(List.of(getSensoryNeurons(creature)));
        }

        int sinkType = Utilities.random(1);
        Neuron sink;
        if (sinkType == 0) {
            sink = (Neuron) Utilities.pickRandom(List.of(internalNeurons));
        }
        else {
            sink = (Neuron) Utilities.pickRandom(List.of(getOutputNeurons(creature)));
        }

        double weight = random.nextDouble(-4, 4);

        return new Gene(sourceType, source, sinkType, sink, weight);
    }
}
