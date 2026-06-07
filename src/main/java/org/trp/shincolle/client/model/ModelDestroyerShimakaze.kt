@file:Suppress("SENSELESS_COMPARISON")
package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.LegacyPoseOffsets.deadY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityDestroyerShimakaze
import org.trp.shincolle.entity.base.EntityShipBase

class ModelDestroyerShimakaze<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val NeckCloth: ModelPart
    private val ArmLeft: ModelPart
    private val ArmRight: ModelPart
    private val Butt: ModelPart
    private val EquipBase: ModelPart
    private val Head: ModelPart
    private val NeckTie: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairAnchor: ModelPart
    private val HairR02: ModelPart
    private val HairMidL01: ModelPart
    private val HairMidR01: ModelPart
    private val EarBase: ModelPart
    private val HairMidL02: ModelPart
    private val HairMidR02: ModelPart
    private val EarL01: ModelPart
    private val EarL02: ModelPart
    private val EarR01: ModelPart
    private val EarR02: ModelPart
    private val LegRight: ModelPart
    private val LegLeft: ModelPart
    private val Skirt: ModelPart
    private val ShoesR: ModelPart
    private val ShoesL: ModelPart
    private val EquipHead: ModelPart
    private val EquipT01: ModelPart
    private val EquipT02: ModelPart
    private val EquipT03: ModelPart
    private val EquipT04: ModelPart
    private val EquipT05: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeckCloth: ModelPart
    private val GlowHead: ModelPart

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.NeckCloth = this.BodyMain.getChild("NeckCloth")
        this.Head = this.NeckCloth.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.HairMidR01 = this.HairMain.getChild("HairMidR01")
        this.HairMidR02 = this.HairMidR01.getChild("HairMidR02")
        this.HairMidL01 = this.HairMain.getChild("HairMidL01")
        this.HairMidL02 = this.HairMidL01.getChild("HairMidL02")
        this.EarBase = this.HairMain.getChild("EarBase")
        this.EarL01 = this.EarBase.getChild("EarL01")
        this.EarL02 = this.EarL01.getChild("EarL02")
        this.EarR01 = this.EarBase.getChild("EarR01")
        this.EarR02 = this.EarR01.getChild("EarR02")
        this.Hair = this.Head.getChild("Hair")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairAnchor = this.HairL02.getChild("HairAnchor")
        this.NeckTie = this.NeckCloth.getChild("NeckTie")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight = this.Butt.getChild("LegRight")
        this.ShoesR = this.LegRight.getChild("ShoesR")
        this.LegLeft = this.Butt.getChild("LegLeft")
        this.ShoesL = this.LegLeft.getChild("ShoesL")
        this.Skirt = this.Butt.getChild("Skirt")
        this.EquipBase = this.BodyMain.getChild("EquipBase")
        this.EquipT05 = this.EquipBase.getChild("EquipT05")
        this.EquipT01 = this.EquipBase.getChild("EquipT01")
        this.EquipT04 = this.EquipBase.getChild("EquipT04")
        this.EquipT03 = this.EquipBase.getChild("EquipT03")
        this.EquipHead = this.EquipBase.getChild("EquipHead")
        this.EquipT02 = this.EquipBase.getChild("EquipT02")
        this.ArmRight = this.BodyMain.getChild("ArmRight")
        this.ArmLeft = this.BodyMain.getChild("ArmLeft")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeckCloth = this.GlowBodyMain.getChild("GlowNeckCloth")
        this.GlowHead = this.GlowNeckCloth.getChild("GlowHead")
        this.initFaceParts(this.GlowHead)
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val ctx = computePoseContext(entity, limbSwing, limbSwingAmount, ageInTicks, 0.0f)
        this.isDeadPose = false
        this.poseTranslateY = 0.0f

        applyFaceAndMouth(entity)
        setFlushVisible(entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY))
        applyEquipVisibility(entity)

        if (entity != null && entity.isInDeadPose) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)
        Head.xRot += HEAD_BASE_X_ROT

        applyBasePose(ctx)
        applySpecialPoseAdjustments(entity, ctx, ageInTicks, limbSwing, limbSwingAmount)
        applyHairAndEarAnimation(entity, ctx, ageInTicks, limbSwing, limbSwingAmount)

        syncGlowParts()
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        EarL01.xRot = 1.0f
        EarL01.yRot = -0.4f
        EarL01.zRot = 0.0f
        EarR01.xRot = 1.0f
        EarR01.yRot = 1.0472f
        EarR01.zRot = 0.0f
        EarL02.xRot = -0.8f
        EarL02.yRot = 0.0f
        EarL02.zRot = 0.0f
        EarR02.xRot = -0.2f
        EarR02.yRot = -0.2f
        EarR02.zRot = 0.0f

        EquipBase.zRot = 0.52f
        Head.xRot = 0.0f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        Ahoke.yRot = 0.5236f
        BodyMain.yRot = 0.0f
        BodyMain.xRot = 1.4835f
        HairMidL01.xRot = -0.05f
        HairMidR01.xRot = -0.05f
        HairMidL02.xRot = -0.1f
        HairMidR02.xRot = -0.1f

        ArmLeft.xRot = -0.12f
        ArmLeft.zRot = -0.2f
        ArmRight.xRot = -0.12f
        ArmRight.zRot = 0.2f
        LegLeft.xRot = -0.2618f
        LegRight.xRot = -0.2618f
        LegLeft.yRot = 0.0f
        LegRight.yRot = 0.0f
        LegLeft.zRot = 0.03f
        LegRight.zRot = -0.03f
    }

    private fun applyEquipVisibility(entity: EntityShipBase?) {
        if (entity == null) return
        val showRigging = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_RIGGING)
        EquipBase.visible = showRigging
        HairAnchor.visible = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_ANCHOR)
        val fh1 = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_1)
        val fh2 = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_2)
        val fh3 = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_3)
        EarBase.visible = fh1 || fh2 || fh3
    }

    private fun applyBasePose(ctx: PoseContext) {
        val angleX = ctx.angleX

        BodyMain.xRot = BODY_BASE_X_ROT
        BodyMain.yRot = 0.0f
        Ahoke.yRot = angleX * 0.25f + AHOKE_BASE_Y_ROT

        ArmLeft.xRot = 0.15f
        ArmLeft.zRot = angleX * 0.1f - 0.5236f
        ArmRight.xRot = 0.0f
        ArmRight.yRot = 0.0f
        ArmRight.zRot = -angleX * 0.1f + 0.5236f

        LegLeft.yRot = 0.0f
        LegLeft.zRot = 0.05f
        LegRight.yRot = 0.0f
        LegRight.zRot = -0.05f

        EquipBase.zRot = 0.52f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        ageInTicks: Float,
        limbSwing: Float,
        limbSwingAmount: Float
    ) {
        var legLeftX = ctx.angleAdd1 - 0.21f
        var legRightX = ctx.angleAdd2 - 0.11f

        val isPassenger = entity != null && entity.isPassenger
        val isCrouching = entity != null && entity.isCrouching
        val isSprinting = entity != null && entity.isSprinting

        if (isSprinting || limbSwingAmount > 0.6f) {
            this.setFace(EntityShipBase.FACE_TENSION)
            Head.xRot -= 0.2618f
            BodyMain.xRot = 0.2618f
            HairMidL01.xRot += 0.5f
            HairMidR01.xRot += 0.5f
            HairMidL02.xRot += 0.5f
            HairMidR02.xRot += 0.5f
            ArmLeft.xRot = 0.7f
            ArmLeft.zRot = -1.0472f
            ArmRight.xRot = 0.7f
            ArmRight.zRot = 1.0472f

            legLeftX = Mth.cos(limbSwing * 2.0f) * limbSwingAmount * 1.5f - 0.5f
            legRightX = Mth.cos(limbSwing * 2.0f + Math.PI.toFloat()) * limbSwingAmount * 1.5f - 0.5f

            LegLeft.yRot = 0.0f
            LegLeft.zRot = 0.05f
            LegRight.yRot = 0.0f
            LegRight.zRot = -0.05f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            Head.xRot -= 0.7854f
            BodyMain.xRot = 0.7854f
            ArmLeft.zRot = -0.5f
            ArmRight.zRot = 0.5f
            legLeftX -= 0.8f
            legRightX -= 0.8f
        }

        if (ctx.isSitting || isPassenger) {
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY = 0.575f * 3.3f
                Head.xRot = -1.48f
                Head.yRot = 0.0f
                Head.zRot = 0.0f
                BodyMain.xRot = 1.4835f
                ArmLeft.xRot = -3.0543f
                ArmLeft.zRot = -0.7f
                ArmRight.xRot = -2.8f
                ArmRight.zRot = 0.35f
                legLeftX = 0.0f
                legRightX = -0.2618f
                LegLeft.zRot = 0.1745f
                LegRight.zRot = -0.35f
            } else {
                this.poseTranslateY = 0.45f * 3.2f
                Head.xRot -= 0.7f
                BodyMain.xRot = 0.5236f
                HairL01.xRot -= 0.2f
                HairL02.xRot -= 0.2f
                HairR01.xRot -= 0.2f
                HairR02.xRot -= 0.2f
                ArmLeft.xRot = -0.5236f
                ArmLeft.zRot = 0.3146f
                ArmRight.xRot = -0.5236f
                ArmRight.zRot = -0.3146f
                legLeftX = -2.2689f
                legRightX = -2.2689f
                LegLeft.yRot = -0.3491f
                LegRight.yRot = 0.3491f
            }
        }

        if (entity != null && entity.attackTick > 20) {
            this.poseTranslateY = 0.14f + entity.scaleLevel * 0.07f
            Head.xRot = -0.8727f
            Head.yRot = 1.0472f
            Head.zRot = -0.7f
            BodyMain.xRot = 1.3f
            BodyMain.yRot = -1.57f
            ArmLeft.xRot = 0.0f
            ArmLeft.zRot = -0.5f
            ArmRight.xRot = 0.0f
            ArmRight.zRot = 1.57f
            legLeftX = -1.75f
            legRightX = -1.92f
            EquipBase.zRot = 1.57f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight.xRot = -0.4f
            ArmRight.yRot = 0.0f
            ArmRight.zRot = -0.2f
            ArmRight.xRot += -f8 * 80.0f * (Math.PI.toFloat() / 180f)
            ArmRight.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.2f
            ArmRight.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        LegLeft.xRot = legLeftX
        LegRight.xRot = legRightX
    }

    private fun applyHairAndEarAnimation(
        entity: T?,
        ctx: PoseContext,
        ageInTicks: Float,
        limbSwing: Float,
        limbSwingAmount: Float
    ) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)
        val angleRun = Mth.cos(limbSwing * 1.5f) * limbSwingAmount

        val fh1 = entity!!.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_1)
        val fh2 = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_2)
        val fh3 = entity.getEquipFlag(EntityDestroyerShimakaze.EQUIP_HAIR_FRONT_3)
        val fh4 = fh1 && fh2
        val fh5 = fh1 && fh3
        val fh6 = fh2 && fh3
        val fh7 = fh1 && fh2 && fh3

        if (fh7) {
            EarL01.xRot = angleX * 0.075f + 0.6f
            EarL01.yRot = -0.5f
            EarL01.zRot = 0.0f
            EarR01.xRot = angleX * 0.075f + 1.1f
            EarR01.yRot = 0.5f
            EarR01.zRot = 0.0f
            EarL02.xRot = angleX1 * 0.1f + 0.7f
            EarL02.yRot = 0.1f
            EarL02.zRot = 0.0f
            EarR02.xRot = angleX1 * 0.1f + 1.0f
            EarR02.yRot = -0.1f
            EarR02.zRot = 0.0f
        } else if (fh6) {
            EarL01.xRot = angleX * 0.075f + 1.1f
            EarL01.yRot = -0.5f
            EarL01.zRot = 0.0f
            EarR01.xRot = angleX * 0.075f + 1.1f
            EarR01.yRot = 0.5f
            EarR01.zRot = 0.0f
            EarL02.xRot = angleX1 * 0.1f + 1.0f
            EarL02.yRot = 0.1f
            EarL02.zRot = 0.0f
            EarR02.xRot = angleX1 * 0.1f + 1.0f
            EarR02.yRot = -0.1f
            EarR02.zRot = 0.0f
        } else if (fh5) {
            EarL01.xRot = angleX * 0.075f - 1.1f
            EarL01.yRot = 0.5f
            EarL01.zRot = 0.0f
            EarR01.xRot = angleX1 * 0.075f - 1.1f
            EarR01.yRot = -0.5f
            EarR01.zRot = 0.0f
            EarL02.xRot = angleX * 0.075f - 0.8f
            EarL02.yRot = 0.0f
            EarL02.zRot = -0.5f
            EarR02.xRot = angleX1 * 0.075f - 0.8f
            EarR02.yRot = 0.0f
            EarR02.zRot = 0.5f
        } else if (fh4) {
            EarL01.xRot = angleX * 0.075f + 0.6f
            EarL01.yRot = -0.5f
            EarL01.zRot = 0.0f
            EarR01.xRot = angleX * 0.075f + 0.6f
            EarR01.yRot = 0.5f
            EarR01.zRot = 0.0f
            EarL02.xRot = angleX1 * 0.1f + 0.7f
            EarL02.yRot = 0.1f
            EarL02.zRot = 0.0f
            EarR02.xRot = angleX1 * 0.1f + 0.7f
            EarR02.yRot = -0.1f
            EarR02.zRot = 0.0f
        } else if (fh3) {
            EarL01.xRot = angleX * 0.075f + 0.3f
            EarL01.yRot = -0.8f
            EarL01.zRot = 0.0f
            EarR01.xRot = angleX * 0.075f + 0.9f
            EarR01.yRot = 0.6f
            EarR01.zRot = 0.0f
            EarL02.xRot = angleX1 * 0.1f + 0.6f
            EarL02.yRot = 0.1f
            EarL02.zRot = 0.0f
            EarR02.xRot = angleX1 * 0.1f + 1.0f
            EarR02.yRot = -0.1f
            EarR02.zRot = 0.0f
        }

        HairMidL01.xRot = angleX * 0.07f + 0.14f
        HairMidL02.xRot = -angleX1 * 0.2f + 0.14f
        HairMidR01.xRot = HairMidL01.xRot
        HairMidR02.xRot = HairMidL02.xRot
        HairMidL01.zRot = -0.2618f
        HairMidL02.zRot = -0.14f
        HairMidR01.zRot = 0.2618f
        HairMidR02.zRot = 0.14f

        HairL01.xRot = angleX * 0.06f - 0.2618f
        HairL02.xRot = -angleX1 * 0.1f + 0.2618f
        HairR01.xRot = angleX * 0.06f - 0.2618f
        HairR02.xRot = -angleX1 * 0.1f + 0.2618f
        HairL01.zRot = -0.2618f
        HairL02.zRot = 0.1745f
        HairR01.zRot = 0.2618f
        HairR02.zRot = -0.1745f

        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        HairMidL01.xRot += headX
        HairMidL01.zRot += headZ
        HairMidL02.xRot += headX * 0.5f
        HairMidL02.zRot += headZ * 0.5f
        HairMidR01.xRot += headX
        HairMidR01.zRot += headZ
        HairMidR02.xRot += headX * 0.5f
        HairMidR02.zRot += headZ * 0.5f

        HairL01.xRot += headX
        HairL02.xRot += headX
        HairR01.xRot += headX
        HairR02.xRot += headX
        HairL01.zRot += headZ
        HairL02.zRot += headZ
        HairR01.zRot += headZ
        HairR02.zRot += headZ
    }

    private fun syncGlowParts() {
        if (this.GlowBodyMain != null) {
            GlowBodyMain.copyFrom(BodyMain)
            GlowNeckCloth.copyFrom(NeckCloth)
            GlowHead.copyFrom(Head)
        }
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        BodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    override fun renderGlow(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val usePoseTranslate = poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, poseTranslateY, 0.0f)
        }

        if (GlowBodyMain != null) {
            GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        }

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "destroyer_shimakaze"), "main")

        private val DEAD_TRANSLATE_Y = deadY("ModelDestroyerShimakaze")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelDestroyerShimakaze")
        private const val OFFSET_SCALE = 16.0f

        private val BODY_BASE_X_ROT = -0.1f
        private const val HEAD_BASE_X_ROT = 0.1f
        private const val BUTT_BASE_X_ROT = 0.2618f
        private const val AHOKE_BASE_Y_ROT = 0.5236f

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 37).addBox(-7f, -11f, -4f, 14f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 0f)
            )

            val NeckCloth = BodyMain.addOrReplaceChild(
                "NeckCloth",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.5f, -1.5f, -4.5f, 15f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 0f)
            )

            val Head = NeckCloth.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(24, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(23, 61).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val HairMidR01 = HairMain.addOrReplaceChild(
                "HairMidR01",
                CubeListBuilder.create().texOffs(42, 40).mirror()
                    .addBox(-4.5f, 0f, 0f, 9f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-2.5f, 9f, 2.5f, 0.1396f, -0.0873f, 0.2618f)
            )

            val HairMidR02 = HairMidR01.addOrReplaceChild(
                "HairMidR02",
                CubeListBuilder.create().texOffs(46, 21).mirror()
                    .addBox(-4.5f, 0f, 0f, 9f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 3f, 0.1396f, 0f, 0.1396f)
            )

            val HairMidL01 = HairMain.addOrReplaceChild(
                "HairMidL01",
                CubeListBuilder.create().texOffs(42, 40).addBox(-4.5f, 0f, 0f, 9f, 13f, 8f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2.5f, 9f, 2.5f, 0.1396f, 0.0873f, -0.2618f)
            )

            val HairMidL02 = HairMidL01.addOrReplaceChild(
                "HairMidL02",
                CubeListBuilder.create().texOffs(46, 21).addBox(-4.5f, 0f, 0f, 9f, 14f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 12f, 3f, 0.1396f, 0f, -0.1396f)
            )

            val EarBase = HairMain.addOrReplaceChild(
                "EarBase",
                CubeListBuilder.create().texOffs(80, 113).addBox(0f, 0f, 0f, 4f, 3f, 4f, CubeDeformation(0f)),
                PartPose.offset(-2f, -2f, 2f)
            )

            val EarL01 = EarBase.addOrReplaceChild(
                "EarL01",
                CubeListBuilder.create().texOffs(83, 113).addBox(-1.5f, -10f, -1f, 3f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(4f, 2.5f, 2f)
            )

            val EarL02 = EarL01.addOrReplaceChild(
                "EarL02",
                CubeListBuilder.create().texOffs(82, 113).addBox(-2f, -13f, -1f, 4f, 13f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 0f)
            )

            val EarR01 = EarBase.addOrReplaceChild(
                "EarR01",
                CubeListBuilder.create().texOffs(83, 113).addBox(-1.5f, -10f, -1f, 3f, 10f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, 2.5f, 2f)
            )

            val EarR02 = EarR01.addOrReplaceChild(
                "EarR02",
                CubeListBuilder.create().texOffs(82, 113).addBox(-2f, -13f, -1f, 4f, 13f, 2f, CubeDeformation(0f)),
                PartPose.offset(0f, -9f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(24, 80).addBox(-8f, -7.5f, -8f, 16f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(102, 0).addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-5.5f, 0f, -3f, -0.2618f, 0.1745f, 0.2618f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(103, 1).addBox(-1f, 0f, 0f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 8.5f, 0.5f, 0.1745f, 0f, -0.1745f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(65, 88).addBox(0f, 0f, -12f, 0f, 13f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -14f, -4f, 0f, 0.5236f, 0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(102, 0).addBox(-1f, 0f, 0f, 2f, 9f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(5.5f, 0f, -3f, -0.2618f, -0.1745f, -0.2618f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(103, 1).addBox(-1f, 0f, 0f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-0.2f, 8.5f, 0.5f, 0.2618f, 0f, 0.1745f)
            )

            val HairAnchor = HairL02.addOrReplaceChild(
                "HairAnchor",
                CubeListBuilder.create().texOffs(112, 7).addBox(-1.5f, 0f, 0f, 2f, 5f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 8f, -1f, 0.0873f, 0f, 0.1367f)
            )

            val NeckTie = NeckCloth.addOrReplaceChild(
                "NeckTie",
                CubeListBuilder.create().texOffs(39, 0).addBox(-3.5f, 0f, 0f, 7f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 2.5f, -4.7f, -0.1396f, 0f, 0f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 22).addBox(-8f, 4f, -5.4f, 16f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val LegRight = Butt.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(0, 96).addBox(-3f, 0f, -3f, 6f, 19f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 9.5f, -3f, -0.2618f, 0f, -0.0524f)
            )

            val ShoesR = LegRight.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(88, 15).addBox(-3.5f, 0f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 19f, -0.2f)
            )

            val LegLeft = Butt.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(0, 96).mirror().addBox(-3f, 0f, -3f, 6f, 19f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 9.5f, -3f, -0.2618f, 0f, 0.0524f)
            )

            val ShoesL = LegLeft.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(88, 15).addBox(-3.5f, 0f, -3.5f, 7f, 7f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 19f, -0.2f)
            )

            val Skirt = Butt.addOrReplaceChild(
                "Skirt",
                CubeListBuilder.create().texOffs(50, 0).addBox(-8.5f, 0f, -6f, 17f, 6f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5.5f, 0f, -0.1745f, 0f, 0f)
            )

            val EquipBase = BodyMain.addOrReplaceChild(
                "EquipBase",
                CubeListBuilder.create().texOffs(76, 33).addBox(-7f, 0f, -3.7f, 14f, 8f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(2f, -5f, 7f, 0.1396f, 0f, 0.5236f)
            )

            val EquipT05 = EquipBase.addOrReplaceChild(
                "EquipT05",
                CubeListBuilder.create().texOffs(85, 65).addBox(0f, 0f, 0f, 3f, 31f, 3f, CubeDeformation(0f)),
                PartPose.offset(-8.1f, -8f, 1f)
            )

            val EquipT01 = EquipBase.addOrReplaceChild(
                "EquipT01",
                CubeListBuilder.create().texOffs(85, 65).addBox(0f, 0f, 0f, 3f, 31f, 3f, CubeDeformation(0f)),
                PartPose.offset(5.1f, -8f, 1f)
            )

            val EquipT04 = EquipBase.addOrReplaceChild(
                "EquipT04",
                CubeListBuilder.create().texOffs(85, 65).addBox(0f, 0f, 0f, 3f, 31f, 3f, CubeDeformation(0f)),
                PartPose.offset(-4.8f, -8f, 1f)
            )

            val EquipT03 = EquipBase.addOrReplaceChild(
                "EquipT03",
                CubeListBuilder.create().texOffs(85, 65).addBox(0f, 0f, 0f, 3f, 31f, 3f, CubeDeformation(0f)),
                PartPose.offset(-1.5f, -8f, 1f)
            )

            val EquipHead = EquipBase.addOrReplaceChild(
                "EquipHead",
                CubeListBuilder.create().texOffs(77, 29).addBox(-9f, 0f, 0f, 18f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -3f, -0.3f)
            )

            val EquipT02 = EquipBase.addOrReplaceChild(
                "EquipT02",
                CubeListBuilder.create().texOffs(85, 65).addBox(0f, 0f, 0f, 3f, 31f, 3f, CubeDeformation(0f)),
                PartPose.offset(1.8f, -8f, 1f)
            )

            val ArmRight = BodyMain.addOrReplaceChild(
                "ArmRight",
                CubeListBuilder.create().texOffs(0, 61).addBox(-2.5f, 0f, -2.5f, 5f, 22f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -10.5f, 0f, 0f, 0f, 0.4363f)
            )

            val ArmLeft = BodyMain.addOrReplaceChild(
                "ArmLeft",
                CubeListBuilder.create().texOffs(0, 61).mirror()
                    .addBox(-2.5f, 0f, -2.5f, 5f, 22f, 5f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -10.5f, 0f, 0f, 0f, -0.3491f)
            )

            val GlowBodyMain = partdefinition.addOrReplaceChild(
                "GlowBodyMain",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -12f, 0f)
            )

            val GlowNeckCloth = GlowBodyMain.addOrReplaceChild(
                "GlowNeckCloth",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -10f, 0f)
            )

            val GlowHead = GlowNeckCloth.addOrReplaceChild(
                "GlowHead",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0f, -1.5f, 0f)
            )
            addFaceLayer(GlowHead)

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
