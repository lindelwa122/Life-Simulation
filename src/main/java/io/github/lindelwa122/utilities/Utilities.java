package io.github.lindelwa122.utilities;

import java.util.List;

public class Utilities {
    public static Object pickRandom(List<Object> list) {
        int rand = (int) Math.floor(Math.random() * list.size());
        return list.get(rand);
    }

    public static int random(int start, int end) {
        return (int) Math.floor(Math.random() * end-start) + start;
    }

    public static int random(int end) {
        return random(0, end);
    }
}
