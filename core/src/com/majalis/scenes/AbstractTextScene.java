package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public abstract class AbstractTextScene extends Scene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
	}
	protected final SaveService saveService;
	protected final Label display;
	protected final Label statusResults;
	protected final Image characterPortrait;
	protected final Image masculinityIcon;
	protected final Image fullnessIcon;
	
	protected AbstractTextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, BitmapFont font, PlayerCharacter character, SaveService saveService, Background background) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		display = addLabel("", skin, font, Color.BLACK, 430, 330);
		display.setWidth(1125);
		statusResults = addLabel("", skin, font, Color.BLACK, 1430, 950);
		statusResults.setWidth(400);
		Label skipText = addLabel("Press CTRL to skip", skin, font, Color.BLACK, 105, 180);
		skipText.setWidth(240);
		Texture portrait = assetManager.get(character.getPortraitPath(), Texture.class);
		characterPortrait = addImage(portrait, null, 105, 800, portrait.getWidth() / (portrait.getHeight() / 200f), 200);
		characterPortrait.addAction(Actions.hide());
		Texture icon = assetManager.get(character.getMasculinityPath(), Texture.class);
		masculinityIcon = addImage(icon, null, 105, 705, icon.getWidth() / (icon.getHeight() / 100f), 100);
		masculinityIcon.addAction(Actions.hide());
		Texture fullness = assetManager.get(character.getCumInflationPath(), Texture.class);
		fullnessIcon = addImage(icon, null, 42, 755, fullness.getWidth() / (fullness.getHeight() / 100f), 100);
		fullnessIcon.addAction(Actions.hide());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
			nextScene();
		}
	}
	
	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		this.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				nextScene();
			}
		});
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	protected abstract void nextScene();
}
