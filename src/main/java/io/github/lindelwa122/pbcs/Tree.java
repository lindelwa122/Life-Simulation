package io.github.lindelwa122.pbcs;

import java.util.List;

import io.github.lindelwa122.cellularStructure.CellularMakeUp;
import io.github.lindelwa122.cellularStructure.Element;
import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class Tree {
    private final CellularMakeUp MAKE_UP;
    private final FundamentalElements TYPE;

    private final int MAX_DECAY_ROUNDS = 4;
    private final int REPRODUCTION_CYCLE = 50;
    private final int HARVEST_CYCLE = 10;
    private final int MAX_FRUITS = 5;

    public static final int SIZE = 5;

    protected final World WORLD;

    private int c;
    private int lifeExpectancy;
    private int age = 0;
    private int decayingStep = 0;
    private int fruits = 5;

    private boolean alive;

    public Tree(World world, CellularMakeUp makeUp, FundamentalElements type) {
        this.MAKE_UP = makeUp;
        this.TYPE = type;
        this.WORLD = world;
    }

    public static Tree plantTree(World world) {
        List<Object> plantTypes = List.of(FundamentalElements.LUNAPHYLL, FundamentalElements.XYLORA, FundamentalElements.MORBIORA);
        FundamentalElements type = (FundamentalElements) Utilities.pickRandom(plantTypes);

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

    private boolean isReproductionCycle() {
        return age % this.REPRODUCTION_CYCLE == 0;
    }

    private boolean isHarvestCycle() {
        return age % this.HARVEST_CYCLE == 0;
    }

    private void incrementAge() {
        if (!this.isAlive() && this.decayingStep >= this.MAX_DECAY_ROUNDS) {
            this.WORLD.removeTree(this);
        }

        else if (!this.isAlive()) {
            this.decayingStep++;
        }

        else {
            this.age++;
        }
    }

    public void nextRound() {
        this.incrementAge();

        if (this.isHarvestCycle() && this.fruits < this.MAX_FRUITS) {
            this.fruits++;
        }
    }
}
