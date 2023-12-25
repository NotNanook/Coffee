package coffee.client.feature.module.impl.movement;

import coffee.client.CoffeeMain;
import coffee.client.feature.config.DoubleSetting;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.util.Utils;
import coffee.client.mixin.ClientPlayerInteractionManagerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Set;

public class AutoMLG extends Module {
    final DoubleSetting falldistance = this.config.create(new DoubleSetting.Builder(10).name("Fall Distance")
            .description("At what height AutoMLG should kick in")
            .min(7)
            .max(50)
            .precision(1)
            .get());

    final Set<Item> itemsOfInterest = Set.of(
            Items.POWDER_SNOW_BUCKET,
            Items.COBWEB,
            Items.SLIME_BLOCK,
            Items.SCAFFOLDING,
            Items.TWISTING_VINES,
            Items.HAY_BLOCK
    );

    final Set<Block> blocksOfInterest = Set.of(
            Blocks.WATER,
            Blocks.POWDER_SNOW,
            Blocks.COBWEB,
            Blocks.SLIME_BLOCK,
            Blocks.SCAFFOLDING,
            Blocks.TWISTING_VINES,
            Blocks.HAY_BLOCK
    );

    public AutoMLG() {super("AutoMLG", "Automatically does a cobweb/water MLG", ModuleType.MOVEMENT);}

    @Override
    public void onFastTick() {

        ItemStack itemStack = client.player.getMainHandStack();
        Item mainHandItem = itemStack.getItem();

        if (!client.player.isOnGround() && mainHandItem != null && client.player.fallDistance > falldistance.getValue() && !client.player.getAbilities().flying && !client.player.isTouchingWater() && !client.player.isCreative() && (itemsOfInterest.contains(mainHandItem) || Utils.buckets.contains(mainHandItem))) {
            Vec3d playerPos = client.player.getPos();
            Vec3d lookVec = client.player.getRotationVec(1.0F);
            Vec3d endVec = playerPos.add(lookVec.multiply(3));
            BlockHitResult hitResult = client.world.raycast(new RaycastContext(playerPos, endVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, client.player));
            BlockPos hitBlockPos = hitResult.getBlockPos();
            Block hitBlock = client.world.getBlockState(hitBlockPos).getBlock();
            if (hitResult.getType() == HitResult.Type.BLOCK && !blocksOfInterest.contains(hitBlock)) {
                Utils.doPlacement(mainHandItem, Hand.MAIN_HAND, hitResult);
            }
        }
    }
}
