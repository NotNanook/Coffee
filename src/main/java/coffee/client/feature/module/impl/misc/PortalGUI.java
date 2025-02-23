/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class PortalGUI extends Module {

    public PortalGUI() {
        super("PortalGUI", "Allows you to open GUIs while being inside a portal", ModuleType.MISC);
    }
}
