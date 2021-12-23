/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 */
package cascade.manager;

import cascade.features.Feature;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.Packet;

public class PacketManager
extends Feature {
    private final List<Packet<?>> noEventPackets = new ArrayList();
}

