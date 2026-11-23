package cn.blockforge.aatu_exup;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public final class SoulInfusionRecipe implements SmithingRecipe {
    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().is(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK)
                && input.addition().is(AaTu_EXUp_ModConfig.soulInfusionCostItem())
                && AttributeEnhancement.isSoulInfusable(input.base())
                && AttributeData.canInjectSoul(input.base());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = input.base().copy();
        int amount = AaTu_EXUp_ModConfig.randomSoulAmount(
                net.minecraft.util.RandomSource.create(seed(input.base())));
        AttributeData.addSoul(result, amount);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return AttributeEnhancement.isSoulInfusable(stack) && AttributeData.canInjectSoul(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(AaTu_EXUp_ModConfig.soulInfusionCostItem());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK),
                Ingredient.of(AaTu_EXUp_ModConfig.soulInfusionCostItem()));
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AaTu_EXUp_Mod.SOUL_INFUSION_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    private static long seed(ItemStack stack) {
        long seed = Integer.toUnsignedLong(ItemStack.hashItemAndComponents(stack));
        seed ^= 0x9E3779B97F4A7C15L;
        seed = (seed ^ (seed >>> 30)) * 0xBF58476D1CE4E5B9L;
        seed = (seed ^ (seed >>> 27)) * 0x94D049BB133111EBL;
        return seed ^ (seed >>> 31);
    }

    public static final class Serializer implements RecipeSerializer<SoulInfusionRecipe> {
        private static final MapCodec<SoulInfusionRecipe> CODEC = MapCodec.unit(SoulInfusionRecipe::new);
        private static final StreamCodec<RegistryFriendlyByteBuf, SoulInfusionRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public MapCodec<SoulInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void encode(RegistryFriendlyByteBuf buffer, SoulInfusionRecipe recipe) {
        }

        private static SoulInfusionRecipe decode(RegistryFriendlyByteBuf buffer) {
            return new SoulInfusionRecipe();
        }
    }
}
