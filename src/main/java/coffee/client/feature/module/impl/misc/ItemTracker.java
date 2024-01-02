package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.feature.module.impl.misc.itemtracker.ItemStackWrapper;
import coffee.client.feature.module.impl.misc.itemtracker.NoInteractInventory;
import coffee.client.feature.module.impl.misc.itemtracker.TrackedItems;
import coffee.client.helper.event.impl.LoreQueryEvent;
import coffee.client.helper.util.Utils;
import lombok.Getter;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemTracker extends Module {

    @Getter
    private static final HashMap<String, TrackedItems> playerMap = new HashMap<>();
    private static final HashMap<Item, Integer> currentInventory = new HashMap<>();


    public ItemTracker() { super("ItemTracker", "Tracks the items of other players", ModuleType.MISC); }

    @Override
    public void enable() {
        for (final PlayerEntity player: client.world.getPlayers()) {
            String playerName = player.getDisplayName().getString().toLowerCase();
            if(Utils.Players.isPlayerNameValid(playerName)) {
                playerMap.put(playerName, new TrackedItems(playerName));
            }
        }
    }

    @Override
    public void disable() {
        playerMap.clear();
    }

    @Override
    public void tick() {
        List<AbstractClientPlayerEntity> cleanedRenderedPlayers = client.world.getPlayers().stream()
                .filter(player -> Utils.Players.isPlayerNameValid(player.getDisplayName().getString().toLowerCase()))
                .toList();

        playerMap.entrySet().removeIf(entry -> entry.getValue().storageItems.elements.isEmpty() && entry.getValue().armorItems.values().isEmpty());

        for(AbstractClientPlayerEntity renderedPlayer : cleanedRenderedPlayers) {
            String playerName = renderedPlayer.getName().getString().toLowerCase();
            if(!playerMap.containsKey(playerName)) {
                playerMap.put(playerName, new TrackedItems(playerName, renderedPlayer.getArmorItems(), renderedPlayer.getActiveItem()));
            } else {
                playerMap.get(playerName).updateItems(renderedPlayer.getArmorItems(), renderedPlayer.getHandItems().iterator().next());
            }
        }
    }

    @MessageSubscription
    void onLoreQuery(LoreQueryEvent e) {
        if(this.isEnabled() && client.currentScreen instanceof NoInteractInventory) {
            e.addClientLore("Last Seen: " + currentInventory.getOrDefault(e.getSource().getItem(), 0)/20 + "s");
        }
    }

    public static void setCurrentInventory(ArrayList<ItemStackWrapper> sortedItems) {
        currentInventory.clear();
        for(ItemStackWrapper itemStackWrapper : sortedItems) {
            currentInventory.put(itemStackWrapper.itemStack.getItem(), itemStackWrapper.ticksLastSeen);
        }
    }
}
