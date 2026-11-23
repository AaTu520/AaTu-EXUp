package cn.blockforge.aatu_exup;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class AaTu_EXUp_Mod implements ModInitializer {
    public static final String MOD_ID = "aatu_exup";

    public static final Item ATTRIBUTE_ENHANCEMENT_BOOK = registerItem(
            "attribute_enhancement_book", new AttributeEnhancementBookItem(new Item.Properties().stacksTo(16)));
    public static final Item SOUL_INFUSION_BOOK = registerItem(
            "soul_infusion_book", new SoulInfusionBookItem(new Item.Properties().stacksTo(16)));
    public static final Holder<Attribute> PENETRATION = Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            id("penetration"),
            new RangedAttribute("attribute.name.aatu_exup.penetration", 0.0D, 0.0D, 1.0D).setSyncable(true));
    public static final RecipeSerializer<AttributeEnhancementRecipe> ATTRIBUTE_ENHANCEMENT_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, id("attribute_enhancement"), new AttributeEnhancementRecipe.Serializer());
    public static final RecipeSerializer<SoulInfusionRecipe> SOUL_INFUSION_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, id("soul_infusion"), new SoulInfusionRecipe.Serializer());

    private static Item registerItem(String path, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, id(path), item);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        AaTu_EXUp_ModConfig.load();
        ModEvents.register();
    }
}
