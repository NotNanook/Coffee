/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.command.impl;

import coffee.client.CoffeeMain;
import coffee.client.feature.command.Command;
import coffee.client.feature.command.argument.PlayerFromNameArgumentParser;
import coffee.client.feature.command.coloring.ArgumentType;
import coffee.client.feature.command.coloring.PossibleArgument;
import coffee.client.feature.command.coloring.StaticArgumentServer;
import coffee.client.feature.command.exception.CommandException;
import coffee.client.feature.module.ModuleRegistry;
import coffee.client.feature.module.impl.misc.ItemTracker;
import coffee.client.feature.module.impl.misc.itemtracker.DummyPlayerEntity;
import coffee.client.feature.module.impl.misc.itemtracker.ItemStackWrapper;
import coffee.client.feature.module.impl.misc.itemtracker.NoInteractInventory;
import coffee.client.feature.module.impl.misc.itemtracker.TrackedItems;
import coffee.client.helper.util.Utils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class Invsee extends Command {

    public Invsee() {
        super("Invsee", "Shows you the inventory of another player", "invsee", "isee");
    }

    @Override
    public PossibleArgument getSuggestionsWithType(int index, String[] args) {
        return StaticArgumentServer.serveFromStatic(
            index,
            new PossibleArgument(
                ArgumentType.STRING,
                Objects.requireNonNull(CoffeeMain.client.world)
                    .getPlayers()
                    .stream()
                    .map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName())
                    .toList()
                    .toArray(String[]::new)
            )
        );
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Provide target username");
        if(ModuleRegistry.getByClass(ItemTracker.class).isEnabled()) {
            String playerName = args[0].toLowerCase();
            if(!ItemTracker.getPlayerMap().containsKey(playerName)) {
                throw new CommandException("Invalid argument \"" + playerName + "\": Player not found");
            }

            DummyPlayerEntity dummy = new DummyPlayerEntity(client.world, playerName);
            PlayerInventory dummyInventory = dummy.getInventory();
            TrackedItems trackedItems = ItemTracker.getPlayerMap().get(playerName);

            EnumMap<EquipmentSlot, ItemStack> armorItems = trackedItems.armorItems;
            ArrayList<ItemStackWrapper> items = trackedItems.storageItems.elements;
            Collections.sort(items);

            dummyInventory.armor.set(0, armorItems.get(EquipmentSlot.FEET).copy());
            dummyInventory.armor.set(1, armorItems.get(EquipmentSlot.LEGS).copy());
            dummyInventory.armor.set(2, armorItems.get(EquipmentSlot.CHEST).copy());
            dummyInventory.armor.set(3, armorItems.get(EquipmentSlot.HEAD).copy());

            for(ItemStackWrapper item : items) {
                dummyInventory.insertStack(item.itemStack.copy());
            }

            Utils.TickManager.runOnNextRender(() -> CoffeeMain.client.setScreen(new NoInteractInventory(dummy)));
        } else {
            PlayerEntity player = new PlayerFromNameArgumentParser(true).parse(args[0]);
            Utils.TickManager.runOnNextRender(() -> CoffeeMain.client.setScreen(new InventoryScreen(player)));
        }
    }
}
