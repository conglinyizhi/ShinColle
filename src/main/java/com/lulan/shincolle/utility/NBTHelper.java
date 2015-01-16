package com.lulan.shincolle.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Pahimar's nbt helper 加上部分註解說明
*除了原本NBTTagCompound提供的取值跟設值方法外, 增加了檢查tag是否為null, 若為null則給預設值
*stackTagCompound -> 為 整個nbt data集合 只能用於不可堆疊的物品
*一旦一個物品加上了nbt data 該物品就不能跟其他同名物品堆疊了 (除非是用指令直接叫出一疊具有相同nbt data的物品)
* 
**/
public class NBTHelper {
	
	//nbt data init
    private static void initNBTTagCompound(ItemStack itemStack) {
        if (itemStack.stackTagCompound == null) {	//check nbt data
            itemStack.setTagCompound(new NBTTagCompound());
        }
    }
	
	//search tag
	public static boolean hasTag(ItemStack itemStack, String keyName) {
        return itemStack != null && itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey(keyName);
    }

	//delete tag
    public static void removeTag(ItemStack itemStack, String keyName) {
        if (itemStack.stackTagCompound != null) {
            itemStack.stackTagCompound.removeTag(keyName);
        }
    }
    
    //add long
    public static void setLong(ItemStack itemStack, String keyName, long keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setLong(keyName, keyValue);
    }
    
    //get long
    public static long getLong(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setLong(itemStack, keyName, 0);
        }

        return itemStack.stackTagCompound.getLong(keyName);
    }

    public static void setString(ItemStack itemStack, String keyName, String keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setString(keyName, keyValue);
    }
    
    public static String getString(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setString(itemStack, keyName, "");
        }

        return itemStack.stackTagCompound.getString(keyName);
    }

    public static void setBoolean(ItemStack itemStack, String keyName, boolean keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setBoolean(keyName, keyValue);
    }

    public static boolean getBoolean(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setBoolean(itemStack, keyName, false);
        }

        return itemStack.stackTagCompound.getBoolean(keyName);
    }

    public static void setByte(ItemStack itemStack, String keyName, byte keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setByte(keyName, keyValue);
    }
    
    public static byte getByte(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setByte(itemStack, keyName, (byte) 0);
        }

        return itemStack.stackTagCompound.getByte(keyName);
    }

    public static void setShort(ItemStack itemStack, String keyName, short keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setShort(keyName, keyValue);
    }

    public static short getShort(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setShort(itemStack, keyName, (short) 0);	
        }

        return itemStack.stackTagCompound.getShort(keyName);
    }
    
    public static void setInteger(ItemStack itemStack, String keyName, int keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setInteger(keyName, keyValue);
    }

    public static int getInteger(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
        	setInteger(itemStack, keyName, 0);
        }

        return itemStack.stackTagCompound.getInteger(keyName);
    }

    public static void setFloat(ItemStack itemStack, String keyName, float keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setFloat(keyName, keyValue);
    }

    public static float getFloat(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setFloat(itemStack, keyName, 0);
        }

        return itemStack.stackTagCompound.getFloat(keyName);
    }
    
    public static void setDouble(ItemStack itemStack, String keyName, double keyValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setDouble(keyName, keyValue);
    }
    
    public static double getDouble(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);
        if (!itemStack.stackTagCompound.hasKey(keyName)) {
            setDouble(itemStack, keyName, 0);
        }

        return itemStack.stackTagCompound.getDouble(keyName);
    }
    

}
