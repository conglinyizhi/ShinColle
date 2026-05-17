package org.trp.shincolle.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class ParticleSparkle extends Particle {

    private static final ParticleRenderType UNTEXTURED_RENDER = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager tm) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
    };
    
    protected int maxBeamAge;
    protected final float beamFad;
    protected final float motionY;
    protected final float beamHeight;
    protected final int particleType;
    protected float[][] beams;
    protected int beamCurrent;
    protected final float quadSize;

    protected ParticleSparkle(ClientLevel level, double x, double y, double z,
                               double type, double beamFad, double beamRiseSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.particleType = (int) type;
        this.beamFad = (float) Math.max(0.0D, beamFad);
        this.motionY = (float) beamRiseSpeed;
        this.beamHeight = 0.4F; 
        this.lifetime = 20;
        this.maxBeamAge = 20;
        this.hasPhysics = false;
        this.quadSize = 0.075F;
        
        
        this.setSize(0.5f, 0.5f);
        this.setBoundingBox(new AABB(x - 2.0, y - 1.0, z - 2.0, x + 2.0, y + 3.0, z + 2.0));

        int setting = getParticleSetting(level);
        int numBeam = Math.max(1, (3 - setting) * 15);
        this.beams = new float[numBeam][8];
        for (int i = 0; i < numBeam; i++) {
            this.beams[i][7] = (float) this.maxBeamAge;
        }
        this.beamCurrent = 0;
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

        int setting = getParticleSetting(this.level);
        int spawnCount = 4 - setting;
        for (int i = 0; i < spawnCount; i++) {
            spawnBeam();
        }

        for (float[] beam : this.beams) {
            beam[1] += this.motionY;
            beam[7] += 1.0F;
            beam[6] = Math.min(1.0F, this.random.nextFloat() + 0.1F);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float baseX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float baseY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float baseZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        Quaternionf rotation = camera.rotation();

        for (float[] beam : this.beams) {
            float beamAge = beam[7];
            if (beamAge >= this.maxBeamAge) {
                continue;
            }

            float size = (this.maxBeamAge - beamAge) / (float)this.maxBeamAge * this.quadSize;
            if (size <= 0.0F) {
                continue;
            }

            float px = baseX + beam[0];
            float py = baseY + beam[1];
            float pz = baseZ + beam[2];

            Vector3f[] corners = new Vector3f[]{
                    new Vector3f(-1.0F, -1.0F, 0.0F),
                    new Vector3f(-1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, -1.0F, 0.0F)
            };

            for (Vector3f corner : corners) {
                corner.rotate(rotation);
                corner.mul(size);
                corner.add(px, py, pz);
            }

            float r = beam[3];
            float g = beam[4];
            float b = beam[5];
            float a = beam[6];

            buffer.addVertex(corners[0].x(), corners[0].y(), corners[0].z()).setColor(r, g, b, a);
            buffer.addVertex(corners[1].x(), corners[1].y(), corners[1].z()).setColor(r, g, b, a);
            buffer.addVertex(corners[2].x(), corners[2].y(), corners[2].z()).setColor(r, g, b, a);
            buffer.addVertex(corners[3].x(), corners[3].y(), corners[3].z()).setColor(r, g, b, a);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return UNTEXTURED_RENDER;
    }

    protected void spawnBeam() {
        float[] color = getBeamColor();

        this.beams[this.beamCurrent][0] = (this.random.nextFloat() * 2.0F - 1.0F) * this.beamFad;
        this.beams[this.beamCurrent][1] = this.beamHeight + (this.random.nextFloat() * 2.0F - 1.0F) * this.beamFad;
        this.beams[this.beamCurrent][2] = (this.random.nextFloat() * 2.0F - 1.0F) * this.beamFad;
        this.beams[this.beamCurrent][3] = color[0];
        this.beams[this.beamCurrent][4] = color[1];
        this.beams[this.beamCurrent][5] = color[2];
        this.beams[this.beamCurrent][6] = 1.0F;
        this.beams[this.beamCurrent][7] = 0.0F;

        this.beamCurrent = (this.beamCurrent + 1) % this.beams.length;
    }

    private float[] getBeamColor() {
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float randFactor = this.random.nextFloat() * 1.2F - 0.5F;
        switch (this.particleType) {
            case 0: red += randFactor; break;
            case 2: green += randFactor; break;
            case 3: red = 1.0F; green = 1.0F; blue = 1.0F + (this.random.nextFloat() * 1.2F - 0.5F); break;
            case 4: red += randFactor; green += this.random.nextFloat() * 1.2F - 0.5F; break;
            case 5: red += randFactor; blue += this.random.nextFloat() * 1.2F - 0.5F; break;
            case 6: green += randFactor; blue += this.random.nextFloat() * 1.2F - 0.5F; break;
            case 7: red += randFactor; green += this.random.nextFloat() * 1.2F - 0.5F; blue += this.random.nextFloat() * 1.2F - 0.5F; break;
            case 8: red += randFactor; green = 0.001F; blue = 0.001F; break;
            case 9: red = 0.001F; green += randFactor; blue = 0.001F; break;
            case 10: red = 0.001F; green = 0.001F; blue += randFactor; break;
            default:
        }
        return new float[]{red, green, blue};
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
    }

    protected int getParticleSetting(Level level) {
        if (Minecraft.getInstance().level != level) {
            return 0;
        }
        return Minecraft.getInstance().options.particles().get().getId();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y,
                                       double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleSparkle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}