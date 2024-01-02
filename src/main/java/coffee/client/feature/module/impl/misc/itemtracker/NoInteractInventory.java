package coffee.client.feature.module.impl.misc.itemtracker;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class NoInteractInventory extends InventoryScreen {

    PlayerEntity trackedPlayer;

    public NoInteractInventory(PlayerEntity player) {
        super(player);
        this.trackedPlayer = player;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        drawEntity(context, i + 51, j + 75, 30, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.trackedPlayer);
    }
    
    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {}
    
}
