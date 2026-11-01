package org.cyclops.evilcraftcompat.modcompat.jei;

/**
 * Fabric: EvilCraft counts fluids in mB, JEI in the platform's unit (81000 per bucket on Fabric).
 * @author rubensworks
 */
public class JeiFluids {

    private static long BUCKET_VOLUME = 1000;

    static void setBucketVolume(long bucketVolume) {
        BUCKET_VOLUME = bucketVolume;
    }

    public static long amount(int mB) {
        return mB * BUCKET_VOLUME / 1000;
    }
}
