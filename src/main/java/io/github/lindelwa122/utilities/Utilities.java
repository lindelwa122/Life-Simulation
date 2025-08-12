package io.github.lindelwa122.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utilities {
    private Utilities() {
        throw new IllegalStateException("Utility class");
    }

    private static Random r = new Random();

    public static Object pickRandom(List<Object> list) {
        int rand = r.nextInt(list.size());
        return list.get(rand);
    }

    public static Set<Object> pickRandom(List<Object> list, int limit) {
        Set<Object> picked = new HashSet<>();

        while (picked.size() < limit) {
            Object p = pickRandom(list);
            picked.add(p);
        }

        return picked;
    }

    public static int random(int start, int end) {
        return r.nextInt(start, end);
    }

    public static int random(int end) {
        return random(0, end);
    }
}
