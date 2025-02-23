/*
 * Copyright (c) 2023 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.render;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class UnfocusedCpu extends Module {
    public UnfocusedCpu() {
        super("UnfocusedCpu", "Prevents rendering of the game when the window is not focused", ModuleType.RENDER);
    }
}
