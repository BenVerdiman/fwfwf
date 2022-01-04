package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class RecipeCampfire extends RecipeCooking {

    public RecipeCampfire(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i) {
        super(Recipes.CAMPFIRE_COOKING, minecraftkey, s, recipeitemstack, itemstack, f, i);
    }

    @Override
    public ItemStack h() {
        return new ItemStack(Blocks.CAMPFIRE);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
    }
}
