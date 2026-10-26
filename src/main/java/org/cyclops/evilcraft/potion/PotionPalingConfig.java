package org.cyclops.evilcraft.potion;

import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.EffectConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link PotionPaling}.
 * @author rubensworks
 *
 */
public class PotionPalingConfig extends EffectConfig {

    public PotionPalingConfig() {
        super(
            EvilCraft._instance,
            "paling",
            eConfig -> new PotionPaling()
        );
    }

}
