package org.trp.shincolle.entity.base

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.level.ServerLevel
import org.trp.shincolle.init.ModParticles
import kotlin.math.max

internal class EntityShipBaseReactions(private val ship: EntityShipBase) {
    private var emotesTick = 0
    private var emotionParticleSeq = 0

    fun tickEmotes() {
        if (this.emotesTick > 0) {
            this.emotesTick--
        }
    }

    fun getEmotesTick(): Int {
        return this.emotesTick
    }

    fun applyEmotesReaction(type: Int) {
        if (this.emotesTick > 10 && type == 2) {
            return
        }
        if (this.emotesTick > 0 && type != 2) {
            return
        }
        when (type) {
            0 -> {
                if (this.ship.getRandom().nextInt(7) == 0) {
                    setEmotesTick(50)
                    reactionNormal()
                }
            }

            1 -> {
                if (this.ship.getRandom().nextInt(9) == 0) {
                    setEmotesTick(60)
                    reactionStranger()
                }
            }

            2 -> {
                setEmotesTick(40)
                reactionDamaged()
            }

            3 -> {
                if (this.ship.getRandom().nextInt(6) == 0) {
                    setEmotesTick(60)
                    reactionAttack()
                }
            }

            4 -> {
                if (this.ship.getRandom().nextInt(3) == 0) {
                    setEmotesTick(20)
                    reactionIdle()
                }
            }

            5 -> {
                if (this.ship.getRandom().nextInt(3) == 0) {
                    setEmotesTick(25)
                    reactionCommand()
                }
            }

            6 -> reactionShock()
            else -> {}
        }
    }

    fun applyParticleEmotion(type: EmotionParticleType) {
        if (this.ship.level().isClientSide) {
            spawnEmotionParticleClient(type)
            return
        }
        if (this.ship.level() !is ServerLevel) {
            return
        }
        val nextSeq = this.emotionParticleSeq++ and 0x7FFF
        val packed = (nextSeq shl 16) or (type.id and 0xFF)
        this.ship.setEmotionParticlePacked(packed)
    }

    fun applyParticleEmotion(typeId: Int) {
        applyParticleEmotion(EmotionParticleType.fromId(typeId) ?: EmotionParticleType.SWEAT_DROP_BIG)
    }

    fun spawnEmotionParticleClient(type: EmotionParticleType) {
        if (this.ship.level() !is ClientLevel) {
            return
        }
        val clientLevel = this.ship.level() as ClientLevel
        val baseX = this.ship.x + (this.ship.random.nextDouble() - 0.5) * 0.2
        val baseY = this.ship.y + this.ship.bbHeight * 0.6
        val baseZ = this.ship.z + (this.ship.random.nextDouble() - 0.5) * 0.2
        val height = (this.ship.bbHeight * 0.6).toFloat()
        clientLevel.addParticle(
            ModParticles.PARTICLE_EMOTION.get(), baseX, baseY, baseZ,
            height.toDouble(), this.ship.id.toDouble(), type.id.toDouble()
        )
    }

    private val moraleLevel: Int
        get() {
            val morale = this.ship.morale
            if (morale > 5100) return 0
            if (morale > 3900) return 1
            if (morale > 2100) return 2
            if (morale > 900) return 3
            return 4
        }

    fun setEmotesTick(ticks: Int) {
        this.emotesTick = max(this.emotesTick, ticks)
    }

    private fun reactionNormal() {
        when (this.moraleLevel) {
            0 -> {
                this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_HAPPY
                val emotes = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.BLUSH,
                    EmotionParticleType.DIZZY_EYES,
                    EmotionParticleType.POUT_BOUNCE,
                    EmotionParticleType.HEART,
                    EmotionParticleType.MUSIC_NOTE
                )
                applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
            }

            1 -> {
                this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_SHY
                val emotes = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.HEART,
                    EmotionParticleType.LAUGH,
                    EmotionParticleType.MUSIC_NOTE
                )
                applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
            }

