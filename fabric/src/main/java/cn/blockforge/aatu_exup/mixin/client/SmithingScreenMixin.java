package cn.blockforge.aatu_exup.mixin.client;

import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
abstract class SmithingScreenMixin {
    @Inject(method = "renderOnboardingTooltips", at = @At("HEAD"), cancellable = true)
    private void aatu_exup$hideVanillaRecipeErrorTooltip(
            GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof SmithingMenu menu)) {
            return;
        }

        ItemStack template = menu.getSlot(0).getItem();
        if (template.is(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK)
                || template.is(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK)) {
            callbackInfo.cancel();
        }
    }
}
