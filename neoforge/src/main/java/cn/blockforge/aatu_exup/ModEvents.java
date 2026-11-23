package cn.blockforge.aatu_exup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public final class ModEvents {
    private static final String ENHANCEMENT_TOTAL = "tooltip.aatu_exup.enhancement_total";

    /**
     * Kept as a name so Legendary Monsters remains an optional dependency.
     * All of its capped boss entities inherit from this class.
     */
    private static final String LEGENDARY_MONSTERS_BOSS_CLASS =
            "net.miauczel.legendary_monsters.entity.AnimatedMonster.OriginClasses.IAnimatedBoss";

    private ModEvents() {
    }

    public static void register(net.neoforged.bus.api.IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(ModEvents::onItemAttributes);
        NeoForge.EVENT_BUS.addListener(ModEvents::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(ModEvents::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(ModEvents::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(ModEvents::onLivingDamagePost);
        modBus.addListener(ModEvents::onEntityAttributes);
    }

    private static void onEntityAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AaTu_EXUp_Mod.PENETRATION, 0.0D);
    }

    private static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (!AttributeEnhancement.isUpgradeable(event.getItemStack())) {
            return;
        }

        AttributeEnhancement.apply(event);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!AttributeEnhancement.isUpgradeable(stack)) {
            return;
        }

        var enhancements = AttributeData.getEnhancements(stack);
        List<Component> tooltip = event.getToolTip();
        int headerIndex = enhancementHeaderIndex(tooltip);
        Component soul = Component.translatable("tooltip.aatu_exup.soul", AttributeData.getSoul(stack))
                .withStyle(ChatFormatting.GOLD);
        if (headerIndex >= 0) {
            tooltip.add(headerIndex, soul);
        } else {
            tooltip.add(soul);
        }
        if (enhancements.isEmpty()) {
            return;
        }

        headerIndex = enhancementHeaderIndex(tooltip);
        if (headerIndex < 0) {
            tooltip.add(Component.translatable(ENHANCEMENT_TOTAL).withStyle(ChatFormatting.GOLD));
            headerIndex = tooltip.size() - 1;
        }
        for (AttributeData.Enhancement enhancement : enhancements) {
            tooltip.add(++headerIndex, Component.translatable(
                    "tooltip.aatu_exup.attribute_total",
                    AttributeEnhancement.formatValue(enhancement.type(), enhancement.value()),
                    Component.translatable(AttributeEnhancement.attributeNameKey(enhancement.type())))
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    private static int enhancementHeaderIndex(List<Component> tooltip) {
        for (int index = 0; index < tooltip.size(); index++) {
            if (tooltip.get(index).getContents() instanceof TranslatableContents contents
                    && ENHANCEMENT_TOTAL.equals(contents.getKey())) {
                return index;
            }
        }
        return -1;
    }

    private static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !(entity.getKillCredit() instanceof Player)
                || entity.getMaxHealth() < AaTu_EXUp_ModConfig.DROP_MINIMUM_HEALTH.getAsDouble()) {
            return;
        }

        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.ATTRIBUTE_BOOK_DROP_CHANCE.getAsDouble()) {
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK.get())));
        }
        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.SOUL_INFUSION_BOOK_DROP_CHANCE.getAsDouble()) {
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(AaTu_EXUp_Mod.SOUL_INFUSION_BOOK.get())));
        }
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        if (PenetrationDamage.isPenetration(source) || isLegendaryMonstersBoss(event.getEntity())) {
            return;
        }
        Player player = playerAttacker(source);
        if (player == null) {
            return;
        }

        float penetrationDamage = additionalPenetrationDamage(event.getEntity(), player, source);
        if (penetrationDamage > 0.0F) {
            event.setNewDamage(event.getNewDamage() + penetrationDamage);
        }
    }

    /**
     * Legendary Monsters applies its boss damage cap before LivingDamageEvent.Pre.
     * Once the regular hit has completed, send a separate tagged true-damage hit so
     * only the penetration portion bypasses that cap.
     */
    private static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (PenetrationDamage.isPenetration(source) || !isLegendaryMonstersBoss(target)) {
            return;
        }

        Player player = playerAttacker(source);
        if (player == null) {
            return;
        }
        float penetrationDamage = additionalPenetrationDamage(target, player, source);
        if (penetrationDamage > 0.0F) {
            target.hurt(PenetrationDamage.source(target, player), penetrationDamage);
        }
    }

    private static float additionalPenetrationDamage(LivingEntity target, Player player, DamageSource source) {
        if (player.level().isClientSide()
                || (target instanceof Player && !AaTu_EXUp_ModConfig.PENETRATION_AFFECTS_PLAYERS.get())) {
            return 0.0F;
        }

        double penetration = player.getAttributeValue(AaTu_EXUp_Mod.PENETRATION);
        if (penetration <= 0.0D || player.getRandom().nextDouble() >= penetration) {
            return 0.0F;
        }

        double penetrationDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE)
                * AaTu_EXUp_ModConfig.PENETRATION_DAMAGE_RATIO.getAsDouble()
                * PenetrationDamage.damageMultiplier(source);
        if (!Double.isFinite(penetrationDamage) || penetrationDamage <= 0.0D) {
            return 0.0F;
        }

        return (float) Math.min(penetrationDamage, Float.MAX_VALUE);
    }

    private static Player playerAttacker(DamageSource source) {
        return source.getEntity() instanceof Player player ? player : null;
    }

    private static boolean isLegendaryMonstersBoss(LivingEntity entity) {
        for (Class<?> type = entity.getClass(); type != null; type = type.getSuperclass()) {
            if (LEGENDARY_MONSTERS_BOSS_CLASS.equals(type.getName())) {
                return true;
            }
        }
        return false;
    }
}
