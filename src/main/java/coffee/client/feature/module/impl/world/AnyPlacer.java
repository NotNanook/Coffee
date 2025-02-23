/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.world;

import coffee.client.CoffeeMain;
import coffee.client.feature.config.annotation.Setting;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.event.impl.MouseEvent;
import coffee.client.helper.util.Rotations;
import coffee.client.helper.util.Utils;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class AnyPlacer extends Module {
    @Setting(name = "Height offset", description = "Offsets the placed entity in the Y direction", min = -5, max = 5, precision = 1)
    double heightOffset = 1;

    public AnyPlacer() {
        super("AnyPlacer", "Places spawn eggs with infinite reach (requires creative)", ModuleType.WORLD);
    }

    @MessageSubscription
    void on(MouseEvent me) {
        if (me.getButton() == 1) {
            ItemStack sex = CoffeeMain.client.player.getMainHandStack();
            if (sex.getItem() instanceof SpawnEggItem) {
                me.setCancelled(true);
                Vec3d rotationVector = Rotations.getRotationVector(Rotations.getClientPitch(), Rotations.getClientYaw());
                EntityHitResult raycast = ProjectileUtil.raycast(
                    client.player,
                    CoffeeMain.client.player.getCameraPosVec(0),
                    CoffeeMain.client.player.getCameraPosVec(0).add(rotationVector.multiply(500)),
                    client.player.getBoundingBox().stretch(rotationVector.multiply(500)).expand(1, 1, 1),
                    Entity::isAttackable,
                    500 * 500
                );
                Vec3d spawnPos;
                if (raycast != null && raycast.getEntity() != null) {
                    spawnPos = raycast.getPos();
                } else {
                    HitResult hr = CoffeeMain.client.player.raycast(500, 0, true);
                    spawnPos = hr.getPos();
                }
                spawnPos = spawnPos.add(0, heightOffset, 0);
                NbtCompound entityTag = sex.getOrCreateSubNbt("EntityTag");
                NbtList nl = new NbtList();
                nl.add(NbtDouble.of(spawnPos.x));
                nl.add(NbtDouble.of(spawnPos.y));
                nl.add(NbtDouble.of(spawnPos.z));
                entityTag.put("Pos", nl);
                CreativeInventoryActionC2SPacket a = new CreativeInventoryActionC2SPacket(
                    Utils.Inventory.slotIndexToId(CoffeeMain.client.player.getInventory().selectedSlot),
                    sex
                );
                Objects.requireNonNull(CoffeeMain.client.getNetworkHandler()).sendPacket(a);
                BlockHitResult bhr = new BlockHitResult(CoffeeMain.client.player.getPos(), Direction.DOWN, BlockPos.ofFloored(CoffeeMain.client.player.getPos()), false);
                PlayerInteractBlockC2SPacket ib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr, Utils.increaseAndCloseUpdateManager(CoffeeMain.client.world));
                CoffeeMain.client.getNetworkHandler().sendPacket(ib);
            }
        }
    }
}
