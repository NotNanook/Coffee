package coffee.client.feature.module.impl.misc.itemtracker;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.UUID;

public class DummyPlayerEntity extends AbstractClientPlayerEntity {
    public DummyPlayerEntity(ClientWorld world, String name) {
        super(world, new GameProfile(UUID.randomUUID(), name));
    }
}
