package org.trp.shincolle.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.trp.shincolle.entity.base.EntityMountBase;

public class ParticleLightning extends Particle {
    private static final ParticleRenderType LIGHTNING_RENDER = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, net.minecraft.client.renderer.texture.TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            return tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        }

        @Override
        public String toString() {
            return "SHINCOLLE_LIGHTNING";
        }
    };

    private final int hostEntityId;
    private final int particleType;
    private final int numStem = 4;
    private final float scaleXZ = 0.01F;
    private final float scaleY = 0.12F;

    protected ParticleLightning(ClientLevel level, double x, double y, double z, double scale, int hostEntityId, int particleType) {
        super(level, x, y, z);
        this.setSize(0.0F, 0.0F);
        this.hostEntityId = hostEntityId;
        this.particleType = particleType;

        this.hasPhysics = false;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.scale((float) scale);

        this.rCol = 1.0F;
        this.gCol = 0.4F + this.random.nextFloat() * 0.3F;
        this.bCol = 0.4F + this.random.nextFloat() * 0.3F;
        this.alpha = 1.0F;
        this.lifetime = 20;

        updatePosition(true);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ > this.lifetime) {
            this.remove();
            return;
        }

        if (this.particleType == 0) {
            updatePosition(false);
        }
    }

    private void updatePosition(boolean initial) {
        Entity host = this.level.getEntity(this.hostEntityId);
        if (host == null || !host.isAlive()) {
            if (this.age > 2) this.remove();
            return;
        }

        float randx = this.random.nextFloat() + 0.1F;
        float yaw = host instanceof LivingEntity living ? living.yBodyRot : host.getYRot();
        float[] newPos = rotateXZByAxis(0.8F + this.random.nextFloat() * 0.2F, randx, -yaw * Mth.DEG_TO_RAD);

        this.x = host.getX() + newPos[0];
        this.y = host.getY() + (initial ? 1.53D : 1.76D) + randx * 0.25D;
        this.z = host.getZ() + newPos[1];

        if (host instanceof EntityMountBase mount) {
            if (mount.getShipDepth() > 0.0) {
                this.y -= 0.08D;
            }
            if (mount.getHost() != null && mount.getHost().isOrderedToSit()) {
                this.y -= 0.23D;
            }
        }
    }

    private float[] rotateXZByAxis(float z, float x, float angle) {
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        return new float[]{
                z * cos + x * sin,
                x * cos - z * sin
        };
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        Quaternionf rotation = camera.rotation();
        Vector3f right = new Vector3f(1.0F, 0.0F, 0.0F).rotate(rotation);
        right.y = 0;
        right.normalize();

        float cosPitch = Mth.cos(camera.getXRot() * Mth.DEG_TO_RAD);

        for (int i = this.numStem - 1; i >= 0; i--) {
            float offx = (this.random.nextFloat() - 0.5F) * 0.1F * (i + 1);
            float offz = (this.random.nextFloat() - 0.5F) * 0.1F * (i + 1);

            float yOffset = (i == 0) ? (cosPitch * this.scaleY) : (cosPitch * this.scaleY - i * this.scaleY);
            float currentY = py + yOffset;

            float v1x = px + offx + right.x() * this.scaleXZ;
            float v1z = pz + offz + right.z() * this.scaleXZ;
            float v2x = px + offx - right.x() * this.scaleXZ;
            float v2z = pz + offz - right.z() * this.scaleXZ;

            buffer.addVertex(v1x, currentY, v1z).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
            buffer.addVertex(v2x, currentY, v2z).setColor(this.rCol, this.gCol, this.bCol, this.alpha);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return LIGHTNING_RENDER;
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) {}

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double scale, double hostEntityId, double particleType) {
            return new ParticleLightning(level, x, y, z, scale, (int) Math.round(hostEntityId), (int) Math.round(particleType));
        }
    }
}