/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.movement;

import coffee.client.feature.config.DoubleSetting;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class VanillaSpeed extends Module {

    public final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(3).name("Speed")
        .description("The speed multiplier to apply")
        .min(1)
        .max(10)
        .precision(3)
        .get());

    public VanillaSpeed() {
        super("VanillaSpeed", "Gives you an extreme speed boost", ModuleType.MOVEMENT);
    }
}
