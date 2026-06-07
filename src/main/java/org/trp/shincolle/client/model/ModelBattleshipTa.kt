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
import org.trp.shincolle.client.model.LegacyPoseOffsets.sittingY
import org.trp.shincolle.client.model.LegacyPoseOffsets.sneakY
import org.trp.shincolle.entity.EntityBattleshipTa
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase

class ModelBattleshipTa<T : EntityShipBase>(root: ModelPart) : ShipModelHumanoidBase<T>(), IGlowableModel {
    private var isDeadPose = false
    private var isSittingPose = false
    override var poseTranslateY = 0f

    private val BodyMain: ModelPart
    private val NeckCloth: ModelPart
    private val BoobR: ModelPart
    private val BoobL: ModelPart
    private val ArmLeft01: ModelPart
    private val ArmRight01: ModelPart
    private val Butt: ModelPart
    private val EquipLeft: ModelPart?
    private val EquipRight: ModelPart?
    private val Cloak01: ModelPart?
    private val Head: ModelPart
    private val NeckTie: ModelPart
    private val Hair: ModelPart
    private val HairMain: ModelPart
    private val Ahoke: ModelPart
    private val HairL01: ModelPart
    private val HairR01: ModelPart
    private val HairL02: ModelPart
    private val HairR02: ModelPart
    private val HairMidL01: ModelPart
    private val HairMidL02: ModelPart
    private val ArmLeft02: ModelPart?
    private val ArmRight02: ModelPart?
    private val LegRight: ModelPart
    private val LegLeft: ModelPart
    private val ShoesR: ModelPart
    private val ShoesL: ModelPart
    private val Cloak02: ModelPart
    private val Cloak03: ModelPart
    private val Cloak04: ModelPart
    private val Cloak05: ModelPart
    private val GlowBodyMain: ModelPart?
    private val GlowNeckCloth: ModelPart
    private val GlowHead: ModelPart
    private val armLeft02DefaultX: Float
    private val armLeft02DefaultY: Float
    private val armLeft02DefaultZ: Float
    private val armRight02DefaultX: Float
    private val armRight02DefaultY: Float
    private val armRight02DefaultZ: Float

    init {
        this.BodyMain = root.getChild("BodyMain")
        this.ArmRight01 = this.BodyMain.getChild("ArmRight01")
        this.ArmRight02 = this.ArmRight01.getChild("ArmRight02")
        this.EquipLeft = this.BodyMain.getChild("EquipLeft")
        this.BoobR = this.BodyMain.getChild("BoobR")
        this.Butt = this.BodyMain.getChild("Butt")
        this.LegRight = this.Butt.getChild("LegRight")
        this.ShoesR = this.LegRight.getChild("ShoesR")
        this.LegLeft = this.Butt.getChild("LegLeft")
        this.ShoesL = this.LegLeft.getChild("ShoesL")
        this.NeckCloth = this.BodyMain.getChild("NeckCloth")
        this.Head = this.NeckCloth.getChild("Head")
        this.HairMain = this.Head.getChild("HairMain")
        this.HairMidL01 = this.HairMain.getChild("HairMidL01")
        this.HairMidL02 = this.HairMidL01.getChild("HairMidL02")
        this.Hair = this.Head.getChild("Hair")
        this.HairL01 = this.Hair.getChild("HairL01")
        this.HairL02 = this.HairL01.getChild("HairL02")
        this.HairR01 = this.Hair.getChild("HairR01")
        this.HairR02 = this.HairR01.getChild("HairR02")
        this.Ahoke = this.Hair.getChild("Ahoke")
        this.NeckTie = this.NeckCloth.getChild("NeckTie")
        this.Cloak01 = this.BodyMain.getChild("Cloak01")
        this.Cloak02 = this.Cloak01.getChild("Cloak02")
        this.Cloak03 = this.Cloak02.getChild("Cloak03")
        this.Cloak04 = this.Cloak03.getChild("Cloak04")
        this.Cloak05 = this.Cloak04.getChild("Cloak05")
        this.ArmLeft01 = this.BodyMain.getChild("ArmLeft01")
        this.ArmLeft02 = this.ArmLeft01.getChild("ArmLeft02")
        this.BoobL = this.BodyMain.getChild("BoobL")
        this.EquipRight = this.BodyMain.getChild("EquipRight")
        this.GlowBodyMain = root.getChild("GlowBodyMain")
        this.GlowNeckCloth = this.GlowBodyMain.getChild("GlowNeckCloth")
        this.GlowHead = this.GlowNeckCloth.getChild("GlowHead")
        initFaceParts(this.GlowHead)
        this.armLeft02DefaultX = this.ArmLeft02.x
        this.armLeft02DefaultY = this.ArmLeft02.y
        this.armLeft02DefaultZ = this.ArmLeft02.z

        this.armRight02DefaultX = this.ArmRight02.x
        this.armRight02DefaultY = this.ArmRight02.y
        this.armRight02DefaultZ = this.ArmRight02.z
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
        resetPoseState()
        resetOffsets()

        applyFaceAndMouth(entity)
        setFlushVisible(
            entity != null && (entity.emotionPrimary == EntityShipBase.EMOTION_SHY
                    || entity.emotionPrimary == EntityShipBase.EMOTION_HAPPY)
        )
        applyEquipVisibility(entity)

        if (isDeadPose(entity)) {
            applyDeadPose()
            syncGlowParts()
            return
        }

        applyHeadRotation(Head, entity, ageInTicks, netHeadYaw, headPitch)

        applyBasePose(ctx, limbSwing, limbSwingAmount, ageInTicks, headPitch)
        applySpecialPoseAdjustments(entity, ctx, limbSwing, limbSwingAmount, ageInTicks)
        applyHairAnimation(ctx, ageInTicks)

        syncGlowParts()
    }

