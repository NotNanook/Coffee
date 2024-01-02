package coffee.client.feature.module.impl.misc.itemtracker;

import coffee.client.helper.event.impl.LoreQueryEvent;
import coffee.client.helper.util.LimitedSizeList;
import me.x150.jmessenger.MessageSubscription;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

import java.util.EnumMap;
import java.util.Iterator;

public class TrackedItems {

    final private int ticksToTrack = 20 * 60 * 2;

    String playerName;

    public EnumMap<EquipmentSlot, ItemStack> armorItems;
    public LimitedSizeList<ItemStackWrapper> storageItems;

    public TrackedItems(String name) {
        this.playerName = name;

        this.armorItems = new EnumMap<>(EquipmentSlot.class);
        this.storageItems = new LimitedSizeList<>(36); // This includes the hotbar
    }

    public TrackedItems(String name, Iterable<ItemStack> armorItems, ItemStack item) {
        this.playerName = name;

        this.armorItems = new EnumMap<>(EquipmentSlot.class);
        for(ItemStack itemStack : armorItems) {
            if(itemStack.getItem() instanceof ArmorItem armorItem) {
                this.armorItems.put(armorItem.getSlotType(), itemStack);
            }
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.armorItems.putIfAbsent(slot, ItemStack.EMPTY);
        }

        // Because the currently held item is definitely new, we create a new ItemStackWrapper object for that item
        this.storageItems = new LimitedSizeList<>(36);
        if(item != ItemStack.EMPTY && item.getItem() != Items.AIR) {
            this.storageItems.add(new ItemStackWrapper(item, 0));
        }
    }

    public void updateItems(Iterable<ItemStack> armorItems, ItemStack activeItemStack) {

        for(ItemStack itemStack : armorItems) {
            if(itemStack.getItem() instanceof ArmorItem armorItem) {
                this.armorItems.put(armorItem.getSlotType(), itemStack);
            }
        }

        // When the gui is shown, the most recently captured item should show in the hotbar from left to right
        // The others should be shown in the other inventory spaces left to right, top to bottom
        // That's why ItemStackWrapper implements Comparable (only sort when It's supposed to be rendered)
        // Find out if the item is already being tracked then set that items time to 0, else add new Item to list

        boolean isAir = activeItemStack.getItem().equals(Items.AIR);

        // Add one tick to all items and remove all Items that haven't been seen for more than two minutes (20 ticks per second * 60 seconds * 2 minutes)
        Iterator<ItemStackWrapper> iterator = storageItems.iterator();
        while (iterator.hasNext()) {
            ItemStackWrapper element = iterator.next();
            ItemStack currentItemStack = element.itemStack;
            if(++element.ticksLastSeen >= ticksToTrack || element.itemStack.getCount() == 0) {
                iterator.remove();
            }

            // If item is air then there is no reason to compare anything
            if(isAir) continue;

            boolean isSameType = activeItemStack.getItem().equals(currentItemStack.getItem());
            if(isSameType && activeItemStack.getEnchantments().equals(currentItemStack.getEnchantments())) {
                element.itemStack = activeItemStack;
                element.ticksLastSeen = 0;
                return;
            }
        }

        // If activeItem isn't in storageItems and not air (important for the first element if its no item)
        if(!isAir) storageItems.add(new ItemStackWrapper(activeItemStack, 0));
    }
}
