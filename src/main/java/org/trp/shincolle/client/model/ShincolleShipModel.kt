package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.geom.ModelPart
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Common base for humanoid ship models.
 *
 * This class unifies duplicated pose-state handling, glow-part synchronization,
 * pose-translated rendering and common equipment mount-point visibility logic.
 * Subclasses keep their unique part definitions and exclusive animations while
 * inheriting the shared boilerplate.
 */
abstract class ShincolleShipModel<T : EntityShipBase> : ShipModelHumanoidBase<T>(), IGlowableModel {

    protected var isDeadPose: Boolean = false
    protected var isSittingPose: Boolean = false

    /** Main body parts that glow parts mirror. */
    protected abstract val bodyMain: ModelPart
    protected abstract val neck: ModelPart
    protected abstract val head: ModelPart

    /** Optional glow counterparts. When null, glow syncing/rendering is skipped. */
    protected abstract val glowBodyMain: ModelPart?
    protected abstract val glowNeck: ModelPart?
    protected abstract val glowHead: ModelPart?

    /** Optional second set of glow counterparts (e.g. for dual-layer glow). */
    protected open val glowBodyMain2: ModelPart? = null
    protected open val glowNeck2: ModelPart? = null
    protected open val glowHead2: ModelPart? = null

    /** Resets the transient pose state at the beginning of [setupAnim]. */
    protected open fun resetPoseState() {
        isDeadPose = false
        isSittingPose = false
        poseTranslateY = 0.0f
    }

    /** Returns true when the entity is in the dead/corpse pose. */
    protected fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    /** Begins a dead pose by setting the flag and vertical offset. */
    protected fun beginDeadPose(translateY: Float) {
        isDeadPose = true
        poseTranslateY = translateY
    }

    /** Copies the main body/neck/head transforms to their glow counterparts. */
    protected open fun syncGlowParts() {
        val glowBody = glowBodyMain ?: return
        glowBody.copyFrom(bodyMain)
        glowNeck?.copyFrom(neck)
        glowHead?.copyFrom(head)

        glowBodyMain2?.copyFrom(bodyMain)
        glowNeck2?.copyFrom(neck)
        glowHead2?.copyFrom(head)

        syncExtraGlowParts()
    }

    /** Hook for subclasses that need to sync additional glow parts. */
    protected open fun syncExtraGlowParts() {
    }

    /** Renders [bodyMain] while applying [poseTranslateY]. */
    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        renderWithPoseTranslate(poseStack) {
            bodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }
    }

    /** Renders [glowBodyMain] while applying [poseTranslateY]. */
    override fun renderGlow(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        renderWithPoseTranslate(poseStack) {
            glowBodyMain?.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
            glowBodyMain2?.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
            renderExtraGlowParts(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }
    }

    /** Hook for subclasses that need to render additional glow parts. */
    protected open fun renderExtraGlowParts(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
    }

    /**
     * Applies visibility for the most common equipment mount points.
     * Subclasses with additional mount points can call this helper and then
     * apply their own specific visibility rules.
     */
    protected fun applyCommonEquipVisibility(
        riggingVisible: Boolean,
        headBaseVisible: Boolean = riggingVisible,
        hairSetVisible: Boolean = false,
        ahokeVisible: Boolean = false,
        equipBase: ModelPart? = null,
        equipHeadBase: ModelPart? = null,
        hairS01: ModelPart? = null,
        hairS02: ModelPart? = null,
        hairCBase: ModelPart? = null,
        hairCBaseB: ModelPart? = null,
        ahoke00: ModelPart? = null,
        ahoke01: ModelPart? = null
    ) {
        equipBase?.visible = riggingVisible
        equipHeadBase?.visible = headBaseVisible
        hairS01?.visible = hairSetVisible
        hairS02?.visible = hairSetVisible
        hairCBase?.visible = hairSetVisible
        hairCBaseB?.visible = hairSetVisible
        ahoke00?.visible = ahokeVisible
        ahoke01?.visible = !ahokeVisible
    }

    private inline fun renderWithPoseTranslate(poseStack: PoseStack, renderAction: () -> Unit) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }
        renderAction()
        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }
}
