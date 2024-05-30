package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketUtils {
    public static List<Float> readFloatList(PacketBuffer buf, int count) {
        if (count < 0) throw new IllegalArgumentException("Count should be greater than zero");

        List<Float> list = new ArrayList<>();
        for (int i = 0; i < count; i++)
            list.add(buf.readFloat());

        return list;
    }

    public static void writeFloatList(PacketBuffer buf, List<Float> list) {
        for (Float f : list)
            buf.writeFloat(f);
    }

    public static void writeVec3(PacketBuffer buf, Vector3d vec) {
        writeFloatList(buf, Arrays.asList((float) vec.x, (float) vec.y, (float) vec.z));
    }

    public static Vector3d readVec3(PacketBuffer buf) {
        List<Float> floatList = readFloatList(buf, 3);
        return new Vector3d(floatList.get(0), floatList.get(1), floatList.get(2));
    }
}
