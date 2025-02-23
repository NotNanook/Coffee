/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package coffee.client.feature.module.impl.render;

import coffee.client.CoffeeMain;
import coffee.client.feature.config.DoubleSetting;
import coffee.client.feature.config.ListSetting;
import coffee.client.feature.config.annotation.Setting;
import coffee.client.feature.config.annotation.VisibilitySpecifier;
import coffee.client.feature.module.Module;
import coffee.client.feature.module.ModuleType;
import coffee.client.helper.render.Renderer;
import coffee.client.helper.util.Utils;
import coffee.client.helper.vertex.DumpVertexConsumer;
import coffee.client.helper.vertex.DumpVertexProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ESP extends Module {
    static DumpVertexProvider provider;
    final DoubleSetting range = this.config.create(new DoubleSetting.Builder(64).name("Range")
        .description("How far to render the entities")
        .min(32)
        .max(128)
        .precision(1)
        .get());
    @Setting(name = "Outline mode", description = "How to render the outline")
    public Mode outlineMode = Mode.Shader;
    @Setting(name = "Rect mode", description = "How to render the rect outline")
    ShaderMode shaderMode = ShaderMode.Simple;
    @Setting(name = "Entity filter", description = "Which entities to show")
    ListSetting.FlagSet<AttackFilter> attackFilter = new ListSetting.FlagSet<>(AttackFilter.Hostile, AttackFilter.Players);

    public ESP() {
        super("ESP", "Shows where entities are", ModuleType.RENDER);
    }

    @VisibilitySpecifier("Rect mode")
    boolean shouldShowRect() {
        return outlineMode == Mode.Rect;
    }

    public boolean shouldRenderEntity(Entity le) {
        if (le instanceof PlayerEntity) {
            return attackFilter.isSet(AttackFilter.Players);
        } else if (le instanceof Monster) {
            return attackFilter.isSet(AttackFilter.Hostile);
        } else if (le instanceof PassiveEntity) {
            return attackFilter.isSet(AttackFilter.Passive);
        }
        return attackFilter.isSet(AttackFilter.EverythingElse);
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        if (CoffeeMain.client.world == null || CoffeeMain.client.player == null || outlineMode == Mode.Shader) {
            return;
        }

        for (Entity entity : CoffeeMain.client.world.getEntities()) {
            if (entity.squaredDistanceTo(CoffeeMain.client.player) > Math.pow(range.getValue(), 2)) {
                continue;
            }
            if (entity.getUuid().equals(CoffeeMain.client.player.getUuid())) {
                continue;
            }
            if (shouldRenderEntity(entity)) {
                Color c;
                if (entity instanceof PlayerEntity) {
                    c = Color.RED;
                } else if (entity instanceof ItemEntity) {
                    c = Color.CYAN;
                } else if (entity instanceof EndermanEntity enderman) {
                    if (enderman.isProvoked()) {
                        c = Color.YELLOW;
                    } else {
                        c = Color.GREEN;
                    }
                } else if (entity instanceof HostileEntity) {
                    c = Color.YELLOW;
                } else {
                    c = Color.GREEN;
                }
                Vec3d eSource = Utils.getInterpolatedEntityPosition(entity);
                switch (outlineMode) {
                    case Filled -> Renderer.R3D.renderFilled(
                        matrices,
                        Renderer.Util.modify(c, -1, -1, -1, 100),
                        eSource.subtract(new Vec3d(entity.getWidth(), 0, entity.getWidth()).multiply(0.5)),
                        new Vec3d(entity.getWidth(), entity.getHeight(), entity.getWidth())
                    );
                    case Outline -> Renderer.R3D.renderEdged(
                        matrices,
                        Renderer.Util.modify(c, -1, -1, -1, 100),
                        c,
                        eSource.subtract(new Vec3d(entity.getWidth(), 0, entity.getWidth()).multiply(0.5)),
                        new Vec3d(entity.getWidth(), entity.getHeight(), entity.getWidth())
                    );
                    case Rect -> renderShaderOutline(entity, matrices);
                }
            }
        }
    }

    void renderShaderOutline(Entity e, MatrixStack stack) {
        if (provider == null) {
            provider = new DumpVertexProvider();
        }

        Vec3d origin = Utils.getInterpolatedEntityPosition(e);

        List<Vec3d> boxPoints = new ArrayList<>();
        if (shaderMode == ShaderMode.Accurate) {
            EntityRenderer<? super Entity> eRenderer = client.getEntityRenderDispatcher().getRenderer(e);
            eRenderer.render(e, e.getYaw(), client.getTickDelta(), Renderer.R3D.getEmptyMatrixStack(), provider, 0);
            for (DumpVertexConsumer consumer : provider.getBuffers()) {
                for (DumpVertexConsumer.VertexData vertexData : consumer.getStack()) {
                    if (vertexData.getPosition() != null) {
                        boxPoints.add(vertexData.getPosition().add(origin));
                    }
                }
                consumer.clear();
            }
        } else {
            double w = e.getWidth();
            double h = e.getHeight();

            Vec3d o = origin.subtract(w / 2d, 0, w / 2d);

            boxPoints.addAll(List.of(
                new Vec3d(o.x + 0, o.y, o.z + 0),
                new Vec3d(o.x + w, o.y, o.z + 0),
                new Vec3d(o.x + 0, o.y, o.z + w),
                new Vec3d(o.x + w, o.y, o.z + w),

                new Vec3d(o.x + 0, o.y + h, o.z + 0),
                new Vec3d(o.x + w, o.y + h, o.z + 0),
                new Vec3d(o.x + 0, o.y + h, o.z + w),
                new Vec3d(o.x + w, o.y + h, o.z + w)
            ));
        }

        Vec3d[] screenSpace = boxPoints.stream().map(ee -> Renderer.R2D.getScreenSpaceCoordinate(ee, stack)).toList().toArray(Vec3d[]::new);

        if (screenSpace.length == 0) {
            return;
        }

        Vec3d leastX = screenSpace[0];
        Vec3d mostX = screenSpace[0];
        Vec3d leastY = screenSpace[0];
        Vec3d mostY = screenSpace[0];
        for (Vec3d vec3d : screenSpace) {
            if (!Renderer.R2D.isOnScreen(vec3d)) {
                return;
            }
            if (vec3d.x < leastX.x) {
                leastX = vec3d;
            }
            if (vec3d.x > mostX.x) {
                mostX = vec3d;
            }
            if (vec3d.y < leastY.y) {
                leastY = vec3d;
            }
            if (vec3d.y > mostY.y) {
                mostY = vec3d;
            }
        }
        Vec3d finalLeastX = leastX;
        Vec3d finalLeastY = leastY;
        Vec3d finalMostX = mostX;
        Vec3d finalMostY = mostY;
        Utils.TickManager.runOnNextRender(() -> {
            float x1 = (float) finalLeastX.x;
            float y1 = (float) finalLeastY.y;
            float x2 = (float) finalMostX.x;
            float y2 = (float) finalMostY.y;
            float r = 1f;
            float g = 1f;
            float b = 1f;
            float a = 1f;
            float desiredHeight = (float) ((finalMostY.y - finalLeastY.y) / 3f);
            float desiredWidth = (float) ((finalMostX.x - finalLeastX.x) / 3f);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            Renderer.setupRender();
            RenderSystem.disableCull();
            renderCorner(bufferBuilder, r, g, b, a, x1, y1, desiredHeight, desiredWidth, 1, 1);
            renderCorner(bufferBuilder, r, g, b, a, x2, y1, desiredHeight, desiredWidth, -1, 1);
            renderCorner(bufferBuilder, r, g, b, a, x2, y2, desiredHeight, desiredWidth, -1, -1);
            renderCorner(bufferBuilder, r, g, b, a, x1, y2, desiredHeight, desiredWidth, 1, -1);
            RenderSystem.enableCull();
            Renderer.endRender();

        });
    }

    void renderCorner(BufferBuilder bb, float r, float g, float b, float a, float x, float y, float height, float topWidth, float xMul, float yMul) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        float width = 1;
        /*
        4---------5
        |         |
        |    1----6
        |    |
        |    |
        3----2
        */
        float[][] verts = new float[][] { new float[] { 0, 0 }, new float[] { 0, height }, new float[] { -width, height }, new float[] { -width, -width },
            new float[] { topWidth, -width }, new float[] { topWidth, 0 }, new float[] { 0, 0 } };
        for (float[] vert : verts) {
            bb.vertex(x + vert[0] * xMul, y + vert[1] * yMul, 0f).color(r, g, b, a).next();
        }
        BufferRenderer.drawWithGlobalProgram(bb.end());
    }

    public enum AttackFilter {
        Passive, Hostile, Players, EverythingElse
    }

    public enum ShaderMode {
        Accurate, Simple
    }

    public enum Mode {
        Filled, Rect, Outline, Shader
    }
}
