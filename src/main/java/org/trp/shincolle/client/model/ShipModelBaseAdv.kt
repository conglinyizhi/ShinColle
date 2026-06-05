package org.trp.shincolle.client.model

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.ArmedModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.PartDefinition
import net.minecraft.world.entity.HumanoidArm
import org.trp.shincolle.entity.base.EntityShipBase
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

abstract class ShipModelBaseAdv<T : EntityShipBase?> : EntityModel<T?>(), ArmedModel {
    var Face0: ModelPart? = null
    var Face1: ModelPart? = null
    var Face2: ModelPart? = null
    var Face3: ModelPart? = null
    var Face4: ModelPart? = null
    @JvmField
    var Mouth0: ModelPart? = null
    @JvmField
    var Mouth1: ModelPart? = null
    @JvmField
    var Mouth2: ModelPart? = null
    var Flush0: ModelPart? = null
    var Flush1: ModelPart? = null

    protected var offsetItem: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    protected var rotateItem: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    protected var offsetBlock: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    protected var rotateBlock: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    protected var modelScale: Float = 1.0f
    private var armMain: Array<ModelPart?>?
    private var armOff: Array<ModelPart?>?
    private var armsResolved = false
    fun initFaceParts(headPart: ModelPart) {
        this.Face0 = getChildOrNull(headPart, "Face0")
        this.Face1 = getChildOrNull(headPart, "Face1")
        this.Face2 = getChildOrNull(headPart, "Face2")
        this.Face3 = getChildOrNull(headPart, "Face3")
        this.Face4 = getChildOrNull(headPart, "Face4")

        this.Mouth0 = getChildOrNull(headPart, "Mouth0")
        this.Mouth1 = getChildOrNull(headPart, "Mouth1")
        this.Mouth2 = getChildOrNull(headPart, "Mouth2")

        this.Flush0 = getChildOrNull(headPart, "Flush0")
        this.Flush1 = getChildOrNull(headPart, "Flush1")
    }

    fun getScale(entity: EntityShipBase?): Float {
        return getLegacyScale(entity)
    }

    val poseTranslateY: Float
        get() {
            val field: Field? =
                POSE_TRANSLATE_Y_FIELDS.computeIfAbsent(javaClass) { type: Class<*>? ->
                    findPoseTranslateYField(type)
                }
            if (field == null) {
                return 0.0f
            }
            try {
                return field.getFloat(this)
            } catch (ignored: IllegalAccessException) {
                return 0.0f
            }
        }

    private fun getChildOrNull(parent: ModelPart, name: String): ModelPart? {
        return if (parent.hasChild(name)) parent.getChild(name) else null
    }

    fun setFace(emo: Int) {
        if (Face0 == null) return

        resetFaceParts()

        when (emo) {
            0 -> setFacePartVisible(0, false)
            1 -> setFacePartVisible(1, false)
            2 -> setFacePartVisible(2, false)
            3 -> setFacePartVisible(3, false)
            4 -> setFacePartVisible(4, false)
            5 -> setFacePartVisible(0, true)
            6 -> setFacePartVisible(1, true)
            7 -> setFacePartVisible(2, true)
            8 -> setFacePartVisible(3, true)
            9 -> setFacePartVisible(4, true)
            else -> setFacePartVisible(0, false)
        }
    }

    fun setMouth(emo: Int) {
        if (Mouth0 == null) return
        resetMouthParts()

        when (emo) {
            0 -> setMouthPartVisible(0, false)
            1 -> setMouthPartVisible(1, false)
            2 -> setMouthPartVisible(2, false)
            3 -> setMouthPartVisible(0, true)
            4 -> setMouthPartVisible(1, true)
            5 -> setMouthPartVisible(2, true)
            else -> setMouthPartVisible(0, false)
        }
    }

    fun setFlushVisible(show: Boolean) {
        if (Flush0 == null || Flush1 == null) {
            return
        }
        Flush0!!.visible = show
        Flush1!!.visible = show
    }

