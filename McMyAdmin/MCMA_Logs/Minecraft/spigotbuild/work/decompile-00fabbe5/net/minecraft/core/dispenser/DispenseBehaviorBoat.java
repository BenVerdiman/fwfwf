package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BlockDispenser;

public class DispenseBehaviorBoat extends DispenseBehaviorItem {

    private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();
    private final EntityBoat.EnumBoatType type;

    public DispenseBehaviorBoat(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.type = entityboat_enumboattype;
    }

    @Override
    public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
        WorldServer worldserver = isourceblock.getWorld();
        double d0 = isourceblock.getX() + (double) ((float) enumdirection.getAdjacentX() * 1.125F);
        double d1 = isourceblock.getY() + (double) ((float) enumdirection.getAdjacentY() * 1.125F);
        double d2 = isourceblock.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 1.125F);
        BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
        double d3;

        if (worldserver.getFluid(blockposition).a((Tag) TagsFluid.WATER)) {
            d3 = 1.0D;
        } else {
            if (!worldserver.getType(blockposition).isAir() || !worldserver.getFluid(blockposition.down()).a((Tag) TagsFluid.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(isourceblock, itemstack);
            }

            d3 = 0.0D;
        }

        EntityBoat entityboat = new EntityBoat(worldserver, d0, d1 + d3, d2);

        entityboat.setType(this.type);
        entityboat.setYRot(enumdirection.o());
        worldserver.addEntity(entityboat);
        itemstack.subtract(1);
        return itemstack;
    }

    @Override
    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
    }
}
