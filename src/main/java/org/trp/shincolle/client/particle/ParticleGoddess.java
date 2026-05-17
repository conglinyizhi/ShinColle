package org.trp.shincolle.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class ParticleGoddess extends ParticleHealSparkle {

    protected ParticleGoddess(ClientLevel level, double x, double y, double z,
                             double beamFad, double beamRiseSpeed, double beamHeight) {
        super(level, x, y, z, beamFad, beamRiseSpeed, beamHeight);
        this.lifetime = 120;
        this.maxBeamAge = 40;
        
        int setting = getParticleSetting(level);
        int numBeam = Math.max(1, (3 - setting) * 30);
        this.beams = new float[numBeam][8];
        for (int i = 0; i < numBeam; i++) {
            this.beams[i][7] = this.maxBeamAge;
        }
        this.beamCurrent = 0;
    }

    @Override
    protected void spawnBeam() {
        float randFactor = this.random.nextFloat() * 0.4F - 0.2F;
        float red = 1.0F;
        float green = 0.9F + randFactor;
        float blue = 0.4F + randFactor;

        this.beams[this.beamCurrent][0] = (this.random.nextFloat() * 2.5F - 1.25F) * this.beamFad;
        this.beams[this.beamCurrent][1] = this.beamHeight + (this.random.nextFloat() * 2.5F - 1.25F) * this.beamFad;
        this.beams[this.beamCurrent][2] = (this.random.nextFloat() * 2.5F - 1.25F) * this.beamFad;
        this.beams[this.beamCurrent][3] = red;
        this.beams[this.beamCurrent][4] = green;
        this.beams[this.beamCurrent][5] = blue;
        this.beams[this.beamCurrent][6] = 1.0F;
        this.beams[this.beamCurrent][7] = 0.0F;

        this.beamCurrent = (this.beamCurrent + 1) % this.beams.length;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) {
        }

        @Override
        public ParticleGoddess createParticle(SimpleParticleType type, ClientLevel level, double x, double y,
                                             double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleGoddess(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
