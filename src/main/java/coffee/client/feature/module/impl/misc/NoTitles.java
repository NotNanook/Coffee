/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.CoffeeMain;
import coffee.client.feature.gui.notifications.Notification;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.event.impl.PacketEvent;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

public class NoTitles extends Module {
    long blocked = 0L;
    Notification lastShown = null;

    public NoTitles() {
        super("NoTitles", "Completely removes any titles from rendering", ModuleType.MISC);
    }

    @MessageSubscription
    void onA(PacketEvent.Received pe) {
        if (pe.getPacket() instanceof TitleS2CPacket) {
            blocked++;
            // create new notif if old one expired
            if (lastShown.creationDate + lastShown.duration < System.currentTimeMillis()) {
                lastShown = Notification.create(6000, "NoTitles", false, Notification.Type.SUCCESS, "Blocked " + blocked + " titles");
            }
            // else just set the current notif to our shit
            else {
                lastShown.contents[0] = "Blocked " + blocked + " titles";
            }
            pe.setCancelled(true);
        } else if (pe.getPacket() instanceof SubtitleS2CPacket || pe.getPacket() instanceof TitleFadeS2CPacket) {
            pe.setCancelled(true);
            CoffeeMain.client.inGameHud.setDefaultTitleFade();
        }
    }
}
