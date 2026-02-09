package io.github.lindelwa122.world;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.simulation.SimulationConfig;
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
    private final List<Creature> creatureList = new ArrayList<>();

    // Obstacles: id -> obstacle
    private final Map<Integer, Obstacle> obstacleList = new HashMap<>();
    private int nextObstacleId = 1;

    // Pheromone map: tile coordinates -> concentration
    private final Map<Coords, Double> pheromoneMap = new HashMap<>();
    public static final double PHEROMONE_EMISSION = 1.0; // fixed amount per creature per tick
    private static final double PHEROMONE_DECAY_FACTOR = 0.5; // exponential decay: concentration *= 0.5 every 10th tick
    private static final double PHEROMONE_THRESHOLD = 0.01; // cleanup negligible pheromones

    // Keep a list of murdered creatures to prevent concurrent modification issues when iterating over creatureList while also removing creatures that don't survive
    private final List<Creature> murderedCreatures = new ArrayList<>();
    private int murderedOnPrevGeneration = 0;
    private int murderedOnCurrentGeneration = 0;
    private int generation = 0;

    // Keep track of maxPopulation here so creatures can update it
    private int maxPopulation;
    private final double populationChangeRate = 0.9;

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public void setMaxPopulation(int maxPopulation) {
        this.maxPopulation = maxPopulation;
    }

    public void increaseMaxPopulation() {
        this.maxPopulation = (int) (this.maxPopulation / populationChangeRate);
    }

    public void decreaseMaxPopulation() {
        this.maxPopulation = (int) (this.maxPopulation * populationChangeRate);
    }

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
        return !(bottomRight1.y() < topLeft2.y() || bottomRight2.y() < topLeft1.y());
    }

    private boolean isAreaOccupiedByCreature(Coords startCoords, Coords endCoords) {
        return this.isAreaOccupiedByCreature(startCoords, endCoords, null);
    }

    private boolean isAreaOccupiedByCreature(Coords startCoords, Coords endCoords, Creature excludeCreature) {
        for (Creature creature: this.creatureList) {
            if (creature == excludeCreature) continue; // skip the creature trying to move
            
            Coords creatureStartCoords = creature.getCurrentPosition();
            Coords creatureEndCoords = new Coords(
                creatureStartCoords.x()+POINT_SIZE, 
                creatureStartCoords.y()+POINT_SIZE
            );

            if (this.doRectsOverlap(creatureStartCoords, creatureEndCoords, startCoords, endCoords)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAreaOccupied(Coords startCoords, int height, int width) {
        Coords endCoords = new Coords(startCoords.x() + width, startCoords.y() + height); 
        return this.isAreaOccupiedByCreature(startCoords, endCoords)
            || this.isAreaOccupiedByObstacle(startCoords, endCoords);
    }

    private boolean isAreaOccupiedByObstacle(Coords startCoords, Coords endCoords) {
        for (Entry<Integer, Obstacle> entry : this.obstacleList.entrySet()) {
            Obstacle obs = entry.getValue();
            if (this.doRectsOverlap(obs.startCoords, obs.endCoords, startCoords, endCoords)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAreaOccupiedByObstacle(Coords startCoords, Coords endCoords, int excludeId) {
        for (Entry<Integer, Obstacle> entry : this.obstacleList.entrySet()) {
            if (entry.getKey() == excludeId) continue;
            Obstacle obs = entry.getValue();
            if (this.doRectsOverlap(obs.startCoords, obs.endCoords, startCoords, endCoords)) {
                return true;
            }
        }
        return false;
    }

    public Climate getClimateOnCoords(Coords coords) {
        return this.climateGrid.get(coords.x() / POINT_SIZE).get(coords.y() / POINT_SIZE);
    }

    public Coords getRandomCoordsInClimate(Climate climate) {
        List<Coords> matchingCoords = new ArrayList<>();

        for (int x = 0; x < this.climateGrid.size(); x++) {
            for (int y = 0; y < this.climateGrid.get(x).size(); y++) {
                if (this.climateGrid.get(x).get(y) == climate) {
                    matchingCoords.add(new Coords(x * POINT_SIZE, y * POINT_SIZE));
                }
            }
        }

        if (matchingCoords.isEmpty()) {
            throw new IllegalStateException("No coordinates found for climate: " + climate);
        }

        return matchingCoords.get(Utilities.random(matchingCoords.size()));
    }

    public boolean addCreature(Creature creature) {
        int maximumTries = (this.width/POINT_SIZE) * (this.height/POINT_SIZE);
        int tries = 0;

        while (tries <= maximumTries) {
            Coords coords = new Coords(
                Utilities.random(this.width / POINT_SIZE) * POINT_SIZE, 
                Utilities.random(this.height / POINT_SIZE) * POINT_SIZE
            );

            if (!isAreaOccupied(coords, POINT_SIZE, POINT_SIZE)) {
                this.creatureList.add(creature);
                creature.setCurrentPosition(coords);
                return true;
            }
            tries++;
        }

        return false;
    }

    public boolean addCreature(Creature creature, Climate birthClimate) {
        int maximumTries = ((this.width/POINT_SIZE) * (this.height/POINT_SIZE)) / 4; // try up to 25% of the world size before giving up, to prevent infinite loops in very crowded worlds
        int tries = 0;

        while (tries <= maximumTries) {
            Coords coords = this.getRandomCoordsInClimate(birthClimate);
            if (!isAreaOccupied(coords, POINT_SIZE, POINT_SIZE)) {
                this.creatureList.add(creature);
                creature.setCurrentPosition(coords);
                return true;
            }
            tries++;
        }

        return false;
    }

    public boolean moveCreature(Creature creature, Coords position) {
        if (canMoveCreature(creature, position)) {
            creature.setCurrentPosition(position);
            return true;
        }
        return false;
    }

    public boolean canMoveCreature(Creature creature, Coords position) {
        Coords endCoords = new Coords(position.x(), position.y());
        return !this.isAreaOccupiedByCreature(position, endCoords, creature)
            && !this.isAreaOccupiedByObstacle(position, endCoords)
            && this.creatureList.contains(creature)
            && !this.newIsOutOfBounds(position.x(), position.y(), POINT_SIZE, POINT_SIZE);
    }

    public List<Creature> getCreaturesList() {
        return this.creatureList;
    }

    public void removeCreature(Creature creature) {
        if (this.creatureList.contains(creature)) this.creatureList.remove(creature);
    }

    public Coords getCreatureCoords(Creature creature) {
        return creature.getCurrentPosition();
    }

    public int getCreatureCount() {
        return this.creatureList.size();
    }

    public Set<Creature> getCreatures() {
        return creatureList.stream().collect(Collectors.toSet());
    }

    public void clearWorldOfCreatures() {
        this.creatureList.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
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

    private boolean newIsOutOfBounds(int x, int y, int width, int height) {
        if (x < 0 || y < 0) return true;
        else if (x + width > this.width) return true;
        else return y + height > this.height;
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

    public void doCreatureBrainScan() {
        for (Creature creature : creatureList) {
            creature.recordBrain();
        }
    }

    public void addPheromone(Coords tileCoords, double amount) {
        pheromoneMap.put(tileCoords, pheromoneMap.getOrDefault(tileCoords, 0.0) + amount);
    }

    public double getPheromoneLevel(Coords tileCoords) {
        return pheromoneMap.getOrDefault(tileCoords, 0.0);
    }

    private void decayPheromones(int generation) {
        if (generation % 10 == 0) {
            pheromoneMap.replaceAll((coords, level) -> level * PHEROMONE_DECAY_FACTOR);
            // Cleanup negligible pheromones
            pheromoneMap.entrySet().removeIf(entry -> entry.getValue() < PHEROMONE_THRESHOLD);
        }
    }

    public void nextSimulationRound(int generation) {
        for (Creature creature : creatureList) {
            // Update creature
            creature.nextSimulationRound();
        }

        // Decay all pheromones
        decayPheromones(generation);

        if (this.generation < generation) {
            this.generation = generation;
            this.murderedOnPrevGeneration = this.murderedOnCurrentGeneration;
            this.murderedOnCurrentGeneration = 0;
        }
        
        // Remove murdered creatures from the world after the round is over to prevent concurrent modification issues
        for (Creature creature : murderedCreatures) {
            this.removeCreature(creature);
        }
        this.murderedOnCurrentGeneration += murderedCreatures.size();
        murderedCreatures.clear();
    }

    public boolean killCreatureAt(Coords coords) {
        Creature target = this.getCreatureAt(coords);
        if (target != null) {
            this.markCreatureAsMurdered(target);
            return true;
        }
        return false;
    }

    private void markCreatureAsMurdered(Creature creature) {
        this.murderedCreatures.add(creature);
    }

    public int getMurderedOnPrevGeneration() {
        return this.murderedOnPrevGeneration;
    }

    public Creature getCreatureAt(Coords coords) {
        for (Creature creature : this.creatureList) {
            if (creature.getCurrentPosition().equals(coords)) {
                return creature;
            }
        }
        return null;
    }

    // Obstacle representation
    private static class Obstacle {
        int id;
        ObstacleOrientation orientation;
        Coords startCoords; // top-left
        Coords endCoords;   // bottom-right

        Obstacle(int id, ObstacleOrientation orientation, Coords startCoords, Coords endCoords) {
            this.id = id;
            this.orientation = orientation;
            this.startCoords = startCoords;
            this.endCoords = endCoords;
        }
    }

    /**
     * Add an obstacle.
     * For VERTICAL: provide x as `fixedCoord`, and `start`/`end` are y1/y2.
     * For HORIZONTAL: provide y as `fixedCoord`, and `start`/`end` are x1/x2.
     * Returns an integer id for later move/remove operations.
     */
    public int addObstacle(ObstacleOrientation orientation, int fixedCoord, int start, int end) {
        int s = Math.min(start, end);
        int e = Math.max(start, end);

        Coords topLeft;
        Coords bottomRight;

        if (orientation == ObstacleOrientation.VERTICAL) {
            int x = fixedCoord;
            topLeft = new Coords(x, s);
            bottomRight = new Coords(x + POINT_SIZE, e);
        } else {
            int y = fixedCoord;
            topLeft = new Coords(s, y);
            bottomRight = new Coords(e, y + POINT_SIZE);
        }

        int w = bottomRight.x() - topLeft.x();
        int h = bottomRight.y() - topLeft.y();

        if (this.newIsOutOfBounds(topLeft.x(), topLeft.y(), w, h)) {
            throw new IllegalArgumentException("Obstacle out of bounds");
        }

        if (this.isAreaOccupiedByObstacle(topLeft, bottomRight)) {
            throw new IllegalArgumentException("Obstacle area already occupied");
        }

        int id = this.nextObstacleId++;
        Obstacle obs = new Obstacle(id, orientation, topLeft, bottomRight);
        this.obstacleList.put(id, obs);
        return id;
    }

    /** Move obstacle by dx, dy (pixels). Returns true if moved. */
    public boolean moveObstacle(int id, int dx, int dy) {
        Obstacle obs = this.obstacleList.get(id);
        if (obs == null) return false;

        Coords newTopLeft = new Coords(obs.startCoords.x() + dx, obs.startCoords.y() + dy);
        Coords newBottomRight = new Coords(obs.endCoords.x() + dx, obs.endCoords.y() + dy);

        int w = newBottomRight.x() - newTopLeft.x();
        int h = newBottomRight.y() - newTopLeft.y();

        if (this.newIsOutOfBounds(newTopLeft.x(), newTopLeft.y(), w, h)) return false;

        // Don't collide with creatures or other obstacles
        if (this.isAreaOccupiedByObstacle(newTopLeft, newBottomRight, id)) return false;

        obs.startCoords = newTopLeft;
        obs.endCoords = newBottomRight;
        return true;
    }

    public void removeObstacle(int id) {
        this.obstacleList.remove(id);
    }

    public void paintWorld(Graphics g, List<SimulationConfig.SurvivalBox> survivalBoxes) {
        for (int x = 0; x < this.climateGrid.size(); x++) {
            for (int y = 0; y < this.climateGrid.get(x).size(); y++) {
                switch (this.climateGrid.get(x).get(y)) {
                    case HOT -> g.setColor(MostlyUsedColors.HOT.getColor());

                    case COLD -> g.setColor(MostlyUsedColors.COLD.getColor());

                    case WET -> g.setColor(MostlyUsedColors.WET.getColor());

                    case DRY -> g.setColor(MostlyUsedColors.DRY.getColor());
                
                    default -> {
                    }
                }
                g.fillRect(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
            }
        }

        // Draw obstacles
        g.setColor(Color.DARK_GRAY);
        for (Entry<Integer, Obstacle> obsEntry : this.obstacleList.entrySet()) {
            Obstacle obs = obsEntry.getValue();
            int w = obs.endCoords.x() - obs.startCoords.x();
            int h = obs.endCoords.y() - obs.startCoords.y();
            g.fillRect(obs.startCoords.x(), obs.startCoords.y(), w, h);
        }

        // Sometimes there's a bug where the creature cannot be painted on the world.
        // I don't know why, but this check prevents the app from crashing when it happens. 
        // The creature may seem to jump because its position is updated but it isn't painted, 
        // but at least the app won't crash and the creature will be painted in the next tick.
        try {
            for (Creature creature : this.creatureList) {
                creature.paint(g, creature.getCurrentPosition());
            }
        } catch (Exception e) {
            // Fail silently, creature won't be painted this tick but will be painted in the next tick when the bug hopefully resolves itself
        }

        for (SimulationConfig.SurvivalBox survivalBox : survivalBoxes) {
            if (survivalBox != null && survivalBox.isValid()) {
                g.setColor(Color.YELLOW);
                g.drawRect(
                    survivalBox.x1, 
                    survivalBox.y1, 
                    survivalBox.x2 - survivalBox.x1, 
                    survivalBox.y2 - survivalBox.y1
                );
            }
        }
    }
}
