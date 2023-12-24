/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.gui.theme;

import coffee.client.feature.gui.theme.impl.BlackWhite;
import coffee.client.feature.gui.theme.impl.Ocean;

public class ThemeManager {
    static final Theme bestThemeEver = new Ocean();
    static final Theme betterTheme = new BlackWhite();

    public static Theme getMainTheme() {
        return betterTheme;
    }
}
