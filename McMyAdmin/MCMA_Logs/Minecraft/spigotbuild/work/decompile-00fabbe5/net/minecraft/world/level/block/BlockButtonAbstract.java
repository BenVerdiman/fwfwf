package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockButtonAbstract extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private static final int PRESSED_DEPTH = 1;
    private static final int UNPRESSED_DEPTH = 2;
    protected static final int HALF_AABB_HEIGHT = 2;
    protected static final int HALF_AABB_WIDTH = 3;
    protected static final VoxelShape CEILING_AABB_X = Block.a(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape CEILING_AABB_Z = Block.a(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape FLOOR_AABB_X = Block.a(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
    protected static final VoxelShape FLOOR_AABB_Z = Block.a(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
    protected static final VoxelShape NORTH_AABB = Block.a(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
    protected static final VoxelShape WEST_AABB = Block.a(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape EAST_AABB = Block.a(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
    protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.a(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.a(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.a(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.a(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
    protected static final VoxelShape PRESSED_NORTH_AABB = Block.a(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape PRESSED_SOUTH_AABB = Block.a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
    protected static final VoxelShape PRESSED_WEST_AABB = Block.a(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape PRESSED_EAST_AABB = Block.a(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
    private final boolean sensitive;

    protected BlockButtonAbstract(boolean flag, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockButtonAbstract.FACING, EnumDirection.NORTH)).set(BlockButtonAbstract.POWERED, false)).set(BlockButtonAbstract.FACE, BlockPropertyAttachPosition.WALL));
        this.sensitive = flag;
    }

    private int c() {
        return this.sensitive ? 30 : 20;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING);
        boolean flag = (Boolean) iblockdata.get(BlockButtonAbstract.POWERED);

        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockButtonAbstract.FACE)) {
            case FLOOR:
                if (enumdirection.n() == EnumDirection.EnumAxis.X) {
                    return flag ? BlockButtonAbstract.PRESSED_FLOOR_AABB_X : BlockButtonAbstract.FLOOR_AABB_X;
                }

                return flag ? BlockButtonAbstract.PRESSED_FLOOR_AABB_Z : BlockButtonAbstract.FLOOR_AABB_Z;
            case WALL:
                switch (enumdirection) {
                    case EAST:
                        return flag ? BlockButtonAbstract.PRESSED_EAST_AABB : BlockButtonAbstract.EAST_AABB;
                    case WEST:
                        return flag ? BlockButtonAbstract.PRESSED_WEST_AABB : BlockButtonAbstract.WEST_AABB;
                    case SOUTH:
                        return flag ? BlockButtonAbstract.PRESSED_SOUTH_AABB : BlockButtonAbstract.SOUTH_AABB;
                    case NORTH:
                    default:
                        return flag ? BlockButtonAbstract.PRESSED_NORTH_AABB : BlockButtonAbstract.NORTH_AABB;
                }
            case CEILING:
            default:
                return enumdirection.n() == EnumDirection.EnumAxis.X ? (flag ? BlockButtonAbstract.PRESSED_CEILING_AABB_X : BlockButtonAbstract.CEILING_AABB_X) : (flag ? BlockButtonAbstract.PRESSED_CEILING_AABB_Z : BlockButtonAbstract.CEILING_AABB_Z);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            return EnumInteractionResult.CONSUME;
        } else {
            this.d(iblockdata, world, blockposition);
            this.a(entityhuman, world, blockposition, true);
            world.a((Entity) entityhuman, GameEvent.BLOCK_PRESS, blockposition);
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    public void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, true), 3);
        this.f(iblockdata, world, blockposition);
        world.getBlockTickList().a(blockposition, this, this.c());
    }

    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        generatoraccess.playSound(flag ? entityhuman : null, blockposition, this.a(flag), SoundCategory.BLOCKS, 0.3F, flag ? 0.6F : 0.5F);
    }

    protected abstract SoundEffect a(boolean flag);

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.a(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
                this.f(iblockdata, world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockButtonAbstract.POWERED) ? 15 : 0;
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockButtonAbstract.POWERED) && h(iblockdata) == enumdirection ? 15 : 0;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            if (this.sensitive) {
                this.e(iblockdata, worldserver, blockposition);
            } else {
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, false), 3);
                this.f(iblockdata, (World) worldserver, blockposition);
                this.a((EntityHuman) null, worldserver, blockposition, false);
                worldserver.a(GameEvent.BLOCK_UNPRESS, blockposition);
            }

        }
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && this.sensitive && !(Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            this.e(iblockdata, world, blockposition);
        }
    }

    private void e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        List<? extends Entity> list = world.a(EntityArrow.class, iblockdata.getShape(world, blockposition).getBoundingBox().a(blockposition));
        boolean flag = !list.isEmpty();
        boolean flag1 = (Boolean) iblockdata.get(BlockButtonAbstract.POWERED);

        if (flag != flag1) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, flag), 3);
            this.f(iblockdata, world, blockposition);
            this.a((EntityHuman) null, world, blockposition, flag);
            world.a((Entity) list.stream().findFirst().orElse((Object) null), flag ? GameEvent.BLOCK_PRESS : GameEvent.BLOCK_UNPRESS, blockposition);
        }

        if (flag) {
            world.getBlockTickList().a(new BlockPosition(blockposition), this, this.c());
        }

    }

    private void f(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition, this);
        world.applyPhysics(blockposition.shift(h(iblockdata).opposite()), this);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockButtonAbstract.FACING, BlockButtonAbstract.POWERED, BlockButtonAbstract.FACE);
    }
}
