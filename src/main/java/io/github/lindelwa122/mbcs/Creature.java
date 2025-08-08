package io.github.lindelwa122.mbcs;

import java.util.List;

import io.github.lindelwa122.cellularStructure.CellularMakeUp;
import io.github.lindelwa122.cellularStructure.DietaryOptions;
import io.github.lindelwa122.cellularStructure.Element;
import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.coords.Coords;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class Creature {
    private CellularMakeUp makeUp;
    private Element type;
    private DietaryOptions dietaryOptions;

    private Hydration hydration = new Hydration(new InternalValue());
    private Hunger hunger = new Hunger(new InternalValue());
    private Age age = new Age(new InternalValue());
    private Libido libido = new Libido(new InternalValue());
    private Fear fear = new Fear(new InternalValue());
    private Energy energy = new Energy(new InternalValue(100));
    private Health health = new Health(new InternalValue(100));

    private int oscillator = Utilities.random(1);
    private int oscillatorPeriod = (int) Utilities.pickRandom(List.of(10, 15, 30, 50));

    private Coords previousPosition = new Coords();
    private Coords currentPosition = new Coords();
    private World world;
    private int gender;

    public Creature(CellularMakeUp makeUp, Element type, DietaryOptions dietaryOptions, int gender, World world) {
        this.makeUp = makeUp;
        this.type = type;
        this.dietaryOptions = dietaryOptions;
        this.world = world;
        this.gender = gender;
    }

    public static boolean birthCreature(World world) {
        Element hydrex = new Element(FundamentalElements.HYDREX, Utilities.random(100));
        Element ignyra = new Element(FundamentalElements.IGNYRA, Utilities.random(100));
        Element xeraphin = new Element(FundamentalElements.XERAPHIN, Utilities.random(100));
        Element humidra = new Element(FundamentalElements.HUMIDRA, Utilities.random(100));
        Element cryonel = new Element(FundamentalElements.CRYONEL, Utilities.random(100));

        CellularMakeUp makeUp = new CellularMakeUp(hydrex, ignyra, xeraphin, humidra, cryonel, null, null, null, null);
        
        Element type = (Element) Utilities.pickRandom(List.of(
            FundamentalElements.CARNYXIS,
            FundamentalElements.PREDONIX
        ));

        DietaryOptions dietaryOptions = (DietaryOptions) Utilities.pickRandom(List.of(
            DietaryOptions.A_EATER,
            DietaryOptions.B_EATER,
            DietaryOptions.C_EATER,
            DietaryOptions.D_EATER,
            DietaryOptions.OPP
        ));

        int gender = Utilities.random(1);

        Creature c = new Creature(makeUp, type, dietaryOptions, gender, world);
        return world.addCreature(c);
    }

    // GETTERS
    public int getHydrationLevel() {
        return this.hydration.value().getValue();
    }

    public int getHungerLevel() {
        return this.hunger.value().getValue();
    }

    public int getOscillator() {
        return this.oscillator;
    }

    public int getAge() {
        return this.age.value().getValue();
    }

    public Coords getCurrentPosition() {
        return this.currentPosition;
    }

    public Coords getPreviousPosition() {
        return this.previousPosition;
    }

    public int getWorldHeight() {
        return this.world.getHeight() / World.POINT_SIZE;
    }

    public int getWorldWidth() {
        return this.world.getWidth() / World.POINT_SIZE;
    }

    public Element getType() {
        return this.type;
    }

    public int getLibido() {
        return this.libido.value().getValue();
    }

    public int getFear() {
        return this.fear.value().getValue();
    }

    public int getEnergy() {
        return this.energy.value().getValue();
    }

    public int getHealth() {
        return this.health.value().getValue();
    }

    public int getGender() {
        return this.gender;
    }

    public World getWorld() {
        return this.world;
    }

    // SETTERS
    public void setOscillatorPeriod(int period) {
        this.oscillatorPeriod = period;
    }

    public void setLibido(int libido) {
        this.libido.value().setValue(libido);
    }

    public void setFear(int fear) {
        this.fear.value().setValue(fear);
    }

    public void setCurrentPosition(Coords position) {
        this.previousPosition = currentPosition;
        this.currentPosition = position;
    }
}
