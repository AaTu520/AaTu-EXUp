package cn.blockforge.aatu_exup.mixin.compat;

import cn.blockforge.aatu_exup.compat.ConfiguredConfigCompatibility;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.neoforged.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.mrcrayfish.configured.impl.neoforge.NeoForgeConfig", remap = false)
abstract class ConfiguredNeoForgeConfigMixin {
    @Shadow
    @Final
    protected ModConfig config;

    @Redirect(
            method = "update",
            at = @At(value = "INVOKE", target = "Lcom/electronwill/nightconfig/core/CommentedConfig;putAll(Lcom/electronwill/nightconfig/core/UnmodifiableConfig;)V"),
            require = 0)
    private void aatu_exup$preserveUnchangedConfigValues(CommentedConfig original, UnmodifiableConfig changes) {
        ConfiguredConfigCompatibility.applyChanges(this.config.getModId(), original, changes);
    }
}
