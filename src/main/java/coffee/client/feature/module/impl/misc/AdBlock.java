/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.event.impl.PacketEvent;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AdBlock extends Module {
    long blocked = 0;

    public AdBlock() {
        super("AdBlock", "Blocks the /ad command on minehut from sending to you", ModuleType.MISC);
    }

    @MessageSubscription
    void onP(PacketEvent.Received pe) {
        if (pe.getPacket() instanceof GameMessageS2CPacket msg) {
            if (msg.content().getString().contains("[AD]")) {
                pe.setCancelled(true);
                blocked++;
            }
        }
    }

    @Override
    public String getContext() {
        return String.valueOf(blocked);
    }
}
