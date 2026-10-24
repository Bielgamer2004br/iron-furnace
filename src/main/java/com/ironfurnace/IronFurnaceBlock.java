package com.ironfurnace;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bloco da Fornalha de Ferro. Funciona igual à fornalha vanilla (mesma GUI,
 * mesmas receitas de smelting), mas executa o "tick" de cozimento várias vezes
 * por tick de jogo, o que a deixa mais rápida.
 */
public class IronFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<IronFurnaceBlock> CODEC = simpleCodec(IronFurnaceBlock::new);

    public IronFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IronFurnaceBlockEntity furnace) {
            player.openMenu(furnace);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IronFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        return createTickerHelper(type, IronFurnaceMod.IRON_FURNACE_BE.get(), (lvl, pos, st, furnace) -> {
            if (!(lvl instanceof ServerLevel server)) {
                return;
            }
            for (int i = 0; i < IronFurnaceMod.SPEED_MULTIPLIER; i++) {
                // Relê o estado a cada passada, pois o bloco troca entre aceso/apagado.
                BlockState current = server.getBlockState(pos);
                if (!current.is(IronFurnaceMod.IRON_FURNACE.get())) {
                    break;
                }
                AbstractFurnaceBlockEntity.serverTick(server, pos, current, furnace);
            }
        });
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && random.nextDouble() < 0.1) {
            level.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }
}
