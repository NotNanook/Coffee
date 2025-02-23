/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.gui.screen;

import coffee.client.CoffeeMain;
import coffee.client.feature.gui.ParticleRenderer;
import coffee.client.feature.gui.element.impl.ButtonGroupElement;
import coffee.client.feature.gui.notifications.Notification;
import coffee.client.feature.gui.screen.base.AAScreen;
import coffee.client.helper.CompatHelper;
import coffee.client.helper.font.FontRenderers;
import coffee.client.helper.font.adapter.FontAdapter;
import coffee.client.helper.render.MSAAFramebuffer;
import coffee.client.helper.render.PlayerHeadResolver;
import coffee.client.helper.render.Renderer;
import coffee.client.helper.render.Texture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL40C;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class HomeScreen extends AAScreen {
    static final double padding = 6;
    private static HomeScreen instance;
    final ParticleRenderer prend = new ParticleRenderer(600);
    Texture currentAccountTexture = new Texture("dynamic/tex_currentaccount_home");
    boolean loaded = false;
    boolean currentAccountTextureLoaded = false;
    UUID previousChecked = null;
    boolean showedCompatWarn = false;

    private HomeScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
    }

    public static HomeScreen instance() {
        if (instance == null) {
            instance = new HomeScreen();
        }
        return instance;
    }

    void load() {
        loaded = true;
        try {
            updateCurrentAccount(() -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        clearWidgets();
        initWidgets();
    }

    void initWidgets() {
        double entireWidth = 60 * 7;
        double startX = padding * 2 + 20 + padding;
        double endX = width - padding * 2;
        double weHave = endX - startX;
        entireWidth = Math.min(entireWidth, weHave);
        ButtonGroupElement bge = new ButtonGroupElement(
            startX,
            this.height - padding * 2 - 20,
            entireWidth,
            20,
            ButtonGroupElement.LayoutDirection.RIGHT,
            new ButtonGroupElement.ButtonEntry("Singleplayer", () -> CoffeeMain.client.setScreen(new SelectWorldScreen(this))),
            new ButtonGroupElement.ButtonEntry("Multiplayer", () -> CoffeeMain.client.setScreen(new MultiplayerScreen(this))),
            new ButtonGroupElement.ButtonEntry("Realms", () -> CoffeeMain.client.setScreen(new RealmsMainScreen(this))),
            new ButtonGroupElement.ButtonEntry("Alts", () -> CoffeeMain.client.setScreen(AltManagerScreen.instance(this))),
            new ButtonGroupElement.ButtonEntry("Options", () -> CoffeeMain.client.setScreen(new OptionsScreen(this, CoffeeMain.client.options))),
            new ButtonGroupElement.ButtonEntry("Vanilla", () -> CoffeeMain.client.setScreen(new TitleScreen(false))),
            new ButtonGroupElement.ButtonEntry("Quit", CoffeeMain.client::scheduleStop)
        );
        addChild(bge);
    }

    @Override
    protected void initInternal() {
        if (CompatHelper.wereAnyFound() && !showedCompatWarn && client.currentScreen == this) {
            showedCompatWarn = true;
            client.setScreen(new NotificationScreen(
                Notification.Type.WARNING,
                "Compatability",
                "Compatibility issues found, some features might not be available",
                this
            ));
        }
        initWidgets();
        if (loaded) {
            updateCurrentAccount(() -> {}); // already loaded this instance, refresh on the fly
        } else {
            load();
        }
    }

    void updateCurrentAccount(Runnable callback) {
        UUID uid = CoffeeMain.client.getSession().getProfile().getId();
        if (previousChecked != null && previousChecked.equals(uid)) {
            callback.run();
            return;
        }
        previousChecked = uid;
        this.currentAccountTexture = PlayerHeadResolver.resolve(uid);
        currentAccountTextureLoaded = true;
        callback.run();
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {

        coffee.client.helper.render.textures.Texture.BACKGROUND.bind();
        Renderer.R2D.renderTexture(stack, 0, 0, width, height, 0, 0, width, height, width, height);
        RenderSystem.defaultBlendFunc();
        prend.render(stack);

        double origW = 1024, origH = 1024;
        double newH = 20;
        double per = newH / origH;
        double newW = origW * per;
        Renderer.R2D.renderRoundedQuadWithShadow(
            stack,
            new Color(0, 0, 0, 200),
            padding,
            height - padding - padding - 20 - padding,
            width - padding,
            height - padding,
            10,
            20
        );
        coffee.client.helper.render.textures.Texture.ICON.bind();
        Renderer.R2D.renderTexture(stack, padding * 2, height - padding * 2 - newH, newW, newH, 0, 0, newW, newH, newW, newH);
        super.renderInternal(stack, mouseX, mouseY, delta); // render bottom row widgets

        double texDim = 20;
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Renderer.R2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, width - padding - texDim, padding, width - padding, padding + texDim, 3, 10);

        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
        RenderSystem.setShaderTexture(0, currentAccountTextureLoaded ? currentAccountTexture : DefaultSkinHelper.getTexture());
        if (currentAccountTextureLoaded) {
            Renderer.R2D.renderTexture(stack, width - padding - texDim, padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
        } else {
            Renderer.R2D.renderTexture(stack, width - padding - texDim, padding, texDim, texDim, 8, 8, 8, 8, 64, 64);
        }
        RenderSystem.defaultBlendFunc();

        FontAdapter fa = FontRenderers.getRenderer();
        String uname = CoffeeMain.client.getSession().getUsername();
        double unameWidth = fa.getStringWidth(uname);
        fa.drawString(stack, uname, width - padding - texDim - padding - unameWidth, padding + texDim / 2d - fa.getFontHeight() / 2d, 0xFFFFFF);
    }

}
