package io.github.lindelwa122.coords;

public record Coords(int x, int y) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords coords = (Coords) o;
        return this.x == coords.x && this.y == coords.y;
    }
}
