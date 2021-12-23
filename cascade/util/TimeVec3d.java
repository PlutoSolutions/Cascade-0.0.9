/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 */
package cascade.util;

import net.minecraft.util.math.Vec3d;

public class TimeVec3d
extends Vec3d {
    private final long time;

    public TimeVec3d(double xIn, double yIn, double zIn, long time) {
        super(xIn, yIn, zIn);
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }
}

