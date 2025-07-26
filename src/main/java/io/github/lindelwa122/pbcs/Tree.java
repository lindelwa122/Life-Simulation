package io.github.lindelwa122.pbcs;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import io.github.lindelwa122.cellularStructure.CellularMakeUp;
import io.github.lindelwa122.cellularStructure.Element;
import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.Climate;
import io.github.lindelwa122.world.World;

public class Tree {
    private final CellularMakeUp makeUp;
    private final FundamentalElements type;

    private static final int MAX_DECAY_ROUNDS = 4;
    private static final int HARVEST_CYCLE = 10;
    private static final int MAX_FRUITS = 5;

    public static final int SIZE = 5;

    protected final World world;

    private int c;
    private int lifeExpectancy;
    private int age = 0;
    private int decayingStep = 0;
    private int fruits = 3;

    private boolean alive;

    public Tree(World world, CellularMakeUp makeUp, FundamentalElements type) {
        this.makeUp = makeUp;
        this.type = type;
        this.world = world;

        Coords coords = this.world.getTreeCoords(this);
        Climate climate = this.world.getClimateOnCoords(coords);

        switch (climate) {
            case DRY:
                this.c = this.makeUp.xeraphin().value();
                break;

            case HOT: 
                this.c = this.makeUp.ignyra().value();
                break;

            case WET: 
                this.c = this.makeUp.humidra().value();
                break;

            case COLD: 
                this.c = this.makeUp.cryonel().value();
        }

        this.lifeExpectancy = 400 * (c/100);
    }

    public static Tree plantTree(World world) {
        List<Object> planttypes = List.of(FundamentalElements.LUNAPHYLL, FundamentalElements.XYLORA, FundamentalElements.MORBIORA);
        FundamentalElements type = (FundamentalElements) Utilities.pickRandom(planttypes);

        Element hydrex = new Element(FundamentalElements.HYDREX, 50);
        Element ignyra = new Element(FundamentalElements.IGNYRA, Utilities.random(100));
        Element xeraphin = new Element(FundamentalElements.XERAPHIN, Utilities.random(100));
        Element humidra = new Element(FundamentalElements.HUMIDRA, Utilities.random(100));
        Element cryonel = new Element(FundamentalElements.CRYONEL, Utilities.random(100));

        CellularMakeUp makeUp = new CellularMakeUp(hydrex, ignyra, xeraphin, humidra, cryonel, null, null, null, null);
        Tree newTree = new Tree(world, makeUp, type);
        
        world.addTree(newTree);
        return newTree;
    }

    public boolean isAlive() {
        return this.alive;
    }

    private boolean isHarvestCycle() {
        return age % HARVEST_CYCLE == 0 && isAlive();
    }

    private void incrementAge() {
        if (!this.isAlive() && this.decayingStep >= MAX_DECAY_ROUNDS) {
            this.world.removeTree(this);
        }

        else if (!this.isAlive()) {
            this.decayingStep++;
        }

        else if (this.age >= this.lifeExpectancy) {
            this.alive = false;
        }

        else {
            this.age++;
        }
    }

    public void nextRound() {
        this.incrementAge();

        if (this.isHarvestCycle() && this.fruits < MAX_FRUITS) {
            this.fruits++;
        }
    }

    public void paint(Graphics g, Coords coords) {
        int treeSize = World.POINT_SIZE*Tree.SIZE;

        g.setColor(new Color(0, 255, 0));
        g.fillRect(coords.x(), coords.y(), treeSize, treeSize);

        Color fruitsColor;
        if (type == FundamentalElements.LUNAPHYLL) {
            fruitsColor = Color.RED;
        }
        else if (type == FundamentalElements.XYLORA) {
            fruitsColor = Color.ORANGE;
        }
        else {
            fruitsColor = Color.BLUE;
        }

        for (int i = 0; i < this.fruits; i++) {
            int x = coords.x() + (treeSize / (World.POINT_SIZE/2)) + (World.POINT_SIZE*i);
            int y = coords.y() + (treeSize / 2) - (World.POINT_SIZE/2);
            g.setColor(fruitsColor);
            g.fillOval(x, y, World.POINT_SIZE, World.POINT_SIZE);
        }
    }
}
