package cn.blockforge.aatu_exup;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ModEvents {
    private ModEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(ModEvents::onLivingDeath);
    }

    private static void onLivingDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        // Matches Minecraft's killed_by_player loot condition, including player-owned attacks.
        if (!(entity.getKillCredit() instanceof Player)) {
            return;
        }

        if (entity.getMaxHealth() < AaTu_EXUp_ModConfig.DROP_MINIMUM_HEALTH.getAsDouble()) {
            return;
        }

        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.ATTRIBUTE_BOOK_DROP_CHANCE.getAsDouble()) {
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK)));
        }
        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.SOUL_INFUSION_BOOK_DROP_CHANCE.getAsDouble()) {
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK)));
        }
    }
}
