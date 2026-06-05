package org.trp.shincolle.entity.base

internal object ShipFollowDecisionResolver {
    fun shouldTryTeleport(state: State): Boolean {
        return ShipBrainRecoverySupport.shouldTryTeleportRecovery(
            state.recovery,
            ShipRecoveryDecisionResolver.State(
                state.forceRecovery,
                state.ownerDistanceSqr,
                ShipAiNumbers.TELEPORT_DISTANCE_SQ
            ),
            ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS
        )
    }

    @JvmRecord
    internal data class State(
        val recovery: ShipMovementRecoveryState?,
        val forceRecovery: Boolean,
        val ownerDistanceSqr: Double
    )
}
