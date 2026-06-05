package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

class ShincolleRenderTypes private constructor(
    name: String,
    format: VertexFormat,
    mode: VertexFormat.Mode,
    bufferSize: Int,
    affectsCrumbling: Boolean,
    sortOnUpload: Boolean,
    setupState: Runnable,
    clearState: Runnable
) : RenderType(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState) {
    companion object {
        @JvmStatic
        fun getFlatGlow(textureLocation: ResourceLocation): RenderType {
            val renderState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_EYES_SHADER)

                .setTextureState(TextureStateShard(textureLocation, false, false))

                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)

                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false)

            return create(
                "shincolle_flat_glow",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                renderState
            )
        }
    }
}
