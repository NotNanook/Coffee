/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.misc;

import coffee.client.CoffeeMain;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.util.Timer;
import coffee.client.helper.util.Utils;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.GameMode;

import java.util.*;

public class GamemodeAlert extends Module {
    static Map<UUID, GameMode> seen = new HashMap<>();
    Timer updater = new Timer();

    public GamemodeAlert() {
        super("GamemodeAlert", "Alerts you when someone changes their gamemode", ModuleType.MISC);
    }

    @Override
    public void tick() {
        if (!updater.hasExpired(4_000L)) {
            return; // 4 sec update time
        }
        updater.reset();
        List<UUID> seenPlayers = new ArrayList<>();
        for (PlayerListEntry playerListEntry : client.getNetworkHandler().getPlayerList()) {
            UUID id = playerListEntry.getProfile().getId();
            GameMode gm = playerListEntry.getGameMode();
            if (gm == null) {
                continue;
            }
            seenPlayers.add(id);
            if (!seen.containsKey(id)) {
                seen.put(id, gm);
            } else {
                GameMode gameMode = seen.get(id);
                if (gameMode != gm && !Objects.equals(CoffeeMain.client.player.getName().getString(), playerListEntry.getProfile().getName())) {
                    String dName = playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName().getString() : playerListEntry.getProfile().getName();
                    Utils.Logging.warn(String.format(
                        "[Gamemode change] %s§r just changed their gamemode: %s -> %s",
                        dName,
                        StringUtil.capitalize(gameMode.getName()),
                        StringUtil.capitalize(gm.getName())
                    ));
                    seen.put(id, gm);
                }
            }
        }
        for (UUID uuid : new ArrayList<>(seen.keySet())) {
            if (!seenPlayers.contains(uuid)) {
                seen.remove(uuid);
            }
        }
    }
}
