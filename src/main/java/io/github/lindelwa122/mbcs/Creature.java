package io.github.lindelwa122.mbcs;


import java.util.List;

import io.github.lindelwa122.cellularStructure.CellularMakeUp;
import io.github.lindelwa122.cellularStructure.DietaryOptions;
import io.github.lindelwa122.cellularStructure.Element;
import io.github.lindelwa122.cellularStructure.FundamentalElements;
import io.github.lindelwa122.utilities.Utilities;
import io.github.lindelwa122.world.World;

public class Creature {
    private CellularMakeUp makeUp;
    private Element type;
    private DietaryOptions dietaryOptions;

    private Hydration hydration = new Hydration(new InternalValue());

    public Creature(CellularMakeUp makeUp, Element type, DietaryOptions dietaryOptions) {
        this.makeUp = makeUp;
        this.type = type;
        this.dietaryOptions = dietaryOptions;
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

        Creature c = new Creature(makeUp, type, dietaryOptions);
        return world.addCreature(c);
    }

    // GETTERS
    public int getHydrationLevel() {
        return this.hydration.value().getValue();
    }
}
