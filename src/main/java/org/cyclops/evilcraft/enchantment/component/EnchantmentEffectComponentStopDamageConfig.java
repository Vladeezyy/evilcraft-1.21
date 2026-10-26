package org.cyclops.evilcraft.enchantment.component;

import org.cyclops.evilcraft.fabric.event.LivingDamageEvent;
import org.cyclops.evilcraft.fabric.event.BlockEvent;
import com.mojang.serialization.Codec;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.EnchantmentEffectComponentConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link EnchantmentEffectComponentStopDamage}.
 * @author rubensworks
 *
 */
public class EnchantmentEffectComponentStopDamageConfig extends EnchantmentEffectComponentConfig<Boolean> {
    public EnchantmentEffectComponentStopDamageConfig() {
        super(
                EvilCraft._instance,
                "stop_damage",
                builder -> builder
                        .persistent(Codec.BOOL)
        );
        EnchantmentEffectComponentStopDamage listener = new EnchantmentEffectComponentStopDamage();
        LivingDamageEvent.Pre.LISTENERS.register(listener::unusingEvent);
        BlockEvent.BreakEvent.addListener(listener::unusingEvent);
    }
}
