package org.cyclops.evilcraft.enchantment.component;

import org.cyclops.evilcraft.fabric.event.LivingDamageEvent;
import org.cyclops.evilcraft.fabric.event.BlockEvent;
import net.minecraft.util.ExtraCodecs;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.EnchantmentEffectComponentConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.enchantment.entityeffect.EnchantmentEntityEffectHealFromDamage;

/**
 * Config for {@link EnchantmentEntityEffectHealFromDamage}.
 * @author rubensworks
 *
 */
public class EnchantmentEffectComponentAmplifyDamageConfig extends EnchantmentEffectComponentConfig<Integer> {
    public EnchantmentEffectComponentAmplifyDamageConfig() {
        super(
                EvilCraft._instance,
                "amplify_damage",
                builder -> builder
                        .persistent(ExtraCodecs.POSITIVE_INT)
        );
        EnchantmentEffectComponentAmplifyDamage listener = new EnchantmentEffectComponentAmplifyDamage();
        LivingDamageEvent.Post.LISTENERS.register(listener::livingDamage);
        BlockEvent.BreakEvent.addListener(listener::breakingEvent);
    }
}
