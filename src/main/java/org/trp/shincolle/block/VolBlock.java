package org.trp.shincolle.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class VolBlock extends Block {
    public static final MapCodec<VolBlock> CODEC = simpleCodec(properties -> new VolBlock());

    public VolBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .strength(3.0F, 200.0F)
                .lightLevel(state -> 15)
                .sound(SoundType.SAND)
                .requiresCorrectToolForDrops());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) != 0) {
            return;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        boolean openBelow = !belowState.isFaceSturdy(level, belowPos, Direction.UP);

        if (openBelow) {
            spawnDripParticle(level, pos, random, 0.18D + random.nextDouble() * 0.64D, 0.05D + random.nextDouble() * 0.9D, 0.02D);
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) {
                continue;
            }
            BlockPos sidePos = pos.relative(direction);
            BlockState sideState = level.getBlockState(sidePos);
            if (sideState.isFaceSturdy(level, sidePos, direction.getOpposite())) {
                continue;
            }

            double x = direction.getStepX() == 0 ? 0.2D + random.nextDouble() * 0.6D : 0.5D + direction.getStepX() * 0.48D;
            double y = 0.1D + random.nextDouble() * 0.7D;
            double z = direction.getStepZ() == 0 ? 0.2D + random.nextDouble() * 0.6D : 0.5D + direction.getStepZ() * 0.48D;
            spawnDripParticle(level, pos, random, x, y, z);
        }
    }

    private static void spawnDripParticle(Level level, BlockPos pos, RandomSource random, double offsetX, double offsetY, double offsetZ) {
        level.addParticle(
                ParticleTypes.DRIPPING_WATER,
                pos.getX() + offsetX,
                pos.getY() + offsetY,
                pos.getZ() + offsetZ,
                (random.nextDouble() - 0.5D) * 0.01D,
                -0.02D - random.nextDouble() * 0.01D,
                (random.nextDouble() - 0.5D) * 0.01D
        );
    }

    public boolean isBeaconBase(BlockState state, BlockGetter level, BlockPos pos, BlockPos beaconPos) {
        return true;
    }
}
