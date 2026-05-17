package org.trp.shincolle.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ParticleWaypoint extends TextureSheetParticle {
    private static final float BASE_Y_OFFSET = 0.26f;
    private static final float BASE_SCALE = 3.0f;

    private final SpriteSet sprites;
    private final float markerHeight;

    protected ParticleWaypoint(ClientLevel level, double x, double y, double z,
                                double markerHeight, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.lifetime = 31;
        this.quadSize = (float) (markerHeight > 0 ? markerHeight : 0.2f);
        this.markerHeight = 1.5f; 
        this.rCol = 1.0f;
        this.gCol = 0.0f;
        this.bCol = 0.0f;
        this.alpha = 0.9f;
        this.hasPhysics = false;
        this.gravity = 0.0f;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.y += this.age * 0.002;
        this.alpha = Math.max(0.0f, 0.9f - this.age * 0.027f);

        this.setSpriteFromAge(this.sprites);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        TextureAtlasSprite sprite = this.sprite;
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = Mth.lerp(0.5f, sprite.getV0(), sprite.getV1());
        float v1 = sprite.getV1();

        Vec3 cameraPos = camera.getPosition();
        float bx = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float by = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y()) + BASE_Y_OFFSET;
        float bz = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        float halfScale = this.getQuadSize(partialTick) * BASE_SCALE;
        int light = getLightColor(partialTick);

        emitVertex(buffer, bx + halfScale, by, bz + halfScale, u1, v1, light);
        emitVertex(buffer, bx + halfScale, by, bz - halfScale, u1, v0, light);
        emitVertex(buffer, bx - halfScale, by, bz - halfScale, u0, v0, light);
        emitVertex(buffer, bx - halfScale, by, bz + halfScale, u0, v1, light);

        emitVertex(buffer, bx + halfScale, by, bz - halfScale, u1, v1, light);
        emitVertex(buffer, bx + halfScale, by, bz + halfScale, u1, v0, light);
        emitVertex(buffer, bx - halfScale, by, bz + halfScale, u0, v0, light);
        emitVertex(buffer, bx - halfScale, by, bz - halfScale, u0, v1, light);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    private void emitVertex(VertexConsumer buffer, float x, float y, float z, float u, float v, int light) {
        buffer.addVertex(x, y, z)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setUv(u, v)
                .setLight(light);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleWaypoint(level, x, y, z, xSpeed, this.sprites);
        }
    }
}
