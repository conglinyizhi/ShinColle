package org.trp.shincolle.client.model

object LegacyPoseOffsets {
    @JvmStatic
    fun deadY(modelName: String): Float {
        return when (modelName) {
            "ModelAirfieldHime" -> 0.55f * 3
            "ModelDestroyerAkatsuki" -> 1.9f
            "ModelDestroyerI" -> 1.5f
            "ModelDestroyerRo" -> 1.5f
            "ModelDestroyerHa" -> 2.5f
            "ModelDestroyerNi" -> 2.8f
            "ModelDestroyerHibiki" -> 1.9f
            "ModelDestroyerIkazuchi" -> 1.9f
            "ModelDestroyerShimakaze" -> 1.9f
            "ModelDestroyerInazuma" -> 1.9f
            "ModelDestroyerHime" -> 0.59f * 3.2f
            "ModelCruiserAtago" -> 1.9f
            "ModelCruiserTakao" -> 1.5f
            "ModelCruiserTenryuu" -> 0.53f * 3.1f
            "ModelCruiserTatsuta" -> 0.51f * 3.1f
            "ModelCarrierHime" -> 0.49f * 3
            "ModelCarrierWDemon" -> 0.48f * 3
            "ModelCarrierAkagi" -> 0.53f * 3.2f
            "ModelCarrierKaga" -> 0.48f * 3
            "ModelCarrierWo" -> 0.41f * 3
            "ModelBattleshipHime" -> 1.05f * 1.5f
            "ModelBattleshipYamato" -> 0.58f * 3
            "ModelBattleshipRu" -> 0.65f * 3
            "ModelBattleshipRe" -> 1.13f * 1.5f
            "ModelBattleshipTa" -> 0.62f * 3
            "ModelBattleshipNagato" -> 0.65f * 3
            "ModelBBKongou" -> 0.7f * 3
            "ModelBBKirishima" -> 0.7f * 3
            "ModelBBHaruna" -> 0.72f * 3
            "ModelBBHiei" -> 0.6f * 3
            "ModelSubmHime" -> 0.62f * 3
            "ModelSubmRo500" -> 0.55f * 3.2f
            "ModelSubmU511" -> 0.41f * 3.2f
            "ModelSubmSo" -> 0.0f * 3
            "ModelSubmKa" -> 0.0f * 3
            "ModelSubmYo" -> 0.39f * 3
            "ModelSSNH" -> 0.27f * 3
            "ModelTransportWa" -> 0.12f * 3
            "ModelCAHime" -> 0.2f * 5
            "ModelIsolatedHime" -> 0.43f * 3.5f
            "ModelNorthernHime" -> 1.1f
            "ModelMidwayHime" -> 0.59f * 3
            else -> 0.0f
        }
    }

    @JvmStatic
    fun sneakY(modelName: String): Float {
        return when (modelName) {
            "ModelAirfieldHime" -> 0.07f
            "ModelDestroyerAkatsuki" -> 0.05f
            "ModelDestroyerHibiki" -> 0.05f
            "ModelDestroyerIkazuchi" -> 0.05f
            "ModelDestroyerShimakaze" -> 0.05f
            "ModelDestroyerInazuma" -> 0.05f
            "ModelDestroyerHime" -> 0.07f
            "ModelCruiserAtago" -> 0.05f
            "ModelCruiserTakao" -> 0.06f
            "ModelCruiserTenryuu" -> 0.06f
            "ModelCruiserTatsuta" -> 0.06f
            "ModelCarrierHime" -> 0.05f
            "ModelCarrierWDemon" -> 0.05f
            "ModelCarrierAkagi" -> 0.1f
            "ModelCarrierKaga" -> 0.1f
            "ModelCarrierWo" -> 0.05f
            "ModelBattleshipHime" -> 0.58f
            "ModelBattleshipYamato" -> 0.07f
            "ModelBattleshipRu" -> 0.05f
            "ModelBattleshipRe" -> 0.1f
            "ModelBattleshipTa" -> 0.05f
            "ModelBattleshipNagato" -> 0.06f
            "ModelBBKongou" -> 0.14f
            "ModelBBKirishima" -> 0.14f
            "ModelBBHaruna" -> 0.14f
            "ModelBBHiei" -> 0.14f
            "ModelSubmHime" -> 0.09f
            "ModelSubmRo500" -> 0.1f
            "ModelSubmU511" -> 0.1f
            "ModelSubmSo" -> 0.05f
            "ModelSubmKa" -> 0.05f
            "ModelSubmYo" -> 0.05f
            "ModelSSNH" -> 0.01f
            "ModelTransportWa" -> 0.05f
            "ModelIsolatedHime" -> 0.06f
            "ModelNorthernHime" -> 0.02f
            "ModelMidwayHime" -> 0.09f
            else -> 0.0f
        }
    }

