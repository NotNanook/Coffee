/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.movement;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class NoLevitation extends Module {

    public NoLevitation() {
        super("NoLevitation", "Prevents the levitation effect from working", ModuleType.MOVEMENT);
    }
}
