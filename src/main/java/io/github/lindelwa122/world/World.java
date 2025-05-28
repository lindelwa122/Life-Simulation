package io.github.lindelwa122.world;

import io.github.lindelwa122.coords.Coords;

public class World {
    private final int HEIGHT;
    private final int WIDTH;

    private final Coords DRY_CLIMATE_ORIGIN;
    private final Coords COLD_CLIMATE_ORIGIN;
    private final Coords WET_CLIMATE_ORIGIN;
    private final Coords HOT_CLIMATE_ORIGIN;

    public World(int height, int width) {
        this.HEIGHT = height;
        this.WIDTH = width;

        this.DRY_CLIMATE_ORIGIN = new Coords(0, 0);
        this.COLD_CLIMATE_ORIGIN = new Coords(width-1, 0);
        this.WET_CLIMATE_ORIGIN = new Coords(0, height-1);
        this.HOT_CLIMATE_ORIGIN = new Coords(height-1, width-1);
    }
}