    protected fun resetFaceParts() {
        Face0!!.visible = false
        Face1!!.visible = false
        Face2!!.visible = false
        Face3!!.visible = false
        Face4!!.visible = false
        Face0!!.yRot = 0.0f
        Face1!!.yRot = 0.0f
        Face2!!.yRot = 0.0f
        Face3!!.yRot = 0.0f
        Face4!!.yRot = 0.0f
    }

    protected fun resetMouthParts() {
        Mouth0!!.visible = false
        Mouth1!!.visible = false
        Mouth2!!.visible = false
        Mouth0!!.yRot = 0.0f
        Mouth1!!.yRot = 0.0f
        Mouth2!!.yRot = 0.0f
    }

    protected fun getFacePart(index: Int): ModelPart? {
        return when (index) {
            0 -> Face0
            1 -> Face1
            2 -> Face2
            3 -> Face3
            4 -> Face4
            else -> null
        }
    }

    protected fun getMouthPart(index: Int): ModelPart? {
        return when (index) {
            0 -> Mouth0
            1 -> Mouth1
            2 -> Mouth2
            else -> null
        }
    }

    fun getArmForSide(side: HumanoidArm?): Array<ModelPart>? {
        resolveArmParts()
        return if (side == HumanoidArm.RIGHT) armMain else armOff
    }

    fun getHeldItemOffset(entity: EntityShipBase?, side: HumanoidArm?, isBlock: Boolean): FloatArray? {
        return if (isBlock) offsetBlock else offsetItem
    }

    fun getHeldItemRotate(entity: EntityShipBase?, side: HumanoidArm?, isBlock: Boolean): FloatArray? {
        return if (isBlock) rotateBlock else rotateItem
    }

    override fun translateToHand(arm: HumanoidArm, poseStack: PoseStack) {
        val parts = getArmForSide(arm)
        if (parts == null) {
            return
        }
        for (part in parts) {
            part.translateAndRotate(poseStack)
        }
    }

    private fun resolveArmParts() {
        if (armsResolved) {
            return
        }
        armMain = resolveArmParts("ArmRight")
        armOff = resolveArmParts("ArmLeft")
        armsResolved = true
    }

    private fun resolveArmParts(baseName: String): Array<ModelPart?>? {
        val parts: MutableList<ModelPart?> = ArrayList<ModelPart?>()
        addArmPart(parts, "BodyMain")
        addArmPart(parts, baseName + "01")
        addArmPart(parts, baseName + "02")
        addArmPart(parts, baseName + "03")
        if (parts.isEmpty()) {
            addArmPart(parts, baseName)
        }
        return if (parts.isEmpty()) null else parts.toTypedArray<ModelPart?>()
    }

    private fun addArmPart(parts: MutableList<ModelPart?>, fieldName: String) {
        val part = findModelPartField(fieldName)
        if (part != null) {
            parts.add(part)
        }
    }

    private fun findModelPartField(fieldName: String): ModelPart? {
        var type: Class<*>? = this.javaClass
        while (type != null) {
            try {
                val field = type.getDeclaredField(fieldName)
                field.setAccessible(true)
                val value = field.get(this)
                if (value is ModelPart) {
                    return value
                }
            } catch (ignored: NoSuchFieldException) {
            } catch (ignored: IllegalAccessException) {
                return null
            }
            type = type.getSuperclass()
        }
        return null
    }

    protected fun setFacePartVisible(index: Int, mirror: Boolean) {
        val part = getFacePart(index)
        if (part == null) return
        part.visible = true
        part.yRot = if (mirror) Math.PI.toFloat() else 0.0f
    }

    protected fun setMouthPartVisible(index: Int, mirror: Boolean) {
        val part = getMouthPart(index)
        if (part == null) return
        part.visible = true
        part.yRot = if (mirror) Math.PI.toFloat() else 0.0f
    }

    protected fun getLegacyState(entity: T?, index: Int): Int {
        return if (entity != null) entity.getStateEmotion(index) else 0
    }

