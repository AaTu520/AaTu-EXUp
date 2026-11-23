package cn.blockforge.aatu_exup.compat;

import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Makes the drop-only AaTu books searchable in JEI. */
@JeiPlugin
public final class AaTuExUpJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AaTu_EXUp_Mod.MOD_ID, "jei");
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(List.of(
                new ItemStack(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK.get()),
                new ItemStack(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK.get())));
    }
}