    private fun resetPoseState() {
        this.isDeadPose = false
        this.isSittingPose = false
        this.poseTranslateY = 0.0f
    }

    private fun resetOffsets() {
        if (ArmLeft02 != null) {
            ArmLeft02.x = armLeft02DefaultX
            ArmLeft02.y = armLeft02DefaultY
            ArmLeft02.z = armLeft02DefaultZ
        }
        if (ArmRight02 != null) {
            ArmRight02.x = armRight02DefaultX
            ArmRight02.y = armRight02DefaultY
            ArmRight02.z = armRight02DefaultZ
        }
    }

    private fun isDeadPose(entity: T?): Boolean {
        return entity != null && entity.isInDeadPose
    }

    private fun applyEquipVisibility(entity: T?) {
        if (entity == null) return
        if (Cloak01 != null) Cloak01.visible = entity.getEquipFlag(EntityBattleshipTa.EQUIP_CLOAK)
        val showEquip = entity.getEquipFlag(EntityBattleshipTa.EQUIP_RIGGING)
        if (EquipLeft != null) EquipLeft.visible = showEquip
        if (EquipRight != null) EquipRight.visible = showEquip
    }

    private fun applyDeadPose() {
        this.isDeadPose = true
        this.poseTranslateY = DEAD_TRANSLATE_Y

        Head.xRot = 0.2f
        Head.yRot = 0.0f
        Head.zRot = 0.0f
        BoobL.xRot = -0.7854f
        BoobR.xRot = -0.7854f
        NeckTie.xRot = -0.7f
        Ahoke.zRot = -0.06f

        BodyMain.xRot = 1.4f

        ArmLeft01.xRot = -2.8f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.8727f

        ArmRight01.xRot = -2.8f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.35f

        EquipLeft!!.xRot = 0.0f
        EquipRight!!.xRot = 0.0f

        HairMidL01.xRot = 0.05f
        HairMidL02.xRot = -0.3f

        LegLeft.xRot = -0.087f
        LegLeft.yRot = 0.0f
        LegLeft.zRot = -0.2618f

        LegRight.xRot = -0.087f
        LegRight.yRot = 0.0f
        LegRight.zRot = 0.4f

        Cloak01!!.xRot = 0.0f
        Cloak02.xRot = 0.0f
        Cloak03.xRot = 0.0f
        Cloak04.xRot = 0.0f
        Cloak05.xRot = 0.0f
    }

