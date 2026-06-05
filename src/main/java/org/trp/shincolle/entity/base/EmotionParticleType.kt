package org.trp.shincolle.entity.base

enum class EmotionParticleType(val id: Int, @JvmField val iconColumn: Int, @JvmField val iconRow: Int, label: String) {
    SWEAT_DROP_BIG(0, 0, 0, "sweat_drop_big"),
    HEART(1, 1, 0, "heart"),
    SWEAT_DROPS(2, 1, 8, "sweat_drops"),
    QUESTION(3, 2, 0, "question"),
    EXCLAMATION(4, 2, 8, "exclamation"),
    SILENCE(5, 3, 0, "ellipsis"),
    ANGER(6, 3, 8, "anger"),
    MUSIC_NOTE(7, 4, 0, "music_note"),
    TEARS(8, 5, 0, "tears"),
    DROOL(9, 5, 8, "drool"),
    DIZZY_EYES(10, 6, 0, "dizzy_eyes"),
    HAPPY_GLANCE(11, 6, 8, "happy_glance"),
    SHOCK(12, 7, 0, "shock"),
    HAPPY_BOB(13, 8, 0, "happy_bob"),
    SPARKLE_EYES(14, 8, 8, "sparkle_eyes"),
    POUT_BOUNCE(15, 9, 0, "kissy_bounce"),
    LAUGH(16, 9, 8, "laugh"),
    EVIL_GRIN(17, 10, 0, "evil_grin"),
    SHAKE_HEAD(18, 11, 0, "shake_head"),
    TONGUE_OUT(19, 11, 8, "tongue_out"),
    ORZ(20, 12, 0, "orz"),
    CIRCLE(21, 12, 8, "circle"),
    CROSS(22, 12, 9, "cross"),
    EXCLAMATION_QUESTION(23, 12, 10, "exclamation_question"),
    FIST(24, 12, 11, "rock"),
    OPEN_HAND(25, 12, 12, "paper"),
    PEACE(26, 12, 13, "scissors"),
    SMUG_SMILE(27, 12, 14, "smug_smile"),
    GASP(28, 12, 15, "gasp"),
    BLINK(29, 13, 0, "blink"),
    SIGH(30, 13, 8, "smug_sigh"),
    BLUSH(31, 14, 0, "blush"),
    SCRATCH_HEAD(32, 14, 4, "scratch_head"),
    SILLY_TONGUE(33, 14, 10, "silly_tongue"),
    GLOOM(34, 14, 15, "gloom");

    val label: String?

    init {
        this.label = label
    }

    companion object {
        private val BY_ID: MutableMap<Int?, EmotionParticleType?> = HashMap<Int?, EmotionParticleType?>()

        init {
            for (type in EmotionParticleType.entries) {
                BY_ID.put(type.id, type)
            }
        }

        @JvmStatic
        fun fromId(id: Int): EmotionParticleType? {
            return BY_ID.getOrDefault(id, EmotionParticleType.SWEAT_DROP_BIG)
        }
    }
}
