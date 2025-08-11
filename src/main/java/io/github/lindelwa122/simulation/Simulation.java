package io.github.lindelwa122.simulation;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.pbcs.Tree;
import io.github.lindelwa122.world.World;

public class Simulation extends JPanel {
    private static final int WORLD_HEIGHT = 800;
    private static final int WORLD_WIDTH = 800;

    private static World world = new World(WORLD_HEIGHT, WORLD_WIDTH);

    public Simulation() {
        for (int i = 0; i < 20; i++) {
            Tree.plantTree(world);
        }
    
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

    public void nextSimulationRound() {
        world.nextSimulationRound();
        this.repaint();
    }

    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation();

        JFrame frame = new JFrame("Life Simulation");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WORLD_WIDTH + 200, WORLD_HEIGHT + 200);
        frame.add(simulation);
        frame.setVisible(true);


        for (int i = 0; i < 100; i++) {
            simulation.nextSimulationRound();
            Thread.sleep(1000);
        }
    }
}