    private fun applyBasePose(
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headPitch: Float
    ) {
        val angleX = ctx.angleX
        val angleX1 = Mth.cos(ageInTicks * 0.08f + 0.3f + limbSwing * 0.5f)

        BoobL.xRot = -angleX * 0.06f - 0.7854f
        BoobR.xRot = -angleX * 0.06f - 0.7854f
        NeckTie.xRot = -angleX * 0.1f - 0.7f

        HairMidL01.xRot = angleX * 0.06f + 0.2618f
        HairMidL01.zRot = 0.0f
        HairMidL02.xRot = -angleX1 * 0.08f - 0.087f
        HairMidL02.zRot = 0.0f

        HairL01.xRot = angleX * 0.06f - 0.13f
        HairL01.zRot = -0.05f
        HairL02.xRot = -angleX1 * 0.08f + 0.21f
        HairL02.zRot = 0.05f

        HairR01.xRot = angleX * 0.06f - 0.13f
        HairR01.zRot = 0.087f
        HairR02.xRot = -angleX1 * 0.08f + 0.21f
        HairR02.zRot = -0.05f

        Ahoke.zRot = angleX * 0.1f - 0.06f

        BodyMain.xRot = 0.0f
        BodyMain.yRot = 0.0f
        BodyMain.zRot = 0.0f

        ArmLeft01.xRot = 0.35f
        ArmLeft01.yRot = 0.0f
        ArmLeft01.zRot = 0.2618f

        ArmRight01.xRot = 0.35f
        ArmRight01.yRot = 0.0f
        ArmRight01.zRot = -0.2618f

        LegLeft.yRot = 0.0f
        LegLeft.zRot = 0.14f

        LegRight.yRot = 0.0f
        LegRight.zRot = -0.14f

        EquipLeft!!.xRot = 0.0f
        EquipRight!!.xRot = 0.0f

        Cloak01!!.xRot = 0.0f
        Cloak02.xRot = angleX * 0.05f + 0.15f
        Cloak03.xRot = angleX * 0.05f + 0.18f
        Cloak04.xRot = angleX * 0.05f + 0.15f
        Cloak05.xRot = 0.2f
    }

