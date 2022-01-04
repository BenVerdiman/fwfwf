package net.minecraft.world.level.block;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockConcretePowder extends BlockFalling {

    private final IBlockData concrete;

    public BlockConcretePowder(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.concrete = block.getBlockData();
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EntityFallingBlock entityfallingblock) {
        if (canHarden(world, blockposition, iblockdata1)) {
            world.setTypeAndData(blockposition, this.concrete, 3);
        }

    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return canHarden(world, blockposition, iblockdata) ? this.concrete : super.getPlacedState(blockactioncontext);
    }

    private static boolean canHarden(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return n(iblockdata) || a(iblockaccess, blockposition);
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

            if (enumdirection != EnumDirection.DOWN || n(iblockdata)) {
                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
                if (n(iblockdata) && !iblockdata.d(iblockaccess, blockposition, enumdirection.opposite())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    private static boolean n(IBlockData iblockdata) {
        return iblockdata.getFluid().a((Tag) TagsFluid.WATER);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return a((IBlockAccess) generatoraccess, blockposition) ? this.concrete : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public int d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.d(iblockaccess, blockposition).col;
    }
}