    @JvmStatic
    fun sittingY(modelName: String): Float {
        return when (modelName) {
            "ModelAirfieldHime" -> 0.37f * 4
            "ModelDestroyerI" -> 0.5f
            "ModelDestroyerRo" -> 1.3f
            "ModelDestroyerHa" -> 1.5f
            "ModelDestroyerNi" -> 1.8f
            "ModelDestroyerIkazuchi" -> 1.3f
            "ModelDestroyerHime" -> 0.43f * 3.2f
            "ModelCruiserTakao" -> 0.35f * 3.1f
            "ModelCruiserTenryuu" -> 0.46f * 3.2f
            "ModelCruiserTatsuta" -> 0.47f * 3.2f
            "ModelCarrierAkagi" -> 0.36f * 3.2f
            "ModelCarrierKaga" -> 0.36f * 3.2f
            "ModelBattleshipHime" -> 0.83f / 2
            "ModelBattleshipYamato" -> 0.54f * 3
            "ModelBattleshipRu" -> 0.54f * 2
            "ModelBattleshipRe" -> 0.51f * 3
            "ModelBattleshipTa" -> 0.51f * 3
            "ModelBattleshipNagato" -> 0.55f * 2.7f
            "ModelBBKongou" -> 0.31f * 3
            "ModelBBKirishima" -> 0.31f * 3
            "ModelBBHaruna" -> 0.55f * 3
            "ModelBBHiei" -> 0.55f * 3
            "ModelSubmHime" -> 0.495f * 3.2f
            "ModelSubmRo500" -> 0.41f * 3.2f
            "ModelSubmU511" -> 0.4f * 3.2f
            "ModelSubmSo" -> 0.45f * 3
            "ModelSubmKa" -> 0.45f * 3
            "ModelSSNH" -> 0.26f * 3.2f
            "ModelTransportWa" -> 0.42f * 3.2f
            "ModelCAHime" -> 0.21f * 4.1f
            "ModelIsolatedHime" -> 0.48f * 3.5f
            "ModelNorthernHime" -> 1.1f
            "ModelMidwayHime" -> 0.51f * 3
            else -> 0.0f
        }
    }

    @JvmStatic
    fun sittingAltY(modelName: String): Float {
        return when (modelName) {
            "ModelCruiserTenryuu" -> 0.41f
            "ModelBBHaruna" -> 0.69f * 3.0f
            "ModelBBHiei" -> 0.69f * 2.7f
            "ModelBBKirishima" -> 0.55f * 3
            "ModelBBKongou" -> 0.69f * 2.5f
            else -> 0.0f
        }
    }

    @JvmStatic
    fun ridingY(modelName: String): Float {
        return when (modelName) {
            "ModelAirfieldHime" -> 0.22f
            "ModelDestroyerIkazuchi" -> -0.375f
            "ModelBattleshipHime" -> 1.01f
            "ModelSubmHime" -> 0.22f
            "ModelBBHiei" -> 0.53f * 3
            "ModelIsolatedHime" -> 0.02f
            "ModelNorthernHime" -> 0.24f
            "ModelMidwayHime" -> 0.08f
            else -> 0.0f
        }
    }

    @JvmStatic
    fun sprintY(modelName: String): Float {
        return when (modelName) {
            "ModelSubmSo" -> 0.05f
            "ModelSubmKa" -> 0.06f
            "ModelSubmYo" -> 0.1f
            else -> 0.0f
        }
    }

    fun deadZ(modelName: String): Float {
        return when (modelName) {
            "ModelSubmYo" -> -0.1f
            else -> 0.0f
        }
    }

    @JvmStatic
    fun ridingZ(modelName: String): Float {
        return when (modelName) {
            "ModelBattleshipHime" -> -0.05f
            "ModelNorthernHime" -> 0.27f
            else -> 0.0f
        }
    }

    fun baseY(modelName: String): Float {
        return when (modelName) {
            "ModelBattleshipHime" -> 0.5f
            else -> 0.0f
        }
    }
}
