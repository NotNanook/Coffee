/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class InfChatLength extends Module {

    public InfChatLength() {
        super("InfChatLength", "Completely removes the length limit for the chat (Server side too, be careful!)", ModuleType.MISC);
    }
}
