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
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;

public class AntiOffhandCrash extends Module {

    public AntiOffhandCrash() {
        super("AntiOffhandCrash", "Prevents you from getting crashed by OffhandCrash", ModuleType.MISC);
    }


    @MessageSubscription
    void on(PacketEvent.Received event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket) {
            if (((PlaySoundS2CPacket) event.getPacket()).getSound().value() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.setCancelled(true);
            }
        }
    }
}
