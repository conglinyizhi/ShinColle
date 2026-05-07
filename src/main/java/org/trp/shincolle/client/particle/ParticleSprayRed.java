package org.trp.shincolle.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ParticleSprayRed extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected ParticleSprayRed(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;

        double speedSq = vx * vx + vy * vy + vz * vz;
        double speedLimit = 0.3D;
        if (speedSq > speedLimit * speedLimit) {
            double speed = Math.sqrt(speedSq);
            this.xd = (vx / speed) * speedLimit;
            this.yd = (vy / speed) * speedLimit;
            this.zd = (vz / speed) * speedLimit;
        } else {
            this.xd = vx;
            this.yd = vy;
            this.zd = vz;
        }

        this.rCol = 1.0F;
        this.gCol = 0.0F;
        this.bCol = 0.0F;
        this.alpha = 0.8F;

        this.lifetime = 40;

        this.hasPhysics = true;
        this.gravity = 0.0F;
        this.friction = 0.96F;

        this.quadSize = 0.3F;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleSprayRed(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}