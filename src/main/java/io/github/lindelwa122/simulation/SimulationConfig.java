package io.github.lindelwa122.simulation;

import java.util.List;

public class SimulationConfig {
    public Boolean fastforward;
    public Integer numberOfGenes;
    public Boolean headless;
    public Integer snapshotInterval;
    public String outputDir;
    public Boolean mutationEnabled;
    public Double mutationRate;
    public Double mutationAddConnectionRate;
    public Double mutationRemoveConnectionRate;
    public List<SurvivalBox> survivalCriteria;
    public List<ObstacleSpec> initialObstacles;
    public List<GenerationUpdate> generationUpdates;
    public boolean skipPainting;
    public Integer maxPopulation;
    public Integer stepsPerGeneration;
    public Integer maxGenerations;

    public static class SurvivalBox {
        public Integer x1;
        public Integer y1;
        public Integer x2;
        public Integer y2;

        public boolean isValid() {
            return x1 != null && y1 != null && x2 != null && y2 != null;
        }

        @Override
        public String toString() {
            return x1 + "," + y1 + " to " + x2 + "," + y2;
        }
    }

    public static class ObstacleSpec {
        public String name; // optional, used to reference later
        public String orientation; // VERTICAL or HORIZONTAL
        public Integer fixedCoord;
        public Integer start;
        public Integer end;
    }

    public static class GenerationUpdate {
        public Integer generation;
        public List<SurvivalBox> survivalCriteria;
        public Boolean fastforward;
        public Boolean skipPainting;
        public List<ObstacleAction> obstacleActions;
        public Double mutationRate;
        public Double mutationAddConnectionRate;
        public Double mutationRemoveConnectionRate;
    }

    public static class ObstacleAction {
        public String action; // add | remove | move
        public String name; // references initial obstacle by name
        public Integer id; // optional numeric id

        // for add
        public String orientation;
        public Integer fixedCoord;
        public Integer start;
        public Integer end;

        // for move
        public Integer dx;
        public Integer dy;
    }
}
