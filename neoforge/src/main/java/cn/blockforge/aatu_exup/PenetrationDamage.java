package cn.blockforge.aatu_exup;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** Shared tagged damage source used for penetration compatibility hits. */
public final class PenetrationDamage {
    public static final ResourceKey<DamageType> TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(AaTu_EXUp_Mod.MOD_ID, "penetration"));
    // A hit may carry an optional multiplier supplied by an independent mod.
    // DamageSource keys are short-lived; weak keys avoid retaining interrupted hits.
    private static final Map<DamageSource, Double> DAMAGE_MULTIPLIERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private PenetrationDamage() {
    }

    public static boolean isPenetration(DamageSource source) {
        return source.is(TYPE);
    }

    public static void setDamageMultiplier(DamageSource source, double multiplier) {
        if (source != null && Double.isFinite(multiplier) && multiplier > 1.0D) {
            DAMAGE_MULTIPLIERS.put(source, multiplier);
        }
    }

    public static double damageMultiplier(DamageSource source) {
        return DAMAGE_MULTIPLIERS.getOrDefault(source, 1.0D);
    }

    public static void clearDamageMultiplier(DamageSource source) {
        DAMAGE_MULTIPLIERS.remove(source);
    }

    public static DamageSource source(LivingEntity target, Player attacker) {
        return new DamageSource(
                target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(TYPE),
                attacker);
    }
}
