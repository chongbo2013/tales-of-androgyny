package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.battle.BattleCode;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveService;
import com.majalis.scenes.ShopScene.Shop;
/*
 * Retrieves encounters from internal files given an encounterId.  Need to create some kind of encounter builder helper class.
 */
public class EncounterFactory {
	
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;

	public EncounterFactory(EncounterReader reader, AssetManager assetManager, SaveManager saveManager) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	public Encounter getEncounter(EncounterCode encounterCode, BitmapFont font, BitmapFont smallFont) {
		Integer sceneCode = loadService.loadDataValue(SaveEnum.SCENE_CODE, Integer.class);
		BattleCode battle = loadService.loadDataValue(SaveEnum.BATTLE_CODE, BattleCode.class);
		GameContext context = loadService.loadDataValue(SaveEnum.RETURN_CONTEXT, GameContext.class);
		int battleCode = -1;
		if (battle != null) battleCode = battle.battleCode;
		@SuppressWarnings("unchecked")
		EncounterBuilder builder = new EncounterBuilder(reader, assetManager, saveService, font, smallFont, sceneCode, battleCode, (ObjectMap<String, Shop>)loadService.loadDataValue(SaveEnum.SHOP, Shop.class), (PlayerCharacter) loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class), context);
		switch (encounterCode) {
			case LEVEL_UP: return builder.getLevelUpEncounter((GameMode) loadService.loadDataValue(SaveEnum.MODE, Shop.class) == GameMode.STORY);
			case INITIAL: return builder.getClassChoiceEncounter();
			case DEFAULT: return builder.getDefaultEncounter();
			default: return builder.getRandomEncounter(encounterCode);
		}
	}
}
