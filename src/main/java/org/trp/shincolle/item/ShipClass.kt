package org.trp.shincolle.item

enum class ShipClass(code: String, textureName: String) {
    DESTROYER("DD", "shipspawnegg0"),
    LIGHT_CRUISER("CL", "shipspawnegg1"),
    HEAVY_CRUISER("CA", "shipspawnegg2"),
    BATTLESHIP("BB", "shipspawnegg3"),
    AUXILIARY_OILER("AO", "shipspawnegg4"),
    SUBMARINE("SS", "shipspawnegg5"),
    DEMON("DE", "shipspawnegg6"),
    PRINCESS("PR", "shipspawnegg7"),
    AIRCRAFT_CARRIER("CV", "shipspawnegg8");

    val code: String?
    val textureName: String?

    init {
        this.code = code
        this.textureName = textureName
    }
}
