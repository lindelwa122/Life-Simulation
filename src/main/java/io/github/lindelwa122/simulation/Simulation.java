package io.github.lindelwa122.simulation;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import io.github.lindelwa122.world.World;

public class Simulation extends JPanel {
    private static final int WORLD_HEIGHT = 800;
    private static final int WORLD_WIDTH = 800;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        World world = new World(WORLD_HEIGHT, WORLD_WIDTH);
        // world.paintWorld(g);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Life Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        frame.add(new Simulation());
        frame.setVisible(true);
    }
}