    private fun applySpecialPoseAdjustments(
        entity: T?,
        ctx: PoseContext,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float
    ) {
        val angleRun = Mth.cos(limbSwing * 0.7f) * limbSwingAmount * 0.6f
        val angleRun2 = Mth.cos(limbSwing * 0.7f + Math.PI.toFloat()) * limbSwingAmount * 0.6f
        var addk1 = angleRun - 0.35f
        var addk2 = angleRun2 - 0.087f

        if (entity != null && entity.shipDepth > 0.0) {
            this.poseTranslateY += ctx.angleX * 0.05f + 0.025f
        }

        val isCrouching = entity != null && entity.isCrouching
        val isSitting =
            ctx.isSitting || (entity != null && entity.isPassenger && (entity.vehicle !is EntityMountBase))
        val isSprinting = entity != null && entity.isSprinting || limbSwingAmount > 0.9f

        if (isSprinting) {
            addk2 -= 0.35f
            HairMidL01.xRot += angleRun * 0.1f + 0.2f
            HairMidL02.xRot += angleRun2 * 0.1f + 0.2f
            BodyMain.xRot = 0.087f
            BodyMain.yRot = 0.0f
            ArmLeft01.xRot = angleRun2
            ArmLeft01.zRot = -0.1745f
            ArmRight01.xRot = angleRun
            ArmRight01.zRot = 0.1745f
            LegLeft.zRot = 0.05f
            LegRight.zRot = -0.05f
            Cloak02.xRot = angleRun * 0.05f + 0.3f
            Cloak03.xRot = angleRun * 0.05f + 0.3f
            Cloak04.xRot = angleRun * 0.05f + 0.35f
            Cloak05.xRot = angleRun * 0.05f + 0.4f
        }

        if (isCrouching) {
            this.poseTranslateY += SNEAK_TRANSLATE_Y
            addk1 -= 0.52f
            addk2 -= 1.0f
            BodyMain.xRot = 0.7f
            ArmLeft01.xRot = -0.35f
            ArmLeft01.zRot = 0.26f
            ArmRight01.xRot = -0.35f
            ArmRight01.zRot = -0.26f
            Cloak02.xRot = ctx.angleX * 0.05f + 0.15f
            Cloak03.xRot = ctx.angleX * 0.05f + 0.15f
            Cloak04.xRot = ctx.angleX * 0.05f + 0.2f
            Cloak05.xRot = ctx.angleX * 0.05f + 0.2f
        }

        if (isSitting) {
            this.isSittingPose = true
            if (entity != null && hasLegacyState(entity, 1, 4)) {
                this.poseTranslateY += 0.65f * 2.9f
                addk1 = -0.087f
                addk2 = 0.174f
                Head.xRot -= 1.4f
                Head.yRot *= 0.5f
                BodyMain.xRot = 1.4f
                ArmLeft01.xRot = -2.8f
                ArmLeft01.zRot = -0.8727f
                ArmRight01.xRot = -2.6f
                ArmRight01.zRot = 0.35f
                LegLeft.zRot = 0.2618f
                LegRight.zRot = -0.2618f
                Cloak01!!.xRot = 0.0f
                Cloak02.xRot = ctx.angleX * 0.01f + 0.15f
                Cloak03.xRot = ctx.angleX * 0.01f + 0.18f
                Cloak04.xRot = 0.0f
                Cloak05.xRot = 0.0f
            } else {
                this.poseTranslateY += SITTING_TRANSLATE_Y
                addk1 = -1.0472f
                addk2 = -1.3f
                Head.xRot += 0.35f
                HairMidL01.xRot += 0.2f
                HairMidL02.xRot += 0.2f
                BodyMain.xRot = -0.7f
                ArmLeft01.xRot = 1.0472f
                ArmLeft01.zRot = -0.2618f
                ArmRight01.xRot = 1.0472f
                ArmRight01.zRot = 0.2618f
                LegLeft.zRot = 0.6f
                LegRight.zRot = -0.6f
                EquipLeft!!.xRot = 0.7f
                EquipRight!!.xRot = 0.7f
                Cloak01!!.xRot = 0.7f
                Cloak02.xRot = ctx.angleX * 0.03f + 0.15f
                Cloak03.xRot = ctx.angleX * 0.03f + 0.15f
                Cloak04.xRot = ctx.angleX * 0.03f + 0.5f
                Cloak05.xRot = ctx.angleX * 0.03f + 0.2f
            }
        }

        if (entity != null && entity.attackTick > 0) {
            ArmLeft01.xRot = -1.3f
            ArmLeft01.yRot = -0.7f
            ArmLeft01.zRot = 0.0f
            ArmRight01.xRot = 0.17f
            ArmRight01.zRot = 0.17f
            EquipLeft!!.xRot = 0.2618f
        }

        val partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
        val customAttackAnim = if (entity != null) entity.getCustomAttackAnim(partialTick) else 0.0f
        if (customAttackAnim > 0.0f) {
            val f7 = Mth.sin(customAttackAnim * customAttackAnim * Math.PI.toFloat())
            val f8 = Mth.sin(Mth.sqrt(customAttackAnim) * Math.PI.toFloat())
            ArmRight01.xRot += -f8 * 120.0f * (Math.PI.toFloat() / 180f)
            ArmRight01.yRot += -f7 * 20.0f * (Math.PI.toFloat() / 180f) + 0.5f
            ArmRight01.zRot += -f8 * 20.0f * (Math.PI.toFloat() / 180f)
        }

        LegLeft.xRot = addk1
        LegRight.xRot = addk2
    }

    private fun applyHairAnimation(ctx: PoseContext?, ageInTicks: Float) {
        val headX = Head.xRot * -0.5f
        val headZ = Head.zRot * -0.5f

        HairMidL01.xRot += headX
        HairMidL01.zRot += headZ
        HairMidL02.xRot += headX * 0.5f
        HairMidL02.zRot += headZ * 0.5f

        HairL01.zRot += headZ
        HairL02.zRot += headZ
        HairR01.zRot += headZ
        HairR02.zRot += headZ

        HairL01.xRot += headX
        HairL02.xRot += headX
        HairR01.xRot += headX
        HairR02.xRot += headX
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
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
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
        if (GlowBodyMain == null) return
        val usePoseTranslate = this.poseTranslateY != 0.0f
        if (usePoseTranslate) {
            poseStack.pushPose()
            poseStack.translate(0.0f, this.poseTranslateY, 0.0f)
        }

        GlowBodyMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)

