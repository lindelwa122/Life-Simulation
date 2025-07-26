package io.github.lindelwa122.utilities;

import java.awt.Color;

public enum MostlyUsedColors {
    HOT(new Color(5, 250, 38, 50)),
    COLD(new Color(255, 255, 255, 50)),
    WET(new Color(5, 123, 250, 50)),
    DRY(new Color(250, 175, 5, 50));

    private final Color color;

    // Constructor for the enum
    private MostlyUsedColors(Color color) {
        this.color = color;
    }

    // Getter methods to access the values
    public Color getColor() {
        return this.color;
    }
}
