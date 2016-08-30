package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.battle.BattleCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.TextScene;

public class EncounterBuilder {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	private final Array<BattleScene> battleScenes; 
	private final SaveService saveService;
	private final BitmapFont font;
	private final int sceneCode;
	private int battleCode;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(SaveService saveService, BitmapFont font, int sceneCode, int battleCode){
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
		this.saveService = saveService;
		this.font = font;
		this.sceneCode = sceneCode;
		this.battleCode = battleCode;
		sceneCounter = 0;
	}
	/* different encounter "templates" */
	protected Encounter getClassChoiceEncounter(AssetManager assetManager){	
		getTextScenes(new String[]{"Welcome to the world of tRaPG!", "You're looking mighty fine.", "Please choose your class."}, addScene(new ChoiceScene(addScene(getJobClassScenes(addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER)), font)), sceneCounter, saveService, assetManager, font)), font);
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getDefaultEncounter(){
		getTextScenes(new String[]{"You encounter a stick!", "It's actually rather sexy looking.", "There is nothing left here to do."}, addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER)), font);
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(){
		if (battleCode == -1) battleCode = new IntArray(new int[]{0,1}).random();
		getTextScenes(getScript(battleCode, 0), 
			addScene(
					new BattleScene(
							aggregateMaps(
									getTextScenes(new String[]{"You won!  You get NOTHING.", "Sad :(", "What a pity.  Go away."}, addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER)), font),
									getTextScenes(new String[]{getDefeatText(battleCode), "GAME OVER"}, addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.GAME_OVER)), font)					
							), saveService, battleCode)), font);
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(-1, -1, -1));
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
	}
	
	private OrderedMap<Integer, Scene> addScene(Scene scene){ return addScene(getSceneList(scene)); }
	// pass in one or multiple scenes that the next scene will branch into
	private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes){
		IntArray sceneCodes = new IntArray();
		for (Scene scene : scenes){
			this.scenes.add(scene);
			if (scene instanceof BattleScene) battleScenes.add((BattleScene)scene);
			if (scene instanceof EndScene) endScenes.add((EndScene)scene);
			sceneCodes.add(sceneCounter++);
		}
		return getSceneMap(sceneCodes, scenes);
	}
	
	private OrderedMap<Integer, Scene> getSceneMap(IntArray integers, Array<Scene> scenes){
		OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
		for (int ii = 0; ii < integers.size; ii++){
			sceneMap.put(integers.get(ii), scenes.get(ii));
		}
		return sceneMap;
	}
	
	private OrderedMap<Integer, Scene> getTextScenes(String[] script, OrderedMap<Integer, Scene> sceneMap, BitmapFont font){ return getTextScenes(new Array<String>(true, script, 0, script.length), sceneMap, font); }	
	// pass in a list of script lines in chronological order, this will reverse their order and add them to the stack
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, OrderedMap<Integer, Scene> sceneMap, BitmapFont font){
		script.reverse();
		for (String scriptLine: script){
			sceneMap = addScene(new TextScene(sceneMap, sceneCounter, saveService, font, scriptLine, getMutationList(new Mutation())));
		}	
		return sceneMap;
	}
	
	private OrderedMap<Integer, Scene> aggregateMaps(OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> aggregatedMap = new OrderedMap<Integer, Scene>();
		for (OrderedMap<Integer, Scene> map : sceneMaps){
			aggregatedMap.putAll(map);
		}
		return aggregatedMap;	
	}
	
	private String getDefeatText(int battleCode){
		switch(battleCode){
			case 0:
				return "You lost! You get knotty werewolf cock! (up the butt).";
			default:
				return "You lost! The harpy mounts you! (up the butt).";
		}
	}
	
	private String[] getScript(int battleCode, int scene){
		switch(battleCode){
			case 0:
				return new String[]{"You encounter a random encounter!", "It's so random. :^)", "There is nothing left here to do.", "No wait lol there's a basic werebitch, RAWR."};
			default:
				return new String[]{"You look around.  There doesn't appear to be a harpy here.", "Wait actually that harpy looks like she wants to drill you silly!"};
		}
	}
		
	private Scene getStartScene(Array<Scene> scenes, Integer sceneCode){
		// default case
		if (sceneCode == 0){
			// returns the final scene and plays in reverse order
			return scenes.get(scenes.size - 1);
		}
		for (Scene objScene: scenes){
			if (objScene.getCode() == sceneCode){
				return objScene;
			}
		}
		return null;
	}
	
	private Array<Scene> getJobClassScenes(OrderedMap<Integer, Scene> sceneMap, BitmapFont font){
		Array<Scene> jobClassScenes = new Array<Scene>();
		int tempCounter = sceneCounter;
		for (SaveManager.JobClass jobClass: SaveManager.JobClass.values()){
			jobClassScenes.add(new TextScene(sceneMap, tempCounter++, saveService, font, "You are now "+ getJobClass(jobClass) +".", getMutationList(new Mutation(saveService, SaveEnum.CLASS, jobClass))));
		}
		return jobClassScenes;
	}
	/* Helper methods that may go away with refactors*/
	private String getJobClass(SaveManager.JobClass jobClass){ return jobClass == SaveManager.JobClass.ENCHANTRESS ? "an Enchantress" : "a " + jobClass.getLabel(); }
	private Array<Scene> getSceneList(Scene... scenes){ return new Array<Scene>(true, scenes, 0, scenes.length); }	
	private Array<Mutation> getMutationList(Mutation... mutations){ return new Array<Mutation>(true, mutations, 0, mutations.length); }
}
