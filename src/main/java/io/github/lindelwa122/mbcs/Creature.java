package io.github.lindelwa122.mbcs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class Creature {
    private FundamentalElements type;

    private Hydration hydration = new Hydration(new InternalValue());
    private Hunger hunger = new Hunger(new InternalValue());
    private Age age = new Age(new InternalValue());
    private Libido libido = new Libido(new InternalValue());
    private Fear fear = new Fear(new InternalValue());
    private Energy energy = new Energy(new InternalValue(100));
    private Health health = new Health(new InternalValue(100));

    private int oscillator = Utilities.random(1);
    private int oscillatorPeriod = (int) Utilities.pickRandom(List.of(10, 15, 30, 50));

    private Coords previousPosition;
    private Coords currentPosition;
    private World world;
    private int gender;

    private Genome genome;

    public Creature(int gender, World world) {
        this.type = (FundamentalElements) Utilities.pickRandom(List.of(
                    FundamentalElements.CARNYXIS,
                    FundamentalElements.PREDONIX
                ));
        this.world = world;
        this.gender = gender;
    }

    public static boolean birthCreature(World world, Creature parent1, Creature parent2) {
        int gender = Utilities.random(2);
        Creature c = new Creature(gender, world);
        
        // Create genes
        int randIndex = Utilities.random(65);
        Genome genome = new Genome();

        List<Gene> genePool1 = parent1.getGenome().getGenes().subList(0, randIndex);
        List<Gene> genePool2 = parent2.getGenome().getGenes().subList(randIndex, 64);

        genePool1.addAll(genePool2);
        for (Gene gene : genePool1) {
            gene.resetCreature(c);
            genome.addGene(gene);
        }
        genome.formLayers();

        c.setGenome(genome);
        boolean added = world.addCreature(c);

        if (added) {
            Coords currentPosition = world.getCreatureCoords(c);
            c.currentPosition = currentPosition;
            c.previousPosition = currentPosition;
            return true;
        }

        return false;
    }

    public static boolean birthCreature(World world) {
        int gender = Utilities.random(2);

        Creature c = new Creature(gender, world);
        c.setGenome(Genome.createGenome(c, 64));
        boolean added = world.addCreature(c);

        if (added) {
            Coords currentPosition = world.getCreatureCoords(c);
            c.currentPosition = currentPosition;
            c.previousPosition = currentPosition;
            return true;
        }

        return false;
    }

    public void recordBrain() {
        String projectRoot = System.getProperty("user.dir");
        Path path = Path.of(projectRoot, "src", "main", "java", "io", "github", "lindelwa122", "brain", "brain-scan.txt");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(path.toString()), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PrintWriter writer = new PrintWriter(fileOutputStream);
        writer.println(genome.toString());
        writer.close();
    }

    public void nextSimulationRound() {
        this.age.value().increment();
        this.energy.value().decrementByValue(5);
        this.hunger.value().incrementByValue(5);
        this.genome.activateActionNeuron();
    }


    public void paint(Graphics g, Coords coords) {
        g.setColor(Color.RED);
        g.fillOval(coords.x(), coords.y(), World.POINT_SIZE, World.POINT_SIZE);
    }

    // GETTERS
    public int getHydrationLevel() {
        return this.hydration.value().getValue();
    }

    public int getHungerLevel() {
        return this.hunger.value().getValue();
    }

    public int getOscillator() {
        return this.oscillator;
    }

    public int getAge() {
        return this.age.value().getValue();
    }

    public Coords getCurrentPosition() {
        return this.currentPosition;
    }

    public Coords getPreviousPosition() {
        return this.previousPosition;
    }

    public int getWorldHeight() {
        return this.world.getHeight() / World.POINT_SIZE;
    }

    public int getWorldWidth() {
        return this.world.getWidth() / World.POINT_SIZE;
    }

    public FundamentalElements getType() {
        return this.type;
    }

    public int getLibido() {
        return this.libido.value().getValue();
    }

    public int getFear() {
        return this.fear.value().getValue();
    }

    public int getEnergy() {
        return this.energy.value().getValue();
    }

    public int getHealth() {
        return this.health.value().getValue();
    }

    public int getGender() {
        return this.gender;
    }

    public World getWorld() {
        return this.world;
    }

    public Genome getGenome() {
        return this.genome;
    }

    public int getOscillatorPeriod() {
        return this.oscillatorPeriod;
    }

    // SETTERS
    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public void setOscillatorPeriod(int period) {
        this.oscillatorPeriod = period;
    }

    public void setLibido(int libido) {
        this.libido.value().setValue(libido);
    }

    public void setFear(int fear) {
        this.fear.value().setValue(fear);
    }

    public void setCurrentPosition(Coords position) {
        this.previousPosition = currentPosition;
        this.currentPosition = position;
    }

    public void setPreviousPosition(Coords position) {
        this.previousPosition = position;
    }
}
