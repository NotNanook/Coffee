/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class AllowFormatCodes extends Module {

    public AllowFormatCodes() {
        super("AllowFormatCodes", "Allows you to type format codes with the paragraph symbol", ModuleType.MISC);
    }
}
