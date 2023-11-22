package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data;

public class BooleanFlag {
    private boolean flag = false;

    public void flag() {
        flag = true;
    }

    public boolean getAndReset() {
        boolean toReturn = flag;
        if (flag)
            flag = false;

        return toReturn;
    }
}
