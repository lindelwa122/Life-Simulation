package io.github.lindelwa122.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.*;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.pbcs.Tree;
import io.github.lindelwa122.utilities.MostlyUsedColors;
import io.github.lindelwa122.utilities.Utilities;

public class World {
    private final int height;
    private final int width;

    private final Coords dryClimateRegion;
    private final Coords coldClimateRegion;
    private final Coords wetClimateRegion;
    private final Coords hotClimateRegion;

    public static final int POINT_SIZE = 10;

    // Weights
    private static final double CORNER_WEIGHT = 0.9;
    private static final double NEIGHBOUR_WEIGHT = 0.7;
    private static final double RAND_VARIATION_WEIGHT = 0.5;

    private final List<List<Climate>> climateGrid = new ArrayList<>();

    private final Map<Tree, Coords> treeList = new HashMap<>();

    public World(int height, int width) {
        this.height = height;
        this.width = width;

        this.dryClimateRegion = new Coords(0, 0);
        this.coldClimateRegion = new Coords((width / POINT_SIZE)-1, 0);
        this.wetClimateRegion = new Coords(0, (height / POINT_SIZE)-1);
        this.hotClimateRegion = new Coords((height / POINT_SIZE)-1, (width / POINT_SIZE)-1);

        this.createClimateRegions();
    }

    private boolean doRectsOverlap(Coords topLeft1, Coords bottomRight1, Coords topLeft2, Coords bottomRight2) {
        if (topLeft1.x() > bottomRight2.x() || topLeft2.x() > bottomRight1.x())
            return false;

        // If one rectangle is above the other
        return !(bottomRight1.y() > topLeft2.y() || bottomRight2.y() > topLeft1.y());
    }

    private boolean isAreaOccupied(Coords startCoords, int height, int width) {
        Coords endCoords = new Coords(startCoords.x() + width, startCoords.y() + height); 

        for (Entry<Tree, Coords> entry : this.treeList.entrySet()) {
            Coords treeStartCoords = entry.getValue();
            Coords treeEndCoords = new Coords(
                treeStartCoords.x() + (Tree.SIZE*POINT_SIZE), 
                treeStartCoords.y() + (Tree.SIZE*POINT_SIZE));

            if (this.doRectsOverlap(treeStartCoords, treeEndCoords, startCoords, endCoords)) {
                return true;
            }
        }

        return false;
    }

    public void addTree(Tree tree) {
        while (true) {
            int treeSize = Tree.SIZE*POINT_SIZE;

            int randX = Utilities.random(this.width - treeSize);
            int randY = Utilities.random(this.height - treeSize);

            Coords coords = new Coords(randX, randY);
            if (!isAreaOccupied(coords, treeSize, treeSize)) {
                this.treeList.put(tree, coords);
                break;
            }
        }
    }

    public void removeTree(Tree tree) {
        for (Tree t : this.treeList.keySet()) {
            if (t == tree) {
                this.treeList.remove(tree);
                return;
            }
        }
    }

    public Map<Tree, Coords> getTrees() {
        return this.treeList;
    }

    public Coords getTreeCoords(Tree tree) {
        for (Entry<Tree, Coords> entry : this.treeList.entrySet()) {
            if (entry.getKey() == tree) return entry.getValue();
        }
        return null;
    }

    public Climate getClimateOnCoords(Coords coords) {
        return this.climateGrid.get(coords.x()).get(coords.y());
    }

    private void createEmptyClimateGrid() {
        for (int x = 0; x < this.width / POINT_SIZE; x++) {
            List<Climate> row = new ArrayList<>();
            for (int y = 0; y < this.height / POINT_SIZE; y++) {
                row.add(null);
            }
            this.climateGrid.add(row);
        }
    } 

    private double distance(Coords pointA, Coords pointB) {
        int diffX = pointB.x() - pointA.x();
        int diffY = pointB.y() - pointA.y();

        double sqrdX = Math.pow(diffX, 2);
        double sqrdY = Math.pow(diffY, 2);
        return Math.sqrt(sqrdX + sqrdY);
    }

