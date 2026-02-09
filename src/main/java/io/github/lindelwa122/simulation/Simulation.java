package io.github.lindelwa122.simulation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.google.gson.Gson;

import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.mbcs.Creature;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.Climate;
import io.github.lindelwa122.world.ObstacleOrientation;
import io.github.lindelwa122.world.World;

public class Simulation extends JPanel {
    private static final int WORLD_HEIGHT = 800;
    private static final int WORLD_WIDTH = 800;

    private static World world = new World(WORLD_HEIGHT, WORLD_WIDTH);
    private static SimulationConfig config = null;
    // map optional names to obstacle ids returned by World
    private static final Map<String, Integer> namedObstacles = new HashMap<>();

    private final boolean headless;
    private boolean currentSkipPainting = false;


    public Simulation(int numberOfGenes, boolean headless, int maxPopulation) {
        // Set the max population
        world.setMaxPopulation(maxPopulation);

        this.headless = headless;
        for (int i = 0; i < maxPopulation; i++) {
            Creature.birthCreature(world, numberOfGenes);
        }
        world.doCreatureBrainScan();
    }

    public Simulation() {
        this(8, false, 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        world.paintWorld(g, config.survivalCriteria);
    }

    public void nextSimulationRound(int gen) {
        world.nextSimulationRound(gen);
        if (!this.headless && !this.currentSkipPainting) this.repaint();
    }

    public List<Object> convertToObjects(List<Creature> creatures) {
        List<Object> objects = new ArrayList<>();
        for (Creature creature : creatures) objects.add(creature);
        return objects;
    }

    public static void main(String[] args) throws InterruptedException {
        // Load config if available
        Path cfgPath = Path.of(args.length > 0 ? args[0] : "simulation-config.json");
        if (Files.exists(cfgPath)) {
            try (FileReader fr = new FileReader(cfgPath.toFile())) {
                config = new Gson().fromJson(fr, SimulationConfig.class);
            } catch (Exception e) {
                System.err.println("Failed to read config: " + e.getMessage());
            }
        }

        int numberOfGenes = 8;
        boolean headless = false;
        int maxPopulation = 100;
        int previousSurvivors = 0;
        int stepsPerGeneration = 100;
        int maxGenerations = 20_000;

        if (config != null) {
            if (config.numberOfGenes != null) numberOfGenes = config.numberOfGenes;
            if (config.headless != null) headless = config.headless;
            if (config.maxPopulation != null) maxPopulation = config.maxPopulation;
            if (config.stepsPerGeneration != null) stepsPerGeneration = config.stepsPerGeneration;
            if (config.maxGenerations != null) maxGenerations = config.maxGenerations;
        }

        Simulation simulation = new Simulation(numberOfGenes, headless, maxPopulation);

        // apply initial obstacles from config
        if (config != null && config.initialObstacles != null) {
            for (SimulationConfig.ObstacleSpec spec : config.initialObstacles) {
                try {
                    ObstacleOrientation o = ObstacleOrientation.valueOf(spec.orientation);
                    int id = world.addObstacle(o, spec.fixedCoord, spec.start, spec.end);
                    if (spec.name != null) namedObstacles.put(spec.name, id);
                    System.out.println("Added obstacle id=" + id + " name=" + spec.name);
                } catch (Exception e) {
                    System.err.println("Failed to add obstacle from config: " + e.getMessage());
                }
            }
        }

        // Only create GUI when not headless
        JFrame frame = null;
        if (!headless) {
            frame = new JFrame("Life Simulation");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(WORLD_WIDTH + 200, WORLD_HEIGHT + 200);
            frame.add(simulation);
            frame.setVisible(true);
        }
        
        // current survival box (if not provided, default to left-half behaviour)
        List<SimulationConfig.SurvivalBox> currentSurvival = null;
        if (config != null && config.survivalCriteria != null && !config.survivalCriteria.isEmpty()) {
            currentSurvival = config.survivalCriteria;
        }

        // current fastforward state (can be toggled by generation updates)
        boolean currentFastforward = false;
        if (config != null && config.fastforward != null) currentFastforward = config.fastforward;

        String outputDir = "output";
        if (config != null) {
            if (config.outputDir != null) outputDir = config.outputDir;
        }

        // ensure output dir exists for headless snapshots
        if (headless) {
            new File(outputDir).mkdirs();
        }

        for (int gen = 0; gen < maxGenerations; gen++) {
            String frameTitle = MessageFormat.format("Generation {0}, Prev Survivors {1}, Murdered on Prev Gen {2}", 
                gen, 
                (int) (previousSurvivors / (double) maxPopulation * 100),
                world.getMurderedOnPrevGeneration()
            );

            if (frame != null) frame.setTitle(frameTitle);
            // apply generation updates at start of generation so changes take effect immediately
            if (config != null && config.generationUpdates != null) {
                for (SimulationConfig.GenerationUpdate upd : config.generationUpdates) {
                    if (upd.generation != null && upd.generation == gen) {
                        if (upd.survivalCriteria != null && !upd.survivalCriteria.isEmpty()) {
                            currentSurvival = upd.survivalCriteria;
                            config.survivalCriteria = currentSurvival; // update config for painting
                            System.out.println("Updated survival criteria at generation " + gen + " to " + currentSurvival);
                        }

                        if (upd.fastforward != null) {
                            currentFastforward = upd.fastforward;
                            System.out.println("Toggled fastforward to " + currentFastforward + " at generation " + gen);
                        }

                        if (upd.skipPainting != null) {
                            simulation.currentSkipPainting = upd.skipPainting;
                            System.out.println("Toggled painting to " + (!simulation.currentSkipPainting) + " at generation " + gen);
                        }

                        if (upd.obstacleActions != null) {
                            for (SimulationConfig.ObstacleAction act : upd.obstacleActions) {
                                try {
                                    if ("add".equalsIgnoreCase(act.action)) {
                                        ObstacleOrientation o = ObstacleOrientation.valueOf(act.orientation);
                                        int id = world.addObstacle(o, act.fixedCoord, act.start, act.end);
                                        if (act.name != null) namedObstacles.put(act.name, id);
                                        System.out.println("Added obstacle id=" + id + " via generation update");
                                    } else if ("remove".equalsIgnoreCase(act.action)) {
                                        Integer id = act.id;
                                        if (id == null && act.name != null) id = namedObstacles.get(act.name);
                                        if (id != null) {
                                            world.removeObstacle(id);
                                            System.out.println("Removed obstacle id=" + id + " via generation update");
                                        }
                                    } else if ("move".equalsIgnoreCase(act.action)) {
                                        Integer id = act.id;
                                        if (id == null && act.name != null) id = namedObstacles.get(act.name);
                                        if (id != null) {
                                            boolean moved = world.moveObstacle(id, act.dx != null ? act.dx : 0, act.dy != null ? act.dy : 0);
                                            System.out.println("Moved obstacle id=" + id + " result=" + moved);
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Failed to apply obstacle action: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }

            for (int round = 0; round < stepsPerGeneration; round++) {
                // headless snapshot
                if (headless) {
                    saveSnapshot(simulation, gen, round, outputDir);
                }

                simulation.nextSimulationRound(gen);
                if (!currentFastforward) Thread.sleep(100);
            }

            List<Creature> toKill = new ArrayList<>(world.getCreaturesList());

            for (Creature creature : world.getCreaturesList()) {
                Coords position = creature.getCurrentPosition();

                boolean survives = true;
                if (currentSurvival != null && !currentSurvival.isEmpty()) {
                    for (SimulationConfig.SurvivalBox box : currentSurvival) {
                        if (box.isValid()) {
                            int minX = Math.min(box.x1, box.x2);
                            int maxX = Math.max(box.x1, box.x2);
                            int minY = Math.min(box.y1, box.y2);
                            int maxY = Math.max(box.y1, box.y2);
                            survives = position.x() >= minX && position.x() <= maxX && position.y() >= minY && position.y() <= maxY;

                            if (survives) break; // survive if in any valid box
                        } else {
                            survives = true; // if box is invalid, ignore it and don't kill creature based on it
                        }
                    }
                } else {
                    // old behaviour: survive if on right half (keep continuity)
                    survives = position.x() >= (world.getWidth() / 2);
                }

                if (survives) toKill.remove(creature);
            }

            for (Creature creature : toKill) {
                world.removeCreature(creature);
            }

            previousSurvivors = world.getCreatureCount();

            simulation.nextSimulationRound(gen);
            if (!currentFastforward) Thread.sleep(1000);

            // apply generation updates
            if (config != null && config.generationUpdates != null) {
                for (SimulationConfig.GenerationUpdate upd : config.generationUpdates) {
                    if (upd.generation != null && upd.generation == gen) {

                        if (upd.survivalCriteria != null && !upd.survivalCriteria.isEmpty()) {
                            currentSurvival = upd.survivalCriteria;
                            config.survivalCriteria = currentSurvival; // update config for painting
                            System.out.println("Updated survival criteria at generation " + gen);
                        }

                        if (upd.fastforward != null) {
                            currentFastforward = upd.fastforward;
                            System.out.println("Toggled fastforward to " + currentFastforward + " at generation " + gen);
                        }

                        if (upd.mutationRate != null) {
                            config.mutationRate = upd.mutationRate;
                            System.out.println("Updated mutation rate to " + config.mutationRate + " at generation " + gen);
                        }

                        if (upd.mutationAddConnectionRate != null) {
                            config.mutationAddConnectionRate = upd.mutationAddConnectionRate;
                            System.out.println("Updated mutation add connection rate to " + config.mutationAddConnectionRate + " at generation " + gen);
                        }

                        if (upd.mutationRemoveConnectionRate != null) {
                            config.mutationRemoveConnectionRate = upd.mutationRemoveConnectionRate;
                            System.out.println("Updated mutation remove connection rate to " + config.mutationRemoveConnectionRate + " at generation " + gen);
                        }

                        if (upd.obstacleActions != null) {
                            for (SimulationConfig.ObstacleAction act : upd.obstacleActions) {
                                try {
                                    if ("add".equalsIgnoreCase(act.action)) {
                                        ObstacleOrientation o = ObstacleOrientation.valueOf(act.orientation);
                                        int id = world.addObstacle(o, act.fixedCoord, act.start, act.end);
                                        if (act.name != null) namedObstacles.put(act.name, id);
                                        System.out.println("Added obstacle id=" + id + " via generation update");
                                    } else if ("remove".equalsIgnoreCase(act.action)) {
                                        Integer id = act.id;
                                        if (id == null && act.name != null) id = namedObstacles.get(act.name);
                                        if (id != null) {
                                            world.removeObstacle(id);
                                            System.out.println("Removed obstacle id=" + id + " via generation update");
                                        }
                                    } else if ("move".equalsIgnoreCase(act.action)) {
                                        Integer id = act.id;
                                        if (id == null && act.name != null) id = namedObstacles.get(act.name);
                                        if (id != null) {
                                            boolean moved = world.moveObstacle(id, act.dx != null ? act.dx : 0, act.dy != null ? act.dy : 0);
                                            System.out.println("Moved obstacle id=" + id + " result=" + moved);
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Failed to apply obstacle action: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }

            // Group survivors based on the regional area the ended up in and breed new creatures from them. 
            // This allows for more diverse survival strategies to emerge since different areas may require 
            // different adaptations to survive, whereas if we just picked the top X survivors regardless of 
            // position we might end up with a less diverse gene pool that is only adapted to surviving in one 
            // specific area of the world
            List<Creature> hotRegionSurvivors = new ArrayList<>();
            List<Creature> coldRegionSurvivors = new ArrayList<>();
            List<Creature> dryRegionSurvivors = new ArrayList<>();
            List<Creature> wetRegionSurvivors = new ArrayList<>();
            
            Set<Creature> survivors = world.getCreatures();
            for (Creature creature : survivors) {
                Coords position = creature.getCurrentPosition();
                Climate climate = world.getClimateOnCoords(position);
                switch (climate) {
                    case HOT -> hotRegionSurvivors.add(creature);
                    case COLD -> coldRegionSurvivors.add(creature);
                    case DRY -> dryRegionSurvivors.add(creature);
                    case WET -> wetRegionSurvivors.add(creature);
                }
            }

            world.clearWorldOfCreatures();

            // For now eliminate the gender mating criteria and just mate randomly across all survivors 
            // in the generation to give the best chance for diverse gene combinations and interesting 
            // adaptations to emerge, but in the future we could consider adding it back in or even adding 
            // more complex mating criteria based on traits or behaviors if we find that it would lead to 
            // more interesting outcomes

            repopulateArea(hotRegionSurvivors, Climate.HOT, simulation);
            repopulateArea(coldRegionSurvivors, Climate.COLD, simulation);
            repopulateArea(dryRegionSurvivors, Climate.DRY, simulation);
            repopulateArea(wetRegionSurvivors, Climate.WET, simulation);

            // List<Creature> males = new ArrayList<>();
            // List<Creature> females = new ArrayList<>();
            
            // for (Creature creature : survivors) {
            //     if (creature.getGender() == 0) females.add(creature);
            //     else males.add(creature);
            // }
            
            // world.clearWorldOfCreatures();

            // for (int i = 0; i < maxPopulation; i++) {
            //     Creature parent1 = (Creature) Utilities.pickRandom(simulation.convertToObjects(males));
            //     Creature parent2 = (Creature) Utilities.pickRandom(simulation.convertToObjects(females));
            //     Creature.birthCreature(world, parent1, parent2, numberOfGenes, mutationEnabled, mutationRate, mutationAddConnectionRate, mutationRemoveConnectionRate);
            // }
            // world.doCreatureBrainScan();

            if (world.getCreatureCount() == 0) {
                System.out.println("All creatures died out at generation " + gen + ". Ending simulation.");
                break;
            }
        }
    }


    private static void repopulateArea(List<Creature> survivors, Climate climate, Simulation simulation) {
        if (survivors.size() > 1) { // need at least 2 survivors to breed
            for (int i = 0; i < world.getMaxPopulation() / 4; i++) {
                Creature parent1 = (Creature) Utilities.pickRandom(simulation.convertToObjects(survivors));
                Creature parent2 = (Creature) Utilities.pickRandom(simulation.convertToObjects(survivors));

                CreationOfNewborn creationContext = new CreationOfNewborn(world, parent1, parent2, config, climate);
                Creature.birthCreature(creationContext);
            }
        }
    }

    private static void saveSnapshot(Simulation simulation, int gen, int step, String outputDir) {
        try {
            BufferedImage img = new BufferedImage(WORLD_WIDTH, WORLD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            simulation.setSize(WORLD_WIDTH, WORLD_HEIGHT);
            simulation.paintComponent(g2);
            g2.dispose();
            File out = new File(outputDir, String.format("gen-%s.png", gen+step));
            ImageIO.write(img, "png", out);
        } catch (Exception e) {
            System.err.println("Failed to write snapshot: " + e.getMessage());
        }
    }

}
