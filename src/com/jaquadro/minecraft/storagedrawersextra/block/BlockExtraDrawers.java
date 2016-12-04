package com.jaquadro.minecraft.storagedrawersextra.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawersextra.core.ModCreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import java.util.List;

public class BlockExtraDrawers extends BlockStandardDrawers
{
    public static final IUnlistedProperty<EnumVariant> VARIANT = new Properties.PropertyAdapter<EnumVariant>(PropertyEnum.create("variant", EnumVariant.class));

    public BlockExtraDrawers (String blockName) {
        super(blockName);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = super.getMainDrop(world, pos, state);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        IBlockState extended = getExtendedState(state, world, pos);
        if (extended instanceof IExtendedBlockState) {
            EnumVariant variant = ((IExtendedBlockState)extended).getValue(VARIANT);
            data.setString("material", variant.getName());
        }

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (EnumVariant material : EnumVariant.values()) {
                if (material == EnumVariant.DEFAULT)
                    continue;

                ItemStack stack = new ItemStack(item, 1, type.getMetadata());

                NBTTagCompound data = new NBTTagCompound();
                data.setString("material", material.getResource().toString());
                stack.setTagCompound(data);

                list.add(stack);
            }
        }
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)super.getExtendedState(state, world, pos))
            .withProperty(VARIANT, translateMaterial(tile.getMaterialOrDefault()));
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, FACING }, new IUnlistedProperty[] { VARIANT, STATE_MODEL });
    }

    private EnumVariant translateMaterial (String materal) {
        for (EnumVariant type : EnumVariant.values()) {
            if (materal.equals(type.getResource().toString()))
                return type;
        }

        return EnumVariant.DEFAULT;
    }
}
