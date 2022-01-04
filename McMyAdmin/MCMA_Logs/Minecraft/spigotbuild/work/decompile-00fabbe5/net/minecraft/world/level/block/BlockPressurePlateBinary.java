package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockPressurePlateBinary extends BlockPressurePlateAbstract {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private final BlockPressurePlateBinary.EnumMobType sensitivity;

    protected BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType blockpressureplatebinary_enummobtype, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockPressurePlateBinary.POWERED, false));
        this.sensitivity = blockpressureplatebinary_enummobtype;
    }

    @Override
    protected int getPower(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockPressurePlateBinary.POWERED) ? 15 : 0;
    }

    @Override
    protected IBlockData a(IBlockData iblockdata, int i) {
        return (IBlockData) iblockdata.set(BlockPressurePlateBinary.POWERED, i > 0);
    }

    @Override
    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        } else {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
        }

    }

    @Override
    protected void b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
        } else {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
        }

    }

    @Override
    protected int b(World world, BlockPosition blockposition) {
        AxisAlignedBB axisalignedbb = BlockPressurePlateBinary.TOUCH_AABB.a(blockposition);
        List list;

        switch (this.sensitivity) {
            case EVERYTHING:
                list = world.getEntities((Entity) null, axisalignedbb);
                break;
            case MOBS:
                list = world.a(EntityLiving.class, axisalignedbb);
                break;
            default:
                return 0;
        }

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!entity.isIgnoreBlockTrigger()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPressurePlateBinary.POWERED);
    }

    public static enum EnumMobType {

        EVERYTHING, MOBS;

        private EnumMobType() {}
    }
}
