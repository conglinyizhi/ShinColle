package org.trp.shincolle.utility;

import net.minecraft.util.Mth;

public class CalcHelper {

    private CalcHelper() {}

    public static float[] rotateXZByAxis(float z, float x, float rad, float scale) {
        float cosD = Mth.cos(rad);
        float sinD = Mth.sin(rad);
        float[] newPos = new float[]{0.0f, 0.0f};
        newPos[0] = z * cosD + x * sinD;
        newPos[1] = x * cosD - z * sinD;
        newPos[0] = newPos[0] * scale;
        newPos[1] = newPos[1] * scale;
        return newPos;
    }

    public static boolean checkIntNotInArray(int target, int[] host) {
        if (host == null) {
            return true;
        }
        for (int i : host) {
            if (target == i) return false;
        }
        return true;
    }
}
