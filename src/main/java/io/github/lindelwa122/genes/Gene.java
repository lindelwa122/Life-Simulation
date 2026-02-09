package io.github.lindelwa122.genes;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.neurons.Neuron;
import io.github.lindelwa122.neurons.internal.InternalNeuron;
import io.github.lindelwa122.neurons.output.DecreaseMaxPopulation;
import io.github.lindelwa122.neurons.output.EmitPheromone;
import io.github.lindelwa122.neurons.output.IncreaseMaxPopulation;
import io.github.lindelwa122.neurons.output.KillForwardNeighbor;
import io.github.lindelwa122.neurons.output.MoveBack;
import io.github.lindelwa122.neurons.output.MoveEast;
import io.github.lindelwa122.neurons.output.MoveForward;
import io.github.lindelwa122.neurons.output.MoveNorth;
import io.github.lindelwa122.neurons.output.MoveRandom;
import io.github.lindelwa122.neurons.output.MoveSouth;
import io.github.lindelwa122.neurons.output.MoveWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.BlockageEast;
import io.github.lindelwa122.neurons.sensory_inputs.external.BlockageLongRangeForward;
import io.github.lindelwa122.neurons.sensory_inputs.external.BlockageNorth;
import io.github.lindelwa122.neurons.sensory_inputs.external.BlockageSouth;
import io.github.lindelwa122.neurons.sensory_inputs.external.BlockageWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.BorderEast;
import io.github.lindelwa122.neurons.sensory_inputs.external.BorderNorth;
import io.github.lindelwa122.neurons.sensory_inputs.external.BorderSouth;
import io.github.lindelwa122.neurons.sensory_inputs.external.BorderWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.ClimateOnPosition;
import io.github.lindelwa122.neurons.sensory_inputs.external.GeneticSimilarityForward;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneDensity;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneEast;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneForward;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneNorth;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneSouth;
import io.github.lindelwa122.neurons.sensory_inputs.external.PheromoneWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationDensity;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationEast;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationForward;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationLongRangeForward;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationNorth;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationSouth;
import io.github.lindelwa122.neurons.sensory_inputs.external.PopulationWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.PositionEastWest;
import io.github.lindelwa122.neurons.sensory_inputs.external.PositionNorthSouth;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Age;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Energy;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Gender;
import io.github.lindelwa122.neurons.sensory_inputs.internal.LastMovementX;
import io.github.lindelwa122.neurons.sensory_inputs.internal.LastMovementY;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Oscillator;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Rand;
import io.github.lindelwa122.neurons.sensory_inputs.internal.Type;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.Climate;

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

    public Neuron getSource() {
        return this.source;
    }

    public Neuron getSink() {
        return this.sink;
    }

    public double getWeight() {
        return this.weight;
    }

    public void mutate(double mutationRate) {
        // mutate weight by small random amount
        if (Math.random() < mutationRate) {
            double delta = (Math.random() - 0.5) * 2.0; // -1.0 to 1.0
            this.weight += delta * 0.5; // scale mutation
            // clamp weight to reasonable range
            this.weight = Math.max(-4.0, Math.min(4.0, this.weight));
        }
    }

    public boolean containsSensoryInput() {
        return this.sourceType == 1;
    }

    public void resetCreature(Creature creature) {
        this.source.resetCreature(creature);
        this.sink.resetCreature(creature);
    }

    public Gene cloneForCreature(Creature creature) {
        Neuron newSource;
        Neuron newSink;

        if (this.source instanceof InternalNeuron) {
            newSource = new InternalNeuron(creature);
        } else {
            // match sensory input type by class
            if (this.source instanceof Age) newSource = new Age(creature);
            else if (this.source instanceof Energy) newSource = new Energy(creature);
            else if (this.source instanceof Gender) newSource = new Gender(creature);
            else if (this.source instanceof LastMovementX) newSource = new LastMovementX(creature);
            else if (this.source instanceof LastMovementY) newSource = new LastMovementY(creature);
            else if (this.source instanceof Oscillator) newSource = new Oscillator(creature);
            else if (this.source instanceof Rand) newSource = new Rand(creature);
            else if (this.source instanceof Type) newSource = new Type(creature);
            else if (this.source instanceof BlockageEast) newSource = new BlockageEast(creature);
            else if (this.source instanceof BlockageWest) newSource = new BlockageWest(creature);
            else if (this.source instanceof BlockageNorth) newSource = new BlockageNorth(creature);
            else if (this.source instanceof BlockageSouth) newSource = new BlockageSouth(creature);
            else if (this.source instanceof BlockageLongRangeForward) newSource = new BlockageLongRangeForward(creature);
            else if (this.source instanceof BorderNorth) newSource = new BorderNorth(creature);
            else if (this.source instanceof BorderSouth) newSource = new BorderSouth(creature);
            else if (this.source instanceof BorderEast) newSource = new BorderEast(creature);
            else if (this.source instanceof BorderWest) newSource = new BorderWest(creature);
            else if (this.source instanceof PopulationNorth) newSource = new PopulationNorth(creature);
            else if (this.source instanceof PopulationSouth) newSource = new PopulationSouth(creature);
            else if (this.source instanceof PopulationEast) newSource = new PopulationEast(creature);
            else if (this.source instanceof PopulationWest) newSource = new PopulationWest(creature);
            else if (this.source instanceof PopulationForward) newSource = new PopulationForward(creature);
            else if (this.source instanceof PopulationLongRangeForward) newSource = new PopulationLongRangeForward(creature);
            else if (this.source instanceof PopulationDensity) newSource = new PopulationDensity(creature);
            else if (this.source instanceof PositionEastWest) newSource = new PositionEastWest(creature);
            else if (this.source instanceof PositionNorthSouth) newSource = new PositionNorthSouth(creature);
            else if (this.source instanceof GeneticSimilarityForward) newSource = new GeneticSimilarityForward(creature);
            else if (this.source instanceof PheromoneNorth) newSource = new PheromoneNorth(creature);
            else if (this.source instanceof PheromoneSouth) newSource = new PheromoneSouth(creature);
            else if (this.source instanceof PheromoneEast) newSource = new PheromoneEast(creature);
            else if (this.source instanceof PheromoneWest) newSource = new PheromoneWest(creature);
            else if (this.source instanceof PheromoneForward) newSource = new PheromoneForward(creature);
            else if (this.source instanceof PheromoneDensity) newSource = new PheromoneDensity(creature);
            else if (this.source instanceof ClimateOnPosition) newSource = new ClimateOnPosition(creature);
            else newSource = (Neuron) Utilities.pickRandom(convertToObjectList(getSensoryNeurons(creature)));
        }

        if (this.sink instanceof InternalNeuron) {
            newSink = new InternalNeuron(creature);
        } else {
            if (this.sink instanceof MoveBack) newSink = new MoveBack(creature);
            else if (this.sink instanceof MoveEast) newSink = new MoveEast(creature);
            else if (this.sink instanceof MoveForward) newSink = new MoveForward(creature);
            else if (this.sink instanceof MoveNorth) newSink = new MoveNorth(creature);
            else if (this.sink instanceof MoveRandom) newSink = new MoveRandom(creature);
            else if (this.sink instanceof MoveSouth) newSink = new MoveSouth(creature);
            else if (this.sink instanceof MoveWest) newSink = new MoveWest(creature);
            else if (this.sink instanceof EmitPheromone) newSink = new EmitPheromone(creature);
            else if (this.sink instanceof KillForwardNeighbor) newSink = new KillForwardNeighbor(creature);
            else if (this.sink instanceof IncreaseMaxPopulation) newSink = new IncreaseMaxPopulation(creature);
            else if (this.sink instanceof DecreaseMaxPopulation) newSink = new DecreaseMaxPopulation(creature);
            else newSink = (Neuron) Utilities.pickRandom(convertToObjectList(getOutputNeurons(creature)));
        }

        return new Gene(this.sourceType, newSource, this.sinkType, newSink, this.weight);
    }

    public static List<Neuron> getSensoryNeurons(Creature creature) {
        return List.of(
            new Age(creature), 
            new Gender(creature), 
            new LastMovementX(creature), 
            new LastMovementY(creature), 
            new Oscillator(creature), 
            new Rand(creature), 
            new BlockageEast(creature),
            new BlockageWest(creature),
            new BlockageNorth(creature),
            new BlockageSouth(creature),
            new BlockageLongRangeForward(creature),
            new BorderNorth(creature),
            new BorderSouth(creature),
            new BorderEast(creature),
            new BorderWest(creature),
            new PopulationNorth(creature),
            new PopulationSouth(creature),
            new PopulationEast(creature),
            new PopulationWest(creature),
            new PopulationForward(creature),
            new PopulationLongRangeForward(creature),
            new PopulationDensity(creature),
            new PositionEastWest(creature),
            new PositionNorthSouth(creature),
            new GeneticSimilarityForward(creature),
            new PheromoneNorth(creature),
            new PheromoneSouth(creature),
            new PheromoneEast(creature),
            new PheromoneWest(creature),
            new PheromoneForward(creature),
            new PheromoneDensity(creature),
            new ClimateOnPosition(creature)
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
           new EmitPheromone(creature),
           new KillForwardNeighbor(creature)
        //    new IncreaseMaxPopulation(creature),
        //    new DecreaseMaxPopulation(creature)
        );
    }

    private static List<Object> convertToObjectList(List<Neuron> neurons) {
        List<Object> objects = new ArrayList<>();
        for (Neuron neuron : neurons) objects.add(neuron);
        return objects;
    }
    
    public static Gene createGene(Creature creature, List<InternalNeuron> internalNeurons) {
        int sourceType = Utilities.random(2);

        Neuron source;
        if (sourceType == 0) {
            source = internalNeurons.getFirst();
        } 
        else {
            source = (Neuron) Utilities.pickRandom(convertToObjectList(getSensoryNeurons(creature)));
        }

        int sinkType = sourceType == 0 ? 1 : Utilities.random(2);
        Neuron sink;
        if (sinkType == 0) {
            sink = internalNeurons.getFirst();
        }
        else {
            sink = (Neuron) Utilities.pickRandom(convertToObjectList(getOutputNeurons(creature)));
        }

        double weight = random.nextDouble(-4, 4);

        return new Gene(sourceType, source, sinkType, sink, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Gene gene = (Gene) obj;
        return Objects.equals(source, gene.source) && Objects.equals(sink, gene.sink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.source, this.sink);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} -> {1}", this.source, this.sink);
    }
}
