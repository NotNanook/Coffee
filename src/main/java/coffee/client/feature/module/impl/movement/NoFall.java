/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.movement;

import coffee.client.feature.config.DoubleSetting;
import coffee.client.feature.config.EnumSetting;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.event.impl.PacketEvent;
import coffee.client.mixin.network.IPlayerMoveC2SPacketMixin;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * @see IPlayerMoveC2SPacketMixin
 */
public class NoFall extends Module {

    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.OnGround).name("Mode")
        .description("How to spoof packets (packet drowns the others out, use with caution)")
        .get());
    final DoubleSetting fallDist = this.config.create(new DoubleSetting.Builder(3).name("Fall distance")
        .description("How much to fall before breaking the fall")
        .min(1)
        .max(10)
        .precision(1)
        .get());
    public boolean enabled = true;

    public NoFall() {
        super("NoFall", "Prevents fall damage", ModuleType.MOVEMENT);

        this.fallDist.showIf(() -> mode.getValue() != Mode.OnGround);
    }

    @MessageSubscription
    void onPacket(PacketEvent.Sent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (mode.getValue() == Mode.OnGround) {
                if (enabled) {
                    ((IPlayerMoveC2SPacketMixin) event.getPacket()).setOnGround(true);
                }
            }
        }
    }

    @Override
    public void tick() {
        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        if (client.player.fallDistance > fallDist.getValue()) {
            switch (mode.getValue()) {
                case Packet -> client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                case BreakFall -> {
                    client.player.setVelocity(0, 0.1, 0);
                    client.player.fallDistance = 0;
                }
            }
        }
    }

    @Override
    public String getContext() {
        return mode.getValue().name();
    }

    public enum Mode {
        OnGround, Packet, BreakFall
    }
}
