package com.lulan.shincolle.init;

import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;

import com.lulan.shincolle.ShinColle;
import com.lulan.shincolle.entity.EntityDestroyerI;
import com.lulan.shincolle.reference.Reference;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModEntity {
	

	public static void init() {
		createEntity(EntityDestroyerI.class, Reference.MOD_ID_LOW+":EntityDestroyerI", 0xEC4545, 0x001EFF);
		
	}
	
	//登錄生物方法
	//參數: 該生物class, 生物名稱, 怪物蛋背景色, 怪物蛋斑點色
	public static void createEntity(Class entityClass, String entityName, int backColor, int spotColor){
		int entityId = EntityRegistry.findGlobalUniqueEntityId();	//找一個空的生物id來用		
		
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId);
		//登錄參數: 生物class, 生物名稱, 生物id, mod副本, 追蹤更新距離, 更新時間間隔, 是否發送速度封包
		EntityRegistry.registerModEntity(entityClass, entityName, entityId, ShinColle.instance, 128, 1, true);
		//登錄怪物生物蛋: 生物id, 生成蛋資訊(生物id,背景色,斑點色)
		EntityList.entityEggs.put(Integer.valueOf(entityId), new EntityList.EntityEggInfo(entityId, backColor, spotColor));

	}
	

}
