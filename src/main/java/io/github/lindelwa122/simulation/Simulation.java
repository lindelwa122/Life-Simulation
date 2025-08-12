package io.github.lindelwa122.simulation;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import io.github.lindelwa122.cellularStructure.CellularMakeUp;
import io.github.lindelwa122.cellularStructure.DietaryOptions;
import io.github.lindelwa122.cellularStructure.Element;
import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.genes.Gene;
import io.github.lindelwa122.genes.Genome;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class Simulation extends JPanel {
    private static final int WORLD_HEIGHT = 800;
    private static final int WORLD_WIDTH = 800;

    private static World world = new World(WORLD_HEIGHT, WORLD_WIDTH);

    public Simulation() {
        for (int i = 0; i < 100; i++) {
            Creature.birthCreature(world);
        }
        world.doCreatureBrainScan();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        world.paintWorld(g);
    }

    public void nextSimulationRound(int gen) {
        world.nextSimulationRound(gen);
        this.repaint();
    }

    public List<Object> convertToObjects(List<Creature> creatures) {
        List<Object> objects = new ArrayList<>();
        for (Creature creature : creatures) objects.add(creature);
        return objects;
    }

    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation();

        JFrame frame = new JFrame("Life Simulation");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WORLD_WIDTH + 200, WORLD_HEIGHT + 200);
        frame.add(simulation);
        frame.setVisible(true);
        
        for (int gen = 0; gen < 100; gen++) {
            frame.setTitle("Generation " + gen);

            for (int round = 0; round < 100; round++) {
                simulation.nextSimulationRound(gen);
                Thread.sleep(100);
            }

            List<Creature> toKill = new ArrayList<>();

            // Remove first half
            Map<Creature, Coords> creatureMap = world.getCreaturesMap();
            for (Entry<Creature, Coords> entry : creatureMap.entrySet()) {
                Coords position = entry.getValue();

                if (position.x() < (world.getWidth() / 2)) {
                    toKill.add(entry.getKey());
                }
            }

            for (Creature creature : toKill) {
                world.removeCreature(creature);
            }

            int survivorsCount = world.getCreatureCount();
            System.out.println("survival rate: " + survivorsCount + "%");

            simulation.nextSimulationRound(gen);
            Thread.sleep(1000);
            
            Set<Creature> survivors = world.getCreatures();
            List<Creature> males = new ArrayList<>();
            List<Creature> females = new ArrayList<>();
            
            for (Creature creature : survivors) {
                if (creature.getGender() == 0) females.add(creature);
                else males.add(creature);
            }
            
            world.clearWorldOfCreatures();

            for (int i = 0; i < 100; i++) {
                Creature parent1 = (Creature) Utilities.pickRandom(simulation.convertToObjects(males));
                Creature parent2 = (Creature) Utilities.pickRandom(simulation.convertToObjects(females));
                Creature.birthCreature(world, parent1, parent2);
            }
            world.doCreatureBrainScan();
        }
    }
}
