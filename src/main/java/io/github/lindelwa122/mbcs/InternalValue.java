package io.github.lindelwa122.mbcs;

public class InternalValue {
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;

    private int value;

    public InternalValue(int initValue) {
        this.value = initValue;
    }

    public InternalValue() {
        this(0);
    }

    public void increment() {
        if (this.value < MAX_VALUE) value++;
    }

    public void incrementByValue(int value) {
        if (this.value + value > MAX_VALUE) {
            this.value = MAX_VALUE;
            return;
        }
        this.value += value;
    }

    public void decrement() {
        if (this.value > MIN_VALUE) value--;
    }

    public void decrementByValue(int value) {
        if (this.value - value < MIN_VALUE) {
            this.value = MIN_VALUE;
            return;
        }
        this.value -= value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int incrementAndGetValue() {
        this.increment();
        return this.getValue();
    }

    public int decrementAndGetValue() {
        this.decrement();
        return this.getValue();
    }
}
