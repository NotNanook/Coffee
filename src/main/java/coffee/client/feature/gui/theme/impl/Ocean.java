/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.gui.theme.impl;

import coffee.client.feature.gui.theme.Theme;

import java.awt.Color;

public class Ocean implements Theme {

    static final Color accent = new Color(0x3AD99D);
    static final Color module = new Color(0xFF171E1F, true);
    static final Color tooltip = new Color(30, 30, 30);
    static final Color active = new Color(21, 157, 204, 255);
    static final Color speed = accent;
    static final Color inactive = new Color(66, 66, 66, 255);

    @Override
    public String getName() {
        return "Ocean";
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
    public Color getTooltip() { return tooltip; }

    @Override
    public Color getSpeed() {return speed; }
}
