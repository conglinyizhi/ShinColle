package org.trp.shincolle.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ParticleBeam extends Particle {

    private static final ParticleRenderType UNTEXTURED_RENDER = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, net.minecraft.client.renderer.texture.TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }

        @Override
        public String toString() {
            return "SHINCOLLE_BEAM_UNTEXTURED";
        }
    };

    private final int hostId;
    private final int targetId;
    private final float baseScale;

    private float shotYaw;
    private float shotPitch;
    private float scaleOut;
    private float scaleIn;
    private float alphaOut;
    private float alphaIn;

    private double tarX;
    private double tarY;
    private double tarZ;

    protected ParticleBeam(ClientLevel level, double x, double y, double z,
                           int hostId, int targetId, float scale) {
        super(level, x, y, z);
        this.hostId = hostId;
        this.targetId = targetId;
        this.baseScale = scale;
        this.lifetime = 30;

        this.rCol = 1.0f;
        this.gCol = 0.8f;
        this.bCol = 0.9f;
        
        this.hasPhysics = false;
        this.setSize(0.0f, 0.0f);

        updatePositions();
    }

    private void updatePositions() {
        Entity host = this.level.getEntity(this.hostId);
        Entity target = this.level.getEntity(this.targetId);

        if (host == null || target == null) {
            return;
        }

        double dx = target.getX() - host.getX();
        double dy = (target.getY() + target.getBbHeight() * 0.5) - (host.getY() + host.getBbHeight() * 0.6);
        double dz = target.getZ() - host.getZ();

        double d1 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double motX = dx, motY = dy, motZ = dz;
        if (d1 > 1.0E-4) {
            motX /= d1;
            motY /= d1;
            motZ /= d1;
        }
        double f1 = Math.sqrt(motX * motX + motZ * motZ);
        this.shotPitch = (float) -Math.atan2(motY, f1);
        this.shotYaw = (float) -Math.atan2(motX, motZ);

        float[] posOffset = rotateXYZByYawPitch(0.0f, 0.0f, host.getBbWidth() * 2.0f, this.shotYaw, this.shotPitch, 1.0f);

        this.x = host.getX() + posOffset[0];
        this.y = host.getY() + host.getBbHeight() * 0.6 + posOffset[1];
        this.z = host.getZ() + posOffset[2];

        this.tarX = target.getX();
        this.tarY = target.getY() + target.getBbHeight() * 0.5;
        this.tarZ = target.getZ();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        Entity host = this.level.getEntity(this.hostId);
        Entity target = this.level.getEntity(this.targetId);
        if (host == null || target == null || !host.isAlive() || !target.isAlive()) {
            this.remove();
            return;
        }

        updatePositions();

        if (this.age > 20) {
            this.alphaIn = 1.0f + (20 - this.age) * 0.1f;
            this.alphaOut = this.alphaIn * 0.25f;
        } else if (this.age < 4) {
            this.alphaIn = 0.2f + this.age * 0.2f;
            this.alphaOut = this.alphaIn * 0.25f;
        } else {
            this.alphaIn = 1.0f;
            this.alphaOut = 0.1f + this.random.nextFloat() * 0.25f;
        }

        if (this.age > 20) {
            this.scaleOut = this.baseScale * (1.0f + (this.age - 20));
            this.scaleIn = this.baseScale * 0.35f * (1.0f - (this.age - 20) * 0.1f);
        } else if (this.age < 8) {
            this.scaleOut = this.baseScale * 0.3f * (this.age * 0.3f);
            this.scaleIn = this.baseScale * 0.35f * (this.age * 0.125f);
        } else {
            this.scaleOut = this.baseScale;
            this.scaleIn = this.baseScale * 0.35f;
        }

        this.scaleOut += this.random.nextFloat() * 0.2f - 0.05f;
        this.scaleIn += this.random.nextFloat() * 0.08f - 0.04f;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    private float[] rotateXYZByYawPitch(float x, float y, float z, float yaw, float pitch, float scale) {
        float cosYaw = Mth.cos(yaw);
        float sinYaw = Mth.sin(yaw);
        float cosPitch = Mth.cos(-pitch);
        float sinPitch = Mth.sin(-pitch);
        float[] newPos = new float[]{x, y, z};
        newPos[1] = y * cosPitch + z * sinPitch;
        newPos[2] = z * cosPitch - y * sinPitch;
        float x2 = newPos[0];
        float z2 = newPos[2];
        newPos[0] = x2 * cosYaw - z2 * sinYaw;
        newPos[2] = z2 * cosYaw + x2 * sinYaw;
        newPos[0] *= scale;
        newPos[1] *= scale;
        newPos[2] *= scale;
        return newPos;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.age <= 1) {
            return;
        }

        Vec3 cameraPos = camera.getPosition();
        float hx = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float hy = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float hz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        float tx = (float) (this.tarX - cameraPos.x());
        float ty = (float) (this.tarY - cameraPos.y());
        float tz = (float) (this.tarZ - cameraPos.z());

        int light = LightTexture.FULL_BRIGHT;

        float[] v1 = rotateXYZByYawPitch(1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut);
        float[] v2 = rotateXYZByYawPitch(1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut);
        float[] v3 = rotateXYZByYawPitch(-1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut);
        float[] v4 = rotateXYZByYawPitch(-1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut);

        float[] v5 = rotateXYZByYawPitch(1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn);
        float[] v6 = rotateXYZByYawPitch(1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn);
        float[] v7 = rotateXYZByYawPitch(-1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn);
        float[] v8 = rotateXYZByYawPitch(-1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn);

        drawQuad(buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v2[0], ty + v2[1], tz + v2[2],
                         hx + v2[0], hy + v2[1], hz + v2[2], hx + v1[0], hy + v1[1], hz + v1[2],
                         this.rCol, this.gCol, this.bCol, this.alphaOut, light);
        drawQuad(buffer, tx + v4[0], ty + v4[1], tz + v4[2], tx + v3[0], ty + v3[1], tz + v3[2],
                         hx + v3[0], hy + v3[1], hz + v3[2], hx + v4[0], hy + v4[1], hz + v4[2],
                         this.rCol, this.gCol, this.bCol, this.alphaOut, light);
        drawQuad(buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v4[0], ty + v4[1], tz + v4[2],
                         hx + v4[0], hy + v4[1], hz + v4[2], hx + v1[0], hy + v1[1], hz + v1[2],
                         this.rCol, this.gCol, this.bCol, this.alphaOut, light);
        drawQuad(buffer, tx + v2[0], ty + v2[1], tz + v2[2], tx + v3[0], ty + v3[1], tz + v3[2],
                         hx + v3[0], hy + v3[1], hz + v3[2], hx + v2[0], hy + v2[1], hz + v2[2],
                         this.rCol, this.gCol, this.bCol, this.alphaOut, light);

        drawQuad(buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v6[0], ty + v6[1], tz + v6[2],
                         hx + v6[0], hy + v6[1], hz + v6[2], hx + v5[0], hy + v5[1], hz + v5[2],
                         1.0f, 1.0f, 1.0f, this.alphaIn, light);
        drawQuad(buffer, tx + v8[0], ty + v8[1], tz + v8[2], tx + v7[0], ty + v7[1], tz + v7[2],
                         hx + v7[0], hy + v7[1], hz + v7[2], hx + v8[0], hy + v8[1], hz + v8[2],
                         1.0f, 1.0f, 1.0f, this.alphaIn, light);
        drawQuad(buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v8[0], ty + v8[1], tz + v8[2],
                         hx + v8[0], hy + v8[1], hz + v8[2], hx + v5[0], hy + v5[1], hz + v5[2],
                         1.0f, 1.0f, 1.0f, this.alphaIn, light);
        drawQuad(buffer, tx + v6[0], ty + v6[1], tz + v6[2], tx + v7[0], ty + v7[1], tz + v7[2],
                         hx + v7[0], hy + v7[1], hz + v7[2], hx + v6[0], hy + v6[1], hz + v6[2],
                         1.0f, 1.0f, 1.0f, this.alphaIn, light);
    }

    private void drawQuad(VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2,
                          float x3, float y3, float z3, float x4, float y4, float z4,
                          float r, float g, float b, float alpha, int light) {
        buffer.addVertex(x1, y1, z1).setColor(r, g, b, alpha);
        buffer.addVertex(x2, y2, z2).setColor(r, g, b, alpha);
        buffer.addVertex(x3, y3, z3).setColor(r, g, b, alpha);
        buffer.addVertex(x4, y4, z4).setColor(r, g, b, alpha);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return UNTEXTURED_RENDER;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new ParticleBeam(level, x, y, z, (int) vx, (int) vy, (float) vz);
        }
    }
}
