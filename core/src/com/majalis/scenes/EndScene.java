package com.majalis.scenes;

import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

/*
 * Represents a scene that concludes an encounter
 */
public class EndScene extends Scene{

	private final Type type;
	private final SaveService saveService;
	private final SaveManager.GameContext context;
	public EndScene(Type type, SaveService saveService, SaveManager.GameContext context) {
		super(null, -1);
		this.type = type;
		this.saveService = saveService;
		this.context = context;
	}

	public Type getType(){
		return type;
	}
	
	@Override
	public void setActive() {
		isActive = true;
		saveService.saveDataValue(SaveEnum.CONTEXT, context);
		if (type == Type.ENCOUNTER_OVER || type == Type.GAME_OVER){
			saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, null);
		}
		saveService.saveDataValue(SaveEnum.SCENE_CODE, 0);
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	public enum Type {
		ENCOUNTER_OVER,
		GAME_OVER
	}
}
