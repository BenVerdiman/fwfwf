package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.IInventoryHolder;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.BlockHopper;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class TileEntityHopper extends TileEntityLootable implements IHopper {

    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private NonNullList<ItemStack> items;
    private int cooldownTime;
    private long tickedGameTime;

    public TileEntityHopper(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.HOPPER, blockposition, iblockdata);
        this.items = NonNullList.a(5, ItemStack.EMPTY);
        this.cooldownTime = -1;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.EMPTY);
        if (!this.c(nbttagcompound)) {
            ContainerUtil.b(nbttagcompound, this.items);
        }

        this.cooldownTime = nbttagcompound.getInt("TransferCooldown");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        nbttagcompound.setInt("TransferCooldown", this.cooldownTime);
        return nbttagcompound;
    }

    @Override
    public int getSize() {
        return this.items.size();
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        this.e((EntityHuman) null);
        return ContainerUtil.a(this.f(), i, j);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.e((EntityHuman) null);
        this.f().set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.hopper");
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityHopper tileentityhopper) {
        --tileentityhopper.cooldownTime;
        tileentityhopper.tickedGameTime = world.getTime();
        if (!tileentityhopper.j()) {
            tileentityhopper.setCooldown(0);
            a(world, blockposition, iblockdata, tileentityhopper, () -> {
                return a(world, (IHopper) tileentityhopper);
            });
        }

    }

    private static boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityHopper tileentityhopper, BooleanSupplier booleansupplier) {
        if (world.isClientSide) {
            return false;
        } else {
            if (!tileentityhopper.j() && (Boolean) iblockdata.get(BlockHopper.ENABLED)) {
                boolean flag = false;

                if (!tileentityhopper.isEmpty()) {
                    flag = a(world, blockposition, iblockdata, (IInventory) tileentityhopper);
                }

                if (!tileentityhopper.i()) {
                    flag |= booleansupplier.getAsBoolean();
                }

                if (flag) {
                    tileentityhopper.setCooldown(8);
                    a(world, blockposition, iblockdata);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean i() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (!itemstack.isEmpty() && itemstack.getCount() == itemstack.getMaxStackSize());

        return false;
    }

    private static boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, IInventory iinventory) {
        IInventory iinventory1 = b(world, blockposition, iblockdata);

        if (iinventory1 == null) {
            return false;
        } else {
            EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockHopper.FACING)).opposite();

            if (b(iinventory1, enumdirection)) {
                return false;
            } else {
                for (int i = 0; i < iinventory.getSize(); ++i) {
                    if (!iinventory.getItem(i).isEmpty()) {
                        ItemStack itemstack = iinventory.getItem(i).cloneItemStack();
                        ItemStack itemstack1 = addItem(iinventory, iinventory1, iinventory.splitStack(i, 1), enumdirection);

                        if (itemstack1.isEmpty()) {
                            iinventory1.update();
                            return true;
                        }

                        iinventory.setItem(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private static IntStream a(IInventory iinventory, EnumDirection enumdirection) {
        return iinventory instanceof IWorldInventory ? IntStream.of(((IWorldInventory) iinventory).getSlotsForFace(enumdirection)) : IntStream.range(0, iinventory.getSize());
    }

    private static boolean b(IInventory iinventory, EnumDirection enumdirection) {
        return a(iinventory, enumdirection).allMatch((i) -> {
            ItemStack itemstack = iinventory.getItem(i);

            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static boolean c(IInventory iinventory, EnumDirection enumdirection) {
        return a(iinventory, enumdirection).allMatch((i) -> {
            return iinventory.getItem(i).isEmpty();
        });
    }

    public static boolean a(World world, IHopper ihopper) {
        IInventory iinventory = c(world, ihopper);

        if (iinventory != null) {
            EnumDirection enumdirection = EnumDirection.DOWN;

            return c(iinventory, enumdirection) ? false : a(iinventory, enumdirection).anyMatch((i) -> {
                return a(ihopper, iinventory, i, enumdirection);
            });
        } else {
            Iterator iterator = b(world, ihopper).iterator();

            EntityItem entityitem;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                entityitem = (EntityItem) iterator.next();
            } while (!a((IInventory) ihopper, entityitem));

            return true;
        }
    }

    private static boolean a(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
        ItemStack itemstack = iinventory.getItem(i);

        if (!itemstack.isEmpty() && b(iinventory, itemstack, i, enumdirection)) {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            ItemStack itemstack2 = addItem(iinventory, ihopper, iinventory.splitStack(i, 1), (EnumDirection) null);

            if (itemstack2.isEmpty()) {
                iinventory.update();
                return true;
            }

            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean a(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;
        ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
        ItemStack itemstack1 = addItem((IInventory) null, iinventory, itemstack, (EnumDirection) null);

        if (itemstack1.isEmpty()) {
            flag = true;
            entityitem.die();
        } else {
            entityitem.setItemStack(itemstack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable IInventory iinventory, IInventory iinventory1, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        if (iinventory1 instanceof IWorldInventory && enumdirection != null) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory1;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length && !itemstack.isEmpty(); ++i) {
                itemstack = a(iinventory, iinventory1, itemstack, aint[i], enumdirection);
            }
        } else {
            int j = iinventory1.getSize();

            for (int k = 0; k < j && !itemstack.isEmpty(); ++k) {
                itemstack = a(iinventory, iinventory1, itemstack, k, enumdirection);
            }
        }

        return itemstack;
    }

    private static boolean a(IInventory iinventory, ItemStack itemstack, int i, @Nullable EnumDirection enumdirection) {
        return !iinventory.b(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection);
    }

    private static boolean b(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
    }

    private static ItemStack a(@Nullable IInventory iinventory, IInventory iinventory1, ItemStack itemstack, int i, @Nullable EnumDirection enumdirection) {
        ItemStack itemstack1 = iinventory1.getItem(i);

        if (a(iinventory1, itemstack, i, enumdirection)) {
            boolean flag = false;
            boolean flag1 = iinventory1.isEmpty();

            if (itemstack1.isEmpty()) {
                iinventory1.setItem(i, itemstack);
                itemstack = ItemStack.EMPTY;
                flag = true;
            } else if (a(itemstack1, itemstack)) {
                int j = itemstack.getMaxStackSize() - itemstack1.getCount();
                int k = Math.min(itemstack.getCount(), j);

                itemstack.subtract(k);
                itemstack1.add(k);
                flag = k > 0;
            }

            if (flag) {
                if (flag1 && iinventory1 instanceof TileEntityHopper) {
                    TileEntityHopper tileentityhopper = (TileEntityHopper) iinventory1;

                    if (!tileentityhopper.s()) {
                        byte b0 = 0;

                        if (iinventory instanceof TileEntityHopper) {
                            TileEntityHopper tileentityhopper1 = (TileEntityHopper) iinventory;

                            if (tileentityhopper.tickedGameTime >= tileentityhopper1.tickedGameTime) {
                                b0 = 1;
                            }
                        }

                        tileentityhopper.setCooldown(8 - b0);
                    }
                }

                iinventory1.update();
            }
        }

        return itemstack;
    }

    @Nullable
    private static IInventory b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockHopper.FACING);

        return a(world, blockposition.shift(enumdirection));
    }

    @Nullable
    private static IInventory c(World world, IHopper ihopper) {
        return a(world, ihopper.x(), ihopper.z() + 1.0D, ihopper.A());
    }

    public static List<EntityItem> b(World world, IHopper ihopper) {
        return (List) ihopper.K_().toList().stream().flatMap((axisalignedbb) -> {
            return world.a(EntityItem.class, axisalignedbb.d(ihopper.x() - 0.5D, ihopper.z() - 0.5D, ihopper.A() - 0.5D), IEntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    @Nullable
    public static IInventory a(World world, BlockPosition blockposition) {
        return a(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D);
    }

    @Nullable
    private static IInventory a(World world, double d0, double d1, double d2) {
        Object object = null;
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        IBlockData iblockdata = world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block instanceof IInventoryHolder) {
            object = ((IInventoryHolder) block).a(iblockdata, world, blockposition);
        } else if (iblockdata.isTileEntity()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                object = (IInventory) tileentity;
                if (object instanceof TileEntityChest && block instanceof BlockChest) {
                    object = BlockChest.getInventory((BlockChest) block, iblockdata, world, blockposition, true);
                }
            }
        }

        if (object == null) {
            List<Entity> list = world.getEntities((Entity) null, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.CONTAINER_ENTITY_SELECTOR);

            if (!list.isEmpty()) {
                object = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return (IInventory) object;
    }

    private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.a(itemstack1.getItem()) ? false : (itemstack.getDamage() != itemstack1.getDamage() ? false : (itemstack.getCount() > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1)));
    }

    @Override
    public double x() {
        return (double) this.worldPosition.getX() + 0.5D;
    }

    @Override
    public double z() {
        return (double) this.worldPosition.getY() + 0.5D;
    }

    @Override
    public double A() {
        return (double) this.worldPosition.getZ() + 0.5D;
    }

    private void setCooldown(int i) {
        this.cooldownTime = i;
    }

    private boolean j() {
        return this.cooldownTime > 0;
    }

    private boolean s() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> f() {
        return this.items;
    }

    @Override
    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity, TileEntityHopper tileentityhopper) {
        if (entity instanceof EntityItem && VoxelShapes.c(VoxelShapes.a(entity.getBoundingBox().d((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()))), tileentityhopper.K_(), OperatorBoolean.AND)) {
            a(world, blockposition, iblockdata, tileentityhopper, () -> {
                return a((IInventory) tileentityhopper, (EntityItem) entity);
            });
        }

    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerHopper(i, playerinventory, this);
    }
}