        if (usePoseTranslate) {
            poseStack.popPose()
        }
    }

    companion object {
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "battleship_ta"), "main")

        private const val OFFSET_SCALE = 16.0f
        private val DEAD_TRANSLATE_Y = deadY("ModelBattleshipTa")
        private val SNEAK_TRANSLATE_Y = sneakY("ModelBattleshipTa")
        private val SITTING_TRANSLATE_Y = sittingY("ModelBattleshipTa")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val BodyMain = partdefinition.addOrReplaceChild(
                "BodyMain",
                CubeListBuilder.create().texOffs(0, 104).addBox(-6.5f, -11f, -4f, 13f, 17f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, -12f, 0f)
            )

            val ArmRight01 = BodyMain.addOrReplaceChild(
                "ArmRight01",
                CubeListBuilder.create().texOffs(24, 56).addBox(-3f, 0f, -3f, 6f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-7f, -10.5f, 0f, 0f, 0f, 0.1571f)
            )

            val ArmRight02 = ArmRight01.addOrReplaceChild(
                "ArmRight02",
                CubeListBuilder.create().texOffs(0, 56).addBox(-2.5f, 0f, -2.5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val EquipLeft = BodyMain.addOrReplaceChild(
                "EquipLeft",
                CubeListBuilder.create().texOffs(0, 0).addBox(0f, 0f, 0f, 14f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(9f, -13f, -6f, 0f, -0.1396f, 0.2618f)
            )

            val BoobR = BodyMain.addOrReplaceChild(
                "BoobR",
                CubeListBuilder.create().texOffs(0, 74).addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-3.8f, -9f, -3.5f, -0.7854f, -0.1745f, -0.0873f)
            )

            val Butt = BodyMain.addOrReplaceChild(
                "Butt",
                CubeListBuilder.create().texOffs(0, 19).addBox(-8f, 4f, -5.5f, 16f, 8f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 0f, 0f, 0.2618f, 0f, 0f)
            )

            val LegRight = Butt.addOrReplaceChild(
                "LegRight",
                CubeListBuilder.create().texOffs(0, 91).addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-4.5f, 9.5f, -3f, -0.2618f, 0f, -0.0524f)
            )

            val ShoesR = LegRight.addOrReplaceChild(
                "ShoesR",
                CubeListBuilder.create().texOffs(22, 71).addBox(-3.5f, 0f, -3.5f, 7f, 19f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, -0.2f)
            )

            val LegLeft = Butt.addOrReplaceChild(
                "LegLeft",
                CubeListBuilder.create().texOffs(0, 91).mirror().addBox(-3f, 0f, -3f, 6f, 12f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(4.5f, 9.5f, -3f, -0.2618f, 0f, 0.0524f)
            )

            val ShoesL = LegLeft.addOrReplaceChild(
                "ShoesL",
                CubeListBuilder.create().texOffs(22, 71).mirror()
                    .addBox(-3.5f, 0f, -3.5f, 7f, 19f, 7f, CubeDeformation(0f)),
                PartPose.offset(0f, 7f, -0.2f)
            )

            val NeckCloth = BodyMain.addOrReplaceChild(
                "NeckCloth",
                CubeListBuilder.create().texOffs(46, 14).addBox(-7.5f, -1.5f, -4.5f, 15f, 12f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, 0f)
            )

            val Head = NeckCloth.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(44, 101)
                    .addBox(-7f, -14.5f, -6.5f, 14f, 14f, 13f, CubeDeformation(0f)),
                PartPose.offset(0f, -1.5f, 0f)
            )

            val HairMain = Head.addOrReplaceChild(
                "HairMain",
                CubeListBuilder.create().texOffs(48, 56).addBox(-7.5f, 0f, 0f, 15f, 9f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -15f, -3f)
            )

            val HairMidL01 = HairMain.addOrReplaceChild(
                "HairMidL01",
                CubeListBuilder.create().texOffs(48, 34).addBox(-7.5f, 0f, 0f, 15f, 13f, 9f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.5f, 0.2618f, 0f, 0f)
            )

            val HairMidL02 = HairMidL01.addOrReplaceChild(
                "HairMidL02",
                CubeListBuilder.create().texOffs(0, 34).addBox(-7f, 0f, 0f, 14f, 14f, 7f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 1.8f, -0.0873f, 0f, 0f)
            )

            val Hair = Head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(50, 75).addBox(-8f, -8f, -7.2f, 16f, 17f, 8f, CubeDeformation(0f)),
                PartPose.offset(0f, -7.5f, 0f)
            )

            val HairL01 = Hair.addOrReplaceChild(
                "HairL01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 11f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(6.5f, 0f, -6f, -0.1745f, -0.1745f, -0.0524f)
            )

            val HairL02 = HairL01.addOrReplaceChild(
                "HairL02",
                CubeListBuilder.create().texOffs(89, 103).addBox(-1f, 0f, 0f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 9f, 0f, 0.2618f, 0f, 0.0524f)
            )

            val HairR01 = Hair.addOrReplaceChild(
                "HairR01",
                CubeListBuilder.create().texOffs(88, 100).addBox(-1f, 0f, 0f, 2f, 9f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-6.5f, 0f, -6f, -0.1396f, 0.1745f, 0.0873f)
            )

            val HairR02 = HairR01.addOrReplaceChild(
                "HairR02",
                CubeListBuilder.create().texOffs(89, 103).addBox(-1f, 0f, 0f, 2f, 9f, 2f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.2f, 7f, 0.5f, 0.1745f, 0f, -0.0524f)
            )

            val Ahoke = Hair.addOrReplaceChild(
                "Ahoke",
                CubeListBuilder.create().texOffs(37, 101).addBox(-4.5f, 0f, 0f, 10f, 10f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, -7.3f, -7.5f, -0.1367f, -0.2276f, 0f)
            )

            val NeckTie = NeckCloth.addOrReplaceChild(
                "NeckTie",
                CubeListBuilder.create().texOffs(24, 97).addBox(-3f, 0f, 0f, 6f, 7f, 0f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0.5f, 1.3f, -5.2f, -0.7f, 0.1396f, 0.1396f)
            )

            val Cloak01 = BodyMain.addOrReplaceChild(
                "Cloak01",
                CubeListBuilder.create().texOffs(128, 0).addBox(-11.5f, 0f, 0f, 23f, 5f, 10f, CubeDeformation(0f)),
                PartPose.offset(0f, -10f, -4.4f)
            )

            val Cloak02 = Cloak01.addOrReplaceChild(
                "Cloak02",
                CubeListBuilder.create().texOffs(128, 15).addBox(-12f, 0f, 0f, 24f, 6f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 0.3f, 0.0524f, 0f, 0f)
            )

            val Cloak03 = Cloak02.addOrReplaceChild(
                "Cloak03",
                CubeListBuilder.create().texOffs(128, 31).addBox(-12.5f, 0f, 0f, 25f, 7f, 10f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 5f, 0.3f, 0.0524f, 0f, 0f)
            )

            val Cloak04 = Cloak03.addOrReplaceChild(
                "Cloak04",
                CubeListBuilder.create().texOffs(128, 48).addBox(-13.5f, 0f, 0f, 27f, 8f, 11f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 7f, 0f, 0.0873f, 0f, 0f)
            )

            val Cloak05 = Cloak04.addOrReplaceChild(
                "Cloak05",
                CubeListBuilder.create().texOffs(128, 67).addBox(-14.5f, 0f, 0f, 29f, 9f, 12f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(0f, 8f, 0f, 0.0873f, 0f, 0f)
            )

            val ArmLeft01 = BodyMain.addOrReplaceChild(
                "ArmLeft01",
                CubeListBuilder.create().texOffs(24, 56).mirror().addBox(-3f, 0f, -3f, 6f, 9f, 6f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(7f, -10.5f, 0f, 0f, 0f, -0.1571f)
            )

            val ArmLeft02 = ArmLeft01.addOrReplaceChild(
                "ArmLeft02",
                CubeListBuilder.create().texOffs(0, 56).addBox(-2.5f, 0f, -2.5f, 5f, 13f, 5f, CubeDeformation(0f)),
                PartPose.offset(0f, 9f, 0f)
            )

            val BoobL = BodyMain.addOrReplaceChild(
                "BoobL",
                CubeListBuilder.create().texOffs(0, 74).mirror().addBox(-3.5f, 0f, 0f, 7f, 5f, 4f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(3.8f, -9f, -3.5f, -0.7854f, 0.1745f, 0.0873f)
            )

            val EquipRight = BodyMain.addOrReplaceChild(
                "EquipRight",
                CubeListBuilder.create().texOffs(38, 0).addBox(-12f, 0f, 0f, 12f, 3f, 3f, CubeDeformation(0f)),
                PartPose.offsetAndRotation(-9f, -12f, -2f, 0f, 0.1396f, -0.1745f)
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

            return LayerDefinition.create(meshdefinition, 256, 128)
        }
    }
}
