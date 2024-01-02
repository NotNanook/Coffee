package coffee.client.feature.module.impl.misc.itemtracker;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackWrapper implements Comparable<ItemStackWrapper>{
    public ItemStack itemStack;
    public int ticksLastSeen;

    public ItemStackWrapper(ItemStack itemStack, int ticksLastSeen) {
        this.itemStack = itemStack;
        this.ticksLastSeen = ticksLastSeen;
    }

    @Override
    public int compareTo(@NotNull ItemStackWrapper o) {
        return Integer.compare(this.ticksLastSeen, o.ticksLastSeen);
    }
}
