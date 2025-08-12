package io.github.lindelwa122.neurons;

import java.util.Objects;

import io.github.lindelwa122.mbcs.Creature;

public abstract class Neuron {
    protected Creature creature;
    protected String id;

    protected Neuron(Creature creature, String id) {
        this.creature = creature;
        this.id = id;
    }

    public abstract double value();

    public void resetCreature(Creature creature) {
        this.creature = creature;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Neuron neuron = (Neuron) obj;
        return Objects.equals(id, neuron.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
