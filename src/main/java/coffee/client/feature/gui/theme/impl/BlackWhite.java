package coffee.client.feature.gui.theme.impl;

import coffee.client.feature.gui.theme.Theme;

import java.awt.*;

public class BlackWhite implements Theme {

    static final Color accent = new Color(0x3AD99D);
    static final Color module = new Color(15, 15, 15, 243);
    static final Color tooltip = new Color(25, 25, 25, 243);
    static final Color active = new Color(240, 240, 240);
    static final Color speed = active;
    static final Color inactive = new Color(66, 66, 66);

    @Override
    public String getName() {
        return "BlackWhite";
    }

    @Override
    public Color getAccent() {
        return accent;
    }

    @Override
    public Color getModule() {
        return module;
    }

    @Override
    public Color getActive() {
        return active;
    }

    @Override
    public Color getInactive() {
        return inactive;
    }

    @Override
    public Color getTooltip() {return tooltip; }

    @Override
    public Color getSpeed() { return speed; }
}
