/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module;

import coffee.client.CoffeeMain;
import coffee.client.feature.config.BooleanSetting;
import coffee.client.feature.config.DoubleSetting;
import coffee.client.feature.config.ModuleConfig;
import coffee.client.feature.gui.notifications.Notification;
import coffee.client.feature.module.impl.misc.ClientSettings;
import coffee.client.helper.event.EventSystem;
import coffee.client.helper.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.lang.annotation.Annotation;

public abstract class Module implements Comparable<Module> {

    protected static final MinecraftClient client = CoffeeMain.client;
    static int lastNotification = -1;
    public final ModuleConfig config;
    public final DoubleSetting keybind;
    @Getter
    private final String name;
    @Getter
    private final String description;
    private final ModuleType moduleType;
    private final BooleanSetting toasts;
    @Getter
    private boolean enabled = false;
    @Getter
    @Setter
    private boolean disabled = false;
    @Getter
    @Setter
    private String disabledReason = "";

    public Module(String n, String d, ModuleType type) {
        this.name = n;
        this.description = d;
        this.moduleType = type;
        this.config = new ModuleConfig();
        this.keybind = this.config.create(new DoubleSetting.Builder(-1).name("Keybind")
            .description("The keybind to toggle the module with")
            .min(-1)
            .max(65535)
            .precision(0)
            .get());
        boolean hasAnnotation = false;
        for (Annotation declaredAnnotation : this.getClass().getDeclaredAnnotations()) {
            if (declaredAnnotation.annotationType() == NoNotificationDefault.class) {
                hasAnnotation = true;
            }
        }
        this.toasts = this.config.create(new BooleanSetting.Builder(!hasAnnotation).name("Toasts").description("Whether to show enabled / disabled toasts").get());
    }

    public final void postModuleInit() {
        this.config.addSettingsFromAnnotations(this);
    }

    public final ModuleType getModuleType() {
        return moduleType;
    }

    public void tick() {}

    public void enable() {}

    public void disable() {}

    public void onWorldRender(MatrixStack matrices) {}

    public void onHudRender() {}

    public void postInit() {}

    public void onFastTick() {}

    public void onFastTick_NWC() {}

    public String getContext() {
        return null;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (isDisabled()) {
            this.enabled = false;
            return;
        }
        this.enabled = enabled;
        if (toasts.getValue()) {
            String s = (this.enabled ? "§aEn" : "§cDis") + "abled §r" + this.getName();
            if (ModuleRegistry.getByClass(ClientSettings.class).toggleStyle == ClientSettings.ToggleMode.Chat) {
                Utils.Logging.removeMessage(lastNotification);
                lastNotification = Utils.Logging.sendMessage(Text.literal(s));
            } else {
                Notification.create(1000, "Module toggle", Notification.Type.INFO, s);
            }
        }
        if (this.enabled) {
            EventSystem.manager.registerSubscribers(this);
            this.enable();
        } else {
            this.disable();
            EventSystem.manager.unregister(this);
        }
    }

    @Override public int compareTo(Module otherModule) {
        return this.name.compareTo(otherModule.name);
    }
}
