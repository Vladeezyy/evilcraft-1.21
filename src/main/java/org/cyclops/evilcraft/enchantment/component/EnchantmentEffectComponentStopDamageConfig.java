package org.cyclops.evilcraft.enchantment.component;

import org.cyclops.evilcraft.fabric.event.LivingDamageEvent;
import org.cyclops.evilcraft.fabric.event.BlockEvent;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.NeoForge;
import org.cyclops.cyclopscore.config.extendedconfig.EnchantmentEffectComponentConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link EnchantmentEffectComponentStopDamage}.
 * @author rubensworks
 *
 */
public class EnchantmentEffectComponentStopDamageConfig extends EnchantmentEffectComponentConfigCommon<Boolean, IModBase> {
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