            2 -> {
                this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_SHY
                val emotes = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.SIGH,
                    EmotionParticleType.MUSIC_NOTE,
                    EmotionParticleType.PEACE,
                    EmotionParticleType.HAPPY_GLANCE,
                    EmotionParticleType.BLINK
                )
                applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
            }

            3 -> {
                val emotes = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.SIGH,
                    EmotionParticleType.SWEAT_DROPS,
                    EmotionParticleType.QUESTION,
                    EmotionParticleType.SWEAT_DROP_BIG
                )
                applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
            }

            else -> {
                val emotes = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.TEARS,
                    EmotionParticleType.SWEAT_DROPS,
                    EmotionParticleType.ORZ,
                    EmotionParticleType.SILENCE,
                    EmotionParticleType.GLOOM
                )
                applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
            }
        }
    }

    private fun reactionStranger() {
        this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_ANGRY
        if (this.ship.getRandom().nextBoolean()) {
            applyParticleEmotion(
                if (this.ship.getRandom().nextBoolean())
                    EmotionParticleType.ANGER
                else
                    EmotionParticleType.CROSS
            )
        } else {
            val emotes = arrayOf<EmotionParticleType?>(
                EmotionParticleType.DROOL,
                EmotionParticleType.SWEAT_DROPS,
                EmotionParticleType.ORZ,
                EmotionParticleType.TEARS,
                EmotionParticleType.SWEAT_DROP_BIG,
                EmotionParticleType.GLOOM
            )
            applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
        }
    }

    private fun reactionAttack() {
        if (this.moraleLevel == 0) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_HAPPY
            val emotes = arrayOf<EmotionParticleType?>(
                EmotionParticleType.SILLY_TONGUE,
                EmotionParticleType.EVIL_GRIN,
                EmotionParticleType.TONGUE_OUT,
                EmotionParticleType.LAUGH,
                EmotionParticleType.MUSIC_NOTE
            )
            applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
        } else {
            val emotes = arrayOf<EmotionParticleType?>(
                EmotionParticleType.SPARKLE_EYES,
                EmotionParticleType.SIGH,
                EmotionParticleType.MUSIC_NOTE,
                EmotionParticleType.EXCLAMATION,
                EmotionParticleType.MUSIC_NOTE,
                EmotionParticleType.ANGER
            )
            applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
        }
    }

    private fun reactionDamaged() {
        if (this.moraleLevel <= 2) {
            val emotes = arrayOf<EmotionParticleType?>(
                EmotionParticleType.SIGH,
                EmotionParticleType.SILENCE,
                EmotionParticleType.SWEAT_DROPS,
                EmotionParticleType.QUESTION,
                EmotionParticleType.TEARS
            )
            applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
        } else {
            val emotes = arrayOf<EmotionParticleType?>(
                EmotionParticleType.SIGH,
                EmotionParticleType.SILENCE,
                EmotionParticleType.SWEAT_DROPS,
                EmotionParticleType.QUESTION,
                EmotionParticleType.SWEAT_DROP_BIG,
                EmotionParticleType.TEARS
            )
            applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
        }
    }

    private fun reactionIdle() {
        when (this.moraleLevel) {
            0, 1 -> {
                val emotesSparkling = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.SILLY_TONGUE,
                    EmotionParticleType.EVIL_GRIN,
                    EmotionParticleType.TONGUE_OUT,
                    EmotionParticleType.DROOL,
                    EmotionParticleType.HEART,
                    EmotionParticleType.POUT_BOUNCE,
                    EmotionParticleType.LAUGH,
                    EmotionParticleType.SPARKLE_EYES,
                    EmotionParticleType.MUSIC_NOTE
                )
                applyParticleEmotion(emotesSparkling[this.ship.getRandom().nextInt(emotesSparkling.size)]!!)
            }

            2 -> {
                val emotesNormal = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.HAPPY_GLANCE,
                    EmotionParticleType.QUESTION,
                    EmotionParticleType.HAPPY_BOB,
                    EmotionParticleType.DROOL,
                    EmotionParticleType.SHAKE_HEAD,
                    EmotionParticleType.LAUGH,
                    EmotionParticleType.BLINK
                )
                applyParticleEmotion(emotesNormal[this.ship.getRandom().nextInt(emotesNormal.size)]!!)
            }

            else -> {
                val emotesTired = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.SWEAT_DROP_BIG,
                    EmotionParticleType.SWEAT_DROPS,
                    EmotionParticleType.QUESTION,
                    EmotionParticleType.TEARS,
                    EmotionParticleType.DIZZY_EYES,
                    EmotionParticleType.ORZ,
                    EmotionParticleType.SCRATCH_HEAD
                )
                applyParticleEmotion(emotesTired[this.ship.getRandom().nextInt(emotesTired.size)]!!)
            }
        }
    }

    private fun reactionCommand() {
        when (this.moraleLevel) {
            0, 1, 2 -> {
                val emotesOk = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.CIRCLE,
                    EmotionParticleType.EXCLAMATION,
                    EmotionParticleType.SPARKLE_EYES,
                    EmotionParticleType.HAPPY_GLANCE,
                    EmotionParticleType.HAPPY_BOB
                )
                applyParticleEmotion(emotesOk[this.ship.getRandom().nextInt(emotesOk.size)]!!)
            }

            else -> {
                val emotesTired = arrayOf<EmotionParticleType?>(
                    EmotionParticleType.SWEAT_DROP_BIG,
                    EmotionParticleType.SILLY_TONGUE,
                    EmotionParticleType.QUESTION,
                    EmotionParticleType.DIZZY_EYES,
                    EmotionParticleType.HAPPY_BOB,
                    EmotionParticleType.SCRATCH_HEAD
                )
                applyParticleEmotion(emotesTired[this.ship.getRandom().nextInt(emotesTired.size)]!!)
            }
        }
    }

    private fun reactionShock() {
        val emotes = arrayOf<EmotionParticleType?>(
            EmotionParticleType.SWEAT_DROP_BIG,
            EmotionParticleType.TEARS,
            EmotionParticleType.EXCLAMATION,
            EmotionParticleType.SHOCK
        )
        applyParticleEmotion(emotes[this.ship.getRandom().nextInt(emotes.size)]!!)
    }
}
