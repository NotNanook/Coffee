package coffee.client.feature.module.impl.misc;

import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.feature.module.impl.misc.itemtracker.TrackedItems;
import coffee.client.helper.util.Utils;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.List;

public class ItemTracker extends Module {

    @Getter
    private static HashMap<String, TrackedItems> playerMap = new HashMap<>();

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
}
