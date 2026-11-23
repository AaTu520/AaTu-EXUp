package cn.blockforge.aatu_exup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AaTu_EXUp_Mod.MOD_ID)
public final class AaTu_EXUp_Mod {
    public static final String MOD_ID = "aatu_exup";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MOD_ID);
    public static final DeferredRegister RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MOD_ID);

    public static final DeferredItem<Item> ATTRIBUTE_ENHANCEMENT_BOOK = ITEMS.register(
            "attribute_enhancement_book", () -> new AttributeEnhancementBookItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> SOUL_INFUSION_BOOK = ITEMS.register(
            "soul_infusion_book", () -> new SoulInfusionBookItem(new Item.Properties().stacksTo(16)));
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.entity.ai.attributes.Attribute, net.minecraft.world.entity.ai.attributes.Attribute> PENETRATION =
            ATTRIBUTES.register("penetration", () -> new net.minecraft.world.entity.ai.attributes.RangedAttribute(
                    "attribute.name.aatu_exup.penetration", 0.0D, 0.0D, 1.0D).setSyncable(true));
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer, net.minecraft.world.item.crafting.RecipeSerializer> ATTRIBUTE_ENHANCEMENT_SERIALIZER =
            RECIPE_SERIALIZERS.register("attribute_enhancement", AttributeEnhancementRecipe.Serializer::new);
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer, net.minecraft.world.item.crafting.RecipeSerializer> SOUL_INFUSION_SERIALIZER =
            RECIPE_SERIALIZERS.register("soul_infusion", SoulInfusionRecipe.Serializer::new);

    public AaTu_EXUp_Mod(IEventBus modBus, ModContainer modContainer) {
        ITEMS.register(modBus);
        ATTRIBUTES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AaTu_EXUp_ModConfig.SPEC);
        ModEvents.register(modBus);
    }
}