    protected fun hasLegacyState(entity: T?, index: Int, value: Int): Boolean {
        return getLegacyState(entity, index) == value
    }

    protected fun hasLegacyModelFlag(entity: T?, flagIndex: Int): Boolean {
        val state = getLegacyState(entity, 0)
        val mask = 1 shl flagIndex
        return (state and mask) == mask
    }


    fun getLegacyScale(entity: EntityShipBase?): Float {
        val base = when (this.javaClass.getSimpleName()) {
            "ModelBattleshipRe", "ModelDestroyerAkatsuki", "ModelDestroyerHa", "ModelDestroyerHibiki", "ModelDestroyerHime", "ModelDestroyerI", "ModelDestroyerIkazuchi", "ModelDestroyerInazuma", "ModelDestroyerNi", "ModelDestroyerRo", "ModelDestroyerShimakaze" -> 0.4f
            "ModelCruiserAtago", "ModelCruiserTakao", "ModelCruiserTatsuta", "ModelCruiserTenryuu" -> 0.43f
            "ModelBattleshipHime", "ModelBattleshipTa", "ModelBattleshipNagato", "ModelBattleshipRu", "ModelBattleshipYamato" -> 0.5f
            "ModelCarrierAkagi", "ModelCarrierKaga", "ModelCarrierHime" -> 0.46f
            "ModelCarrierWDemon", "ModelAirfieldHime" -> 0.47f
            "ModelCarrierWo" -> 0.44f
            "ModelBBHaruna", "ModelBBHiei", "ModelBBKirishima", "ModelBBKongou" -> 0.45f
            "ModelCAHime" -> 0.45f
            "ModelNorthernHime" -> 0.34f
            "ModelSSNH" -> 0.32f
            "ModelSubmHime", "ModelSubmSo", "ModelSubmKa", "ModelSubmYo" -> 0.48f
            "ModelTransportWa" -> 0.4f
            "ModelIsolatedHime" -> 0.38f
            "ModelHeavyCruiserNe" -> 0.4f
            "ModelHeavyCruiserRi" -> 0.41f
            "ModelMidwayHime" -> 0.48f
            "ModelHarbourHime" -> 0.53f
            else -> 0.34f
        }

        var level = if (entity != null) entity.getScaleLevel() else 0
        level = max(0, min(level, 3))
        return base * (level + 1)
    }

