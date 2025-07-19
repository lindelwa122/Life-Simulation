package io.github.lindelwa122.world;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

import io.github.lindelwa122.coords.Coords;

public class World {
    private final int HEIGHT;
    private final int WIDTH;

    private final Coords DRY_CLIMATE_ORIGIN;
    private final Coords COLD_CLIMATE_ORIGIN;
    private final Coords WET_CLIMATE_ORIGIN;
    private final Coords HOT_CLIMATE_ORIGIN;

    private final int POINT_SIZE = 10;

    // Weights
    private final double CORNER_WEIGHT = 0.9;
    private final double NEIGHBOUR_WEIGHT = 0.7;
    private final double RAND_VARIATION_WEIGHT = 0.5;

    private final List<List<Climate>> CLIMATE_GRID = new ArrayList<>();

    public World(int height, int width) {
        this.HEIGHT = height;
        this.WIDTH = width;

        this.DRY_CLIMATE_ORIGIN = new Coords(0, 0);
        this.COLD_CLIMATE_ORIGIN = new Coords((width / this.POINT_SIZE)-1, 0);
        this.WET_CLIMATE_ORIGIN = new Coords(0, (height / this.POINT_SIZE)-1);
        this.HOT_CLIMATE_ORIGIN = new Coords((height / this.POINT_SIZE)-1, (width / this.POINT_SIZE)-1);

        this.createClimateRegions();
    }

    private void createEmptyClimateGrid() {
        for (int x = 0; x < this.WIDTH / this.POINT_SIZE; x++) {
            List<Climate> row = new ArrayList<>();
            for (int y = 0; y < this.HEIGHT / this.POINT_SIZE; y++) {
                row.add(null);
            }
            this.CLIMATE_GRID.add(row);
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
            case DRY -> cornerC = this.DRY_CLIMATE_ORIGIN;
            case COLD -> cornerC = this.COLD_CLIMATE_ORIGIN;
            case WET -> cornerC = this.WET_CLIMATE_ORIGIN;
            case HOT -> cornerC = this.HOT_CLIMATE_ORIGIN;
        }

        return Math.max(1 - this.distance(cornerC, coords) / (this.WIDTH / (double) this.POINT_SIZE), 0);
    }

    private double getNeighbourInfluence(Climate climate, Coords coords) {
        int count = 0;
        int x = coords.x();
        int y = coords.y();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (isOutOfBounds(x, y, dx, dy))
                    continue;

                Climate climateAtCoords = this.CLIMATE_GRID.get(x + dx).get(y + dy);
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
                || (x == (this.WIDTH / this.POINT_SIZE) - 1 && dx == 1);
    }

    private boolean isOutOfBoundsVertical(int y, int dy) {
        return (y == 0 && dy == -1)
                || (y == (this.HEIGHT / this.POINT_SIZE) - 1 && dy == 1);
    }

    private double getRandomVariation() {
        return Math.min(Math.random()+0.5, 1);
    }

    private double getClimateInfluence(Climate climate, Coords coords) {
        return this.CORNER_WEIGHT*this.getCornerInfluence(climate, coords)
            + this.NEIGHBOUR_WEIGHT*this.getNeighbourInfluence(climate, coords)
            + this.RAND_VARIATION_WEIGHT*this.getRandomVariation();
    }

    private boolean biggerThanAll(double value, List<Double> values) {
        for (Double num : values) {
            if (num > value) return false;
        }
        return true;
    }

    private void createClimateRegions() {
        this.createEmptyClimateGrid();

        for (int x = 0; x < this.WIDTH / this.POINT_SIZE; x++) {
            for (int y = 0; y < this.HEIGHT / this.POINT_SIZE; y++) {
                double hotClimateInfluence = this.getClimateInfluence(Climate.HOT, new Coords(x, y));
                double coldClimateInfluence = this.getClimateInfluence(Climate.COLD, new Coords(x, y));
                double dryClimateInfluence = this.getClimateInfluence(Climate.DRY, new Coords(x, y));
                double wetClimateInfluence = this.getClimateInfluence(Climate.WET, new Coords(x, y));

                if (this.biggerThanAll(wetClimateInfluence, List.of(hotClimateInfluence, coldClimateInfluence, dryClimateInfluence))) {
                    this.CLIMATE_GRID.get(x).set(y, Climate.WET);
                }

                else if (this.biggerThanAll(coldClimateInfluence, List.of(hotClimateInfluence, wetClimateInfluence, dryClimateInfluence))) {
                    this.CLIMATE_GRID.get(x).set(y, Climate.COLD);
                }

                else if (this.biggerThanAll(dryClimateInfluence, List.of(hotClimateInfluence, coldClimateInfluence, wetClimateInfluence))) {
                    this.CLIMATE_GRID.get(x).set(y, Climate.DRY);
                }

                else {
                    this.CLIMATE_GRID.get(x).set(y, Climate.HOT);
                }
            }
        }
    }

    public void paintWorld(Graphics g) {
        for (int x = 0; x < this.CLIMATE_GRID.size(); x++) {
            for (int y = 0; y < this.CLIMATE_GRID.get(x).size(); y++) {
                switch (this.CLIMATE_GRID.get(x).get(y)) {
                    case HOT:
                        g.setColor(new Color(5, 250, 38, 50));
                        break;

                    case COLD:
                        g.setColor(new Color(255, 255, 255, 50));
                        break;

                    case WET:
                        g.setColor(new Color(5, 123, 250, 50));
                        break;

                    case DRY:
                        g.setColor(new Color(250, 175, 5, 50));
                        break;
                
                    default:
                        break;
                }
                g.fillRect(x*this.POINT_SIZE, y*this.POINT_SIZE, this.POINT_SIZE, this.POINT_SIZE);
            }
        }
    }
}
