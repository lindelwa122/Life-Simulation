package io.github.lindelwa122.utilities;

import java.util.List;
import java.util.Random;

public class Utilities {
    private Utilities() {
        throw new IllegalStateException("Utility class");
    }

    private static Random r = new Random();

    public static Object pickRandom(List<Object> list) {
        int rand = r.nextInt(list.size());
        return list.get(rand);
    }

    public static int random(int start, int end) {
        return r.nextInt(start, end);
    }

    public static int random(int end) {
        return random(0, end);
    }
}
