package org.trp.shincolle.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ParticleCraning extends Particle {

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

    private final float lenMax;
    private float len;
    private final double[][] vt1;
    private final double[][] vt2;
    private final float quadSize;

    public ParticleCraning(ClientLevel level, double x, double y, double z, double lengthMax, double scale, SpriteSet sprites) {
        super(level, x, y, z);
        this.setSize(0.5f, 0.5f); 
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.lenMax = (float) lengthMax;
        this.quadSize = (float) scale;
        this.vt1 = new double[8][3];
        this.vt2 = new double[8][3];
        this.hasPhysics = false;
        this.lifetime = 127;
        this.rCol = 0.0f;
        this.gCol = 0.0f;
        this.bCol = 0.0f;
        this.len = 0.0f;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        
        float sizeHead = this.quadSize;
        float sizeChain = this.quadSize * 0.25f;
        float[] pos1 = new float[]{sizeHead * 0.75f, -sizeHead, -sizeHead};
        float[] pos2 = new float[]{sizeHead * 0.75f, sizeHead, -sizeHead};
        float[] pos3 = new float[]{-sizeHead * 0.75f, sizeHead, -sizeHead};
        float[] pos4 = new float[]{-sizeHead * 0.75f, -sizeHead, -sizeHead};
        float[] pos5 = new float[]{sizeChain, -sizeChain * 1.5f, -sizeChain};
        float[] pos6 = new float[]{sizeChain, sizeChain * 1.5f, -sizeChain};
        float[] pos7 = new float[]{-sizeChain, sizeChain * 1.5f, -sizeChain};
        float[] pos8 = new float[]{-sizeChain, -sizeChain * 1.5f, -sizeChain};

        double hx = Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x();
        double hy = Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y() - this.len + this.quadSize * 5.0;
        double hz = Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z() + this.quadSize * 0.5;
        double hz_chain = hz - this.quadSize * 0.47;
        
        double z1 = this.quadSize * 0.8;
        double z2 = this.quadSize * 0.25;
        double y1 = this.quadSize;

        float clen = 0.0f;
        while (clen < this.len) {
            double ny = hy + clen;
            
            this.vt2[0][0] = hx + pos5[0]; this.vt2[0][1] = (ny + pos5[1]) + y1; this.vt2[0][2] = hz_chain + pos5[2];
            this.vt2[1][0] = hx + pos6[0]; this.vt2[1][1] = (ny + pos6[1]) + y1; this.vt2[1][2] = hz_chain + pos6[2];
            this.vt2[2][0] = hx + pos7[0]; this.vt2[2][1] = (ny + pos7[1]) + y1; this.vt2[2][2] = hz_chain + pos7[2];
            this.vt2[3][0] = hx + pos8[0]; this.vt2[3][1] = (ny + pos8[1]) + y1; this.vt2[3][2] = hz_chain + pos8[2];
            
            this.vt2[4][0] = hx + pos5[0]; this.vt2[4][1] = (ny + pos5[1]) + y1; this.vt2[4][2] = hz_chain + pos5[2] + z2;
            this.vt2[5][0] = hx + pos6[0]; this.vt2[5][1] = (ny + pos6[1]) + y1; this.vt2[5][2] = hz_chain + pos6[2] + z2;
            this.vt2[6][0] = hx + pos7[0]; this.vt2[6][1] = (ny + pos7[1]) + y1; this.vt2[6][2] = hz_chain + pos7[2] + z2;
            this.vt2[7][0] = hx + pos8[0]; this.vt2[7][1] = (ny + pos8[1]) + y1; this.vt2[7][2] = hz_chain + pos8[2] + z2;

            renderQuads(buffer, vt2);
            clen += this.quadSize;
        }

        this.vt1[0][0] = hx + pos1[0]; this.vt1[0][1] = hy + pos1[1]; this.vt1[0][2] = hz + pos1[2];
        this.vt1[1][0] = hx + pos2[0]; this.vt1[1][1] = hy + pos2[1]; this.vt1[1][2] = hz + pos2[2];
        this.vt1[2][0] = hx + pos3[0]; this.vt1[2][1] = hy + pos3[1]; this.vt1[2][2] = hz + pos3[2];
        this.vt1[3][0] = hx + pos4[0]; this.vt1[3][1] = hy + pos4[1]; this.vt1[3][2] = hz + pos4[2];
        
        this.vt1[4][0] = hx + pos1[0]; this.vt1[4][1] = hy + pos1[1]; this.vt1[4][2] = hz + pos1[2] + z1;
        this.vt1[5][0] = hx + pos2[0]; this.vt1[5][1] = hy + pos2[1]; this.vt1[5][2] = hz + pos2[2] + z1;
        this.vt1[6][0] = hx + pos3[0]; this.vt1[6][1] = hy + pos3[1]; this.vt1[6][2] = hz + pos3[2] + z1;
        this.vt1[7][0] = hx + pos4[0]; this.vt1[7][1] = hy + pos4[1]; this.vt1[7][2] = hz + pos4[2] + z1;

        renderQuads(buffer, vt1);
    }

    
    private void renderQuads(VertexConsumer buffer, double[][] vt) {
        
        emitVertex(buffer, vt[3]); 
        emitVertex(buffer, vt[2]); 
        emitVertex(buffer, vt[1]); 
        emitVertex(buffer, vt[0]); 
        
        emitVertex(buffer, vt[0]); 
        emitVertex(buffer, vt[1]); 
        emitVertex(buffer, vt[5]); 
        emitVertex(buffer, vt[4]); 
        
        emitVertex(buffer, vt[4]); 
        emitVertex(buffer, vt[5]); 
        emitVertex(buffer, vt[6]); 
        emitVertex(buffer, vt[7]); 
        
        emitVertex(buffer, vt[7]); 
        emitVertex(buffer, vt[6]); 
        emitVertex(buffer, vt[2]); 
        emitVertex(buffer, vt[3]); 
        
        emitVertex(buffer, vt[1]); 
        emitVertex(buffer, vt[2]); 
        emitVertex(buffer, vt[6]); 
        emitVertex(buffer, vt[5]); 
        
        emitVertex(buffer, vt[3]); 
        emitVertex(buffer, vt[0]); 
        emitVertex(buffer, vt[4]); 
        emitVertex(buffer, vt[7]); 
    }

    private void emitVertex(VertexConsumer buffer, double[] pos) {
        buffer.addVertex((float)pos[0], (float)pos[1], (float)pos[2]).setColor(this.rCol, this.gCol, this.bCol, 1.0f);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.pack(3, 3);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return UNTEXTURED_RENDER;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        float half = this.lifetime * 0.45f;
        float half2 = this.lifetime - half;
        if (this.age <= half) {
            this.len = this.age / half * this.lenMax;
        } else if (this.age <= half2) {
            this.len = this.lenMax;
        } else {
            this.len = (this.lifetime - this.age) / half * this.lenMax;
        }

        double minY = Math.min(this.y, this.y - this.len);
        double maxY = Math.max(this.y, this.y - this.len) + (this.quadSize * 6.0);
        this.setBoundingBox(new AABB(this.x - 1.0, minY, this.z - 1.0, this.x + 1.0, maxY, this.z + 1.0));

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            double lenMax = xSpeed;
            double scale = ySpeed > 0 ? ySpeed : 0.25;
            return new ParticleCraning(level, x, y, z, lenMax, scale, this.sprites);
        }
    }
}