    companion object {
        private val POSE_TRANSLATE_Y_FIELDS: MutableMap<Class<*>?, Field?> = ConcurrentHashMap<Class<*>?, Field?>()

        @JvmStatic
        fun addFaceLayer(headPartDef: PartDefinition) {
            headPartDef.addOrReplaceChild(
                "Face0",
                CubeListBuilder.create().texOffs(98, 63).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face1",
                CubeListBuilder.create().texOffs(98, 76).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face2",
                CubeListBuilder.create().texOffs(98, 89).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face3",
                CubeListBuilder.create().texOffs(98, 102).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face4",
                CubeListBuilder.create().texOffs(98, 115).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )

            headPartDef.addOrReplaceChild(
                "Mouth0",
                CubeListBuilder.create().texOffs(100, 53).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth1",
                CubeListBuilder.create().texOffs(100, 58).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth2",
                CubeListBuilder.create().texOffs(114, 53).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )

            headPartDef.addOrReplaceChild(
                "Flush0",
                CubeListBuilder.create().texOffs(114, 58).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(-6.0f, -3.0f, -6.9f)
            )
            headPartDef.addOrReplaceChild(
                "Flush1",
                CubeListBuilder.create().texOffs(114, 58).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(6.0f, -3.0f, -6.9f)
            )
        }

        @JvmStatic
        fun addFaceLayerHibiki(headPartDef: PartDefinition) {
            headPartDef.addOrReplaceChild(
                "Face0",
                CubeListBuilder.create().texOffs(98, 63).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face1",
                CubeListBuilder.create().texOffs(98, 76).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face2",
                CubeListBuilder.create().texOffs(98, 89).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face3",
                CubeListBuilder.create().texOffs(98, 102).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face4",
                CubeListBuilder.create().texOffs(98, 115).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )

            headPartDef.addOrReplaceChild(
                "Mouth0",
                CubeListBuilder.create().texOffs(22, 52).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth1",
                CubeListBuilder.create().texOffs(100, 58).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth2",
                CubeListBuilder.create().texOffs(114, 56).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )

            headPartDef.addOrReplaceChild(
                "Flush0",
                CubeListBuilder.create().texOffs(114, 61).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(-6.0f, -3.0f, -6.8f)
            )
            headPartDef.addOrReplaceChild(
                "Flush1",
                CubeListBuilder.create().texOffs(114, 61).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(6.0f, -3.0f, -6.8f)
            )
        }

        @JvmStatic
        fun addFaceLayerCAHime(headPartDef: PartDefinition) {
            headPartDef.addOrReplaceChild(
                "Face0",
                CubeListBuilder.create().texOffs(98, 63).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -8.5f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face1",
                CubeListBuilder.create().texOffs(98, 76).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -8.5f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face2",
                CubeListBuilder.create().texOffs(98, 89).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -8.5f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face3",
                CubeListBuilder.create().texOffs(98, 102).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -8.5f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face4",
                CubeListBuilder.create().texOffs(98, 115).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -8.5f, -6.1f)
            )

            headPartDef.addOrReplaceChild(
                "Mouth0",
                CubeListBuilder.create().texOffs(100, 53).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -0.7f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth1",
                CubeListBuilder.create().texOffs(100, 58).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -0.7f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth2",
                CubeListBuilder.create().texOffs(114, 53).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -0.7f, -6.2f)
            )

            headPartDef.addOrReplaceChild(
                "Flush0",
                CubeListBuilder.create().texOffs(114, 58).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(-6.0f, 0.7f, -6.8f)
            )
            headPartDef.addOrReplaceChild(
                "Flush1",
                CubeListBuilder.create().texOffs(114, 58).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(6.0f, 0.7f, -6.8f)
            )
        }

        @JvmStatic
        fun addFaceLayerWo(headPartDef: PartDefinition) {
            headPartDef.addOrReplaceChild(
                "Face0",
                CubeListBuilder.create().texOffs(98, 63).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face1",
                CubeListBuilder.create().texOffs(98, 76).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face2",
                CubeListBuilder.create().texOffs(98, 89).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face3",
                CubeListBuilder.create().texOffs(98, 102).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )
            headPartDef.addOrReplaceChild(
                "Face4",
                CubeListBuilder.create().texOffs(98, 115).addBox(-7.0f, 0.0f, -0.5f, 14.0f, 12.0f, 1.0f),
                PartPose.offset(0.0f, -12.2f, -6.1f)
            )

            headPartDef.addOrReplaceChild(
                "Mouth0",
                CubeListBuilder.create().texOffs(69, 91).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth1",
                CubeListBuilder.create().texOffs(69, 96).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )
            headPartDef.addOrReplaceChild(
                "Mouth2",
                CubeListBuilder.create().texOffs(83, 91).addBox(-3.0f, 0.0f, -0.5f, 6.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, -4.2f, -6.2f)
            )

            headPartDef.addOrReplaceChild(
                "Flush0",
                CubeListBuilder.create().texOffs(83, 96).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(-6.0f, -3.0f, -6.8f)
            )
            headPartDef.addOrReplaceChild(
                "Flush1",
                CubeListBuilder.create().texOffs(83, 96).addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.0f, 0.0f),
                PartPose.offset(6.0f, -3.0f, -6.8f)
            )
        }

        private fun findPoseTranslateYField(type: Class<*>?): Field? {
            var current = type
            while (current != null) {
                try {
                    val field = current.getDeclaredField("poseTranslateY")
                    field.setAccessible(true)
                    return field
                } catch (ignored: NoSuchFieldException) {
                    current = current.getSuperclass()
                }
            }
            return null
        }
    }
}