    private double getCornerInfluence(Climate climate, Coords coords) {
        Coords cornerC = null;
        switch (climate) {
            case DRY -> cornerC = this.dryClimateRegion;
            case COLD -> cornerC = this.coldClimateRegion;
            case WET -> cornerC = this.wetClimateRegion;
            case HOT -> cornerC = this.hotClimateRegion;
        }

        return Math.max(1 - this.distance(cornerC, coords) / (this.width / (double) POINT_SIZE), 0);
    }

    private double getNeighbourInfluence(Climate climate, Coords coords) {
        int count = 0;
        int x = coords.x();
        int y = coords.y();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (isOutOfBounds(x, y, dx, dy))
                    continue;

                Climate climateAtCoords = this.climateGrid.get(x + dx).get(y + dy);
                if (climate.equals(climateAtCoords))
                    count++;
            }
        }
        return (double) count / 8;
    }

    private boolean isOutOfBounds(int x, int y, int dx, int dy) {
        return isStationary(dx, dy) || isOutOfBoundsHorizontal(x, dx) || isOutOfBoundsVertical(y, dy);
    }

    private boolean isStationary(int dx, int dy) {
        return (dx == 0 && dy == 0);
    }

    private boolean isOutOfBoundsHorizontal(int x, int dx) {
        return (x == 0 && dx == -1)
                || (x == (this.width / POINT_SIZE) - 1 && dx == 1);
    }

    private boolean isOutOfBoundsVertical(int y, int dy) {
        return (y == 0 && dy == -1)
                || (y == (this.height / POINT_SIZE) - 1 && dy == 1);
    }

    private double getRandomVariation() {
        return Math.min(Math.random()+0.5, 1);
    }

    private double getClimateInfluence(Climate climate, Coords coords) {
        return CORNER_WEIGHT*this.getCornerInfluence(climate, coords)
            + NEIGHBOUR_WEIGHT*this.getNeighbourInfluence(climate, coords)
            + RAND_VARIATION_WEIGHT*this.getRandomVariation();
    }

    private boolean biggerThanAll(double value, List<Double> values) {
        for (Double num : values) {
            if (num > value) return false;
        }
        return true;
    }

    private void createClimateRegions() {
        this.createEmptyClimateGrid();

        for (int x = 0; x < this.width / POINT_SIZE; x++) {
            for (int y = 0; y < this.height / POINT_SIZE; y++) {
                double hotClimateInfluence = this.getClimateInfluence(Climate.HOT, new Coords(x, y));
                double coldClimateInfluence = this.getClimateInfluence(Climate.COLD, new Coords(x, y));
                double dryClimateInfluence = this.getClimateInfluence(Climate.DRY, new Coords(x, y));
                double wetClimateInfluence = this.getClimateInfluence(Climate.WET, new Coords(x, y));

                if (this.biggerThanAll(wetClimateInfluence, List.of(hotClimateInfluence, coldClimateInfluence, dryClimateInfluence))) {
                    this.climateGrid.get(x).set(y, Climate.WET);
                }

                else if (this.biggerThanAll(coldClimateInfluence, List.of(hotClimateInfluence, wetClimateInfluence, dryClimateInfluence))) {
                    this.climateGrid.get(x).set(y, Climate.COLD);
                }

                else if (this.biggerThanAll(dryClimateInfluence, List.of(hotClimateInfluence, coldClimateInfluence, wetClimateInfluence))) {
                    this.climateGrid.get(x).set(y, Climate.DRY);
                }

                else {
                    this.climateGrid.get(x).set(y, Climate.HOT);
                }
            }
        }
    }

    public void paintWorld(Graphics g) {
        for (int x = 0; x < this.climateGrid.size(); x++) {
            for (int y = 0; y < this.climateGrid.get(x).size(); y++) {
                switch (this.climateGrid.get(x).get(y)) {
                    case HOT:
                        g.setColor(MostlyUsedColors.HOT.getColor());
                        break;

                    case COLD:
                        g.setColor(MostlyUsedColors.COLD.getColor());
                        break;

                    case WET:
                        g.setColor(MostlyUsedColors.WET.getColor());
                        break;

                    case DRY:
                        g.setColor(MostlyUsedColors.DRY.getColor());
                        break;
                
                    default:
                        break;
                }
                g.fillRect(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
            }
        }

        for (Map.Entry<Tree, Coords> treeEntry : this.treeList.entrySet()) {
            treeEntry.getKey().paint(g, treeEntry.getValue());
        }
    }
}
