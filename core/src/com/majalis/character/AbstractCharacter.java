package com.majalis.character;

import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.Attack.Status;
import com.majalis.character.Item.ItemEffect;
import com.majalis.character.Item.Weapon;
import com.majalis.character.PlayerCharacter.Bootyliciousness;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.Bonus;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class AbstractCharacter extends Actor {
	
	// some of these ints will be enumerators or objects in time
	/* permanent stats */
	protected String label;
	protected PronounSet pronouns;
	protected boolean secondPerson;
	
	/* rigid stats */
	protected JobClass jobClass;
	protected EnemyEnum enemyType;
	protected int level;
	protected int experience;
	protected int baseStrength;
	protected int baseEndurance;
	protected int baseAgility;
	protected int basePerception;
	protected int baseMagic;
	protected int baseCharisma;
	protected int baseLuck; // 0 for most classes, can go negative
	
	protected int baseDefense;
	protected int baseEvade;
	protected int baseBlock;
	protected int baseParry;
	protected int baseCounter;
	
	protected IntArray healthTiers; // total these to receive maxHealth, maybe cache it when this changes
	protected IntArray staminaTiers; // total these to receive maxStamina, maybe cache it when this changes
	protected IntArray manaTiers; // total these to receive maxMana, maybe cache it when this changes
	
	/* morphic stats */
	protected int currentHealth;
	protected int currentStamina;
	protected int currentMana; // mana might be replaced with spell slots that get refreshed
	
	protected int stability;
	protected int focus;
	protected int fortune;
	
	protected int lust; 
	protected int struggle;
	protected int knotInflate;
	
	protected Weapon weapon;
	// public Shield shield;
	// public Armor armor;
	// public Gauntlet gauntlet;
	// public Sabaton sabaton;
	// public Accessory firstAccessory;
	// public Accessory secondAccessory;
	
	protected Weapon disarmedWeapon;
	
	// public Hole hole;  // bowels contents, tightness, number of copulations, number of creampies, etc. 
	// public Mouth mouth; 
	protected PhallusType phallus;	
	
	protected int buttful;
	protected int mouthful;
	
	protected Bootyliciousness bootyliciousness;
	
	protected Stance stance;
	protected Stance oldStance;
	public ObjectMap<String, Integer> statuses; // status effects will be represented by a map of Enum to Status object
	
	protected Array<Item> inventory;
	protected int food;

	/* Constructors */
	protected AbstractCharacter() {}
	protected AbstractCharacter(boolean defaultValues) {
		if (defaultValues) {
			secondPerson = false;
			level = 1;
			experience = 0;
			baseStrength = baseEndurance = baseAgility = basePerception = baseMagic = baseCharisma = 3;
			baseDefense = 4;
			baseLuck = 0;
			baseEvade = 0;
			baseBlock = 0;
			baseParry = 0;
			baseCounter = 0;
			healthTiers = getDefaultHealthTiers();
			staminaTiers = getDefaultStaminaTiers();
			manaTiers = getDefaultManaTiers();
			currentHealth = getMaxHealth();
			currentStamina = getMaxStamina();
			currentMana = getMaxMana();
			stability = focus = fortune = 10;
			stance = Stance.BALANCED;
			struggle = 0;
			phallus = PhallusType.NORMAL;
			statuses = new ObjectMap<String, Integer>();
		}
	}
	
	protected abstract Technique getTechnique(AbstractCharacter target);
	
	protected IntArray getDefaultHealthTiers() { return new IntArray(new int[]{10, 10, 10, 10}); }
	protected IntArray getDefaultStaminaTiers() { return new IntArray(new int[]{5, 5, 5, 5}); }
	protected IntArray getDefaultManaTiers() { return new IntArray(new int[]{0}); }
	
	public int getMaxHealth() { return getMax(healthTiers); }
	public int getMaxStamina() { return getMax(staminaTiers); }
	public int getMaxMana() { return getMax(manaTiers); }
	public int getMaxStability() { return getAgility() * 3 + 9; }
	protected int getMax(IntArray tiers) {
		int max = 0;
		for (int ii = 0; ii < tiers.size; ii++) {
			max += tiers.get(ii);
		}
		return max;
	}
	
	public Stance getStance() { return stance; }
	
	public void setStance(Stance stance) { this.stance = stance; }
	
	public int getCurrentHealth() { return currentHealth; }
	
	public int getCurrentStamina() { return currentStamina; }
	
	public int getCurrentMana() { return currentMana; }
		
	public float getHealthPercent() { return currentHealth / (getMaxHealth() * 1.0f); }
	
	public float getStaminaPercent() { return currentStamina / (getMaxStamina() * 1.0f); }
	
	public float getBalancePercent() { return stability / (getMaxStability() * 1.0f); }
	
	public float getManaPercent() { return currentMana / (getMaxMana() * 1.0f); }
	
	public String getHealthDisplay() { 
		switch (getHealthDegradation()) {
			case 0: return AssetEnum.HEALTH_ICON_0.getPath();
			case 1: return AssetEnum.HEALTH_ICON_1.getPath();
			case 2: return AssetEnum.HEALTH_ICON_2.getPath();
			case 3: return AssetEnum.HEALTH_ICON_3.getPath();
			case 4: return AssetEnum.HEALTH_ICON_3.getPath();
			case 5: return AssetEnum.HEALTH_ICON_3.getPath();
		}
		return null;
	}
	
	public String getStaminaDisplay() { 
		switch (getStaminaDegradation()) {
			case 0: return AssetEnum.STAMINA_ICON_0.getPath();
			case 1: return AssetEnum.STAMINA_ICON_1.getPath();
			case 2: return AssetEnum.STAMINA_ICON_2.getPath();
			case 3: return AssetEnum.STAMINA_ICON_3.getPath();
			case 4: return AssetEnum.STAMINA_ICON_3.getPath();
		}
		return null;
	}
	
	public String getBalanceDisplay() { 
		return stability > 10 ?  AssetEnum.BALANCE_ICON_0.getPath() : stability > 5 ? AssetEnum.BALANCE_ICON_1.getPath() : AssetEnum.BALANCE_ICON_2.getPath();
	}
	
	public String getManaDisplay() {  
		switch (4 - (int)(getManaPercent() * 100)/ 25) {
			case 0: return AssetEnum.MANA_ICON_0.getPath();
			case 1: return AssetEnum.MANA_ICON_1.getPath();
			case 2: return AssetEnum.MANA_ICON_2.getPath();
			case 3: return AssetEnum.MANA_ICON_3.getPath();
			case 4: return AssetEnum.MANA_ICON_3.getPath();
		}
		return null;
	}
	
	public int getStability() { return stability; }
	
	public int getLust() { return lust; }
	
	public String modHealth(int healthMod) { 
		int healthChange = this.currentHealth;
		this.currentHealth += healthMod; 
		if (currentHealth > getMaxHealth()) 
			currentHealth = getMaxHealth();  
		healthChange = this.currentHealth - healthChange;
		return healthChange > 0 ? "Gained " + healthChange + " health!" : "You take " + -healthChange + " damage!"; 
	}
	
	protected int getStaminaRegen() { return Math.max(getEndurance()/2, 0); }
	
	protected int getStabilityRegen() { return getAgility()/2; }
	
	protected String getLabel() { return label; }
	
	protected Boolean getSecondPerson() { return secondPerson; }
	
	protected void setHealthToMax() { currentHealth = getMaxHealth(); }
	
	protected void setStaminaToMax() { currentStamina = getMaxStamina(); }
	
	protected void modStamina(int staminaMod) { this.currentStamina += staminaMod; if (currentStamina > getMaxStamina()) currentStamina = getMaxStamina(); }
	
	protected void setStabilityToMax() { stability = getMaxStability(); }
	
	protected void setStabilityToMin() { stability = -5; }
	
	protected void modStability(int stabilityMod) { this.stability += stabilityMod; if (stability > getMaxStability()) stability = getMaxStability(); }
	
	protected void setManaToMax() { currentMana = getMaxMana(); }
	
	protected void modMana(int manaMod) { this.currentMana += manaMod; if (currentMana > getMaxMana()) currentMana = getMaxMana(); if (currentMana < 0) currentMana = 0; }

	protected int getStrength() { return Math.max((baseStrength + getStrengthBuff()) - (getHealthDegradation() + getStaminaDegradation())/2, 0); }
	
	protected int getStrengthBuff() { return statuses.get(StatusType.STRENGTH_BUFF.toString(), 0); }
	protected int getEnduranceBuff() { return statuses.get(StatusType.ENDURANCE_BUFF.toString(), 0); }
	protected int getAgilityBuff() { return statuses.get(StatusType.AGILITY_BUFF.toString(), 0); }
	
	protected int stepDown(int value) { if (value < 3) return value; else if (value < 7) return 3 + (value - 3)/2; else return 5 + (value - 7)/3; } 
	
	protected int getEndurance() { return Math.max((baseEndurance + getEnduranceBuff()) - (getHealthDegradation()), 0); }
	
	protected int getAgility() { return Math.max((baseAgility + getAgilityBuff()) - (getHealthDegradation() + getStaminaDegradation() + getCumInflation()), 0); }

	protected int getPerception() { return Math.max(basePerception, 0); }

	protected int getMagic() { return Math.max(baseMagic, 0); }

	protected int getCharisma() { return Math.max(baseCharisma, 0); }
	
	public int getDefense() { return Math.max(baseDefense, 0); }
	protected int getTraction() { return 2; }
	
	public int getHealthDegradation() { return getDegradation(healthTiers, currentHealth); }
	public int getStaminaDegradation() { return getDegradation(staminaTiers, currentStamina); }
	public int getCumInflation() { return buttful >= 20 || mouthful >= 20 ? 2 : buttful >=10 || mouthful >= 10 ? 1 : 0; } 
	
	protected int getDegradation(IntArray tiers, int currentValue) {
		int numTiers = tiers.size;
		int value = currentValue;
		for (int tier : tiers.items) {
			value -= tier;
			numTiers--;
			if (value <= 0) return numTiers;
		}
		return numTiers;
	}
	
	public void modLust(int lustMod) { lust += lustMod; }
	
	// this method can be removed, as the CharacterState could dictate what modifiers are applied to the stamina cost of a technique
	protected int getStaminaMod(Technique technique) {
		int staminaMod = technique.getStaminaCost();
		if (staminaMod >= 0) {
			staminaMod -= getStaminaRegen();
			if (staminaMod < 0) staminaMod = 0;
		}
		else {
			staminaMod -= getStaminaRegen();
		}
		return staminaMod;
	}
	
	// right now this and "doAttack" handle once-per-turn character activities
	public void extractCosts(Technique technique) {
		int staminaMod = getStaminaMod(technique); 
		modStamina(-staminaMod);
		modStability(-technique.getStabilityCost());
		modStability(getStabilityRegen());
		modMana(-technique.getManaCost());
		modHealth(-getBloodLossDamage());
		
		Array<String> toRemove = new Array<String>();
		// statuses degrade with time in a general way currently
		for(String key: statuses.keys()) {
			StatusType type = StatusType.valueOf(key);
			if (!type.degrades()) continue;
			int value = statuses.get(key) - 1;
			statuses.put(key, value);
			if (value <= 0) {
				toRemove.add(key);
			}
		}
		for(String key: toRemove) {
			statuses.remove(key);
		}
		
		oldStance = stance;
		stance = technique.getStance();
		if (oldStance != Stance.PRONE && oldStance != Stance.SUPINE && (stance == Stance.PRONE || stance == Stance.SUPINE)) {
			setStabilityToMin();
		}
	}
	
	private int getBloodLossDamage() {
		return Math.max(0, statuses.get(StatusType.BLEEDING.toString(), 0) - getEndurance());
	}
	
	protected CharacterState getCurrentState(AbstractCharacter target) {		
		return new CharacterState(getStats(), getRawStats(), weapon, stability < 5, currentMana, this, target);
	}
	
	protected boolean alreadyIncapacitated() {
		return stance.isIncapacitatingOrErotic();
	}
	
	public Attack doAttack(Attack resolvedAttack) {
		
		int bleedDamage = getBloodLossDamage();
		if (bleedDamage > 0) {
			resolvedAttack.addMessage(label + (secondPerson ? " bleed" : " bleeds") + " out for " + getBloodLossDamage() + " damage!");
		}
		
		if (!resolvedAttack.isSuccessful()) {
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + (resolvedAttack.getStatus() == Status.MISSED ? " but missed!" : (resolvedAttack.getStatus() == Status.EVADED ? " but was evaded!" : resolvedAttack.getStatus() == Status.PARRIED ? " but was parried!" : resolvedAttack.getStatus() == Status.FIZZLE ? " but the spell fizzled!" : "! FAILURE!")));
			
			if ((resolvedAttack.getStatus() == Status.MISSED || resolvedAttack.getStatus() == Status.EVADED) && enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO && resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM) {
				resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " crashes to the ground!");
				stance = Stance.PRONE;
			}
			else if(resolvedAttack.getForceStance() != null) {
				stance = oldStance;
			}	
			return resolvedAttack;
		}
		
		if (resolvedAttack.getItem() != null) {
			resolvedAttack.addMessage(consumeItem(resolvedAttack.getItem()));
		}
		else if (!resolvedAttack.isAttack() && !resolvedAttack.isClimax() && resolvedAttack.getLust() == 0) {
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + "!");
		}
		
		if (resolvedAttack.isHealing()) {
			modHealth(resolvedAttack.getHealing());
			
			resolvedAttack.addMessage("You heal for " + resolvedAttack.getHealing()+"!");
		}
		Buff buff = resolvedAttack.getBuff();
		if (buff != null) {
			statuses.put(buff.type.toString(), buff.power);
		}
		if (enemyType != null) {
			if (resolvedAttack.getForceStance() == Stance.DOGGY_BOTTOM || resolvedAttack.getForceStance() == Stance.ANAL_BOTTOM || resolvedAttack.getForceStance() == Stance.STANDING_BOTTOM) {
				resolvedAttack.addMessage("You are being anally violated!");
				resolvedAttack.addMessage("Your hole is stretched by " + pronouns.getPossessive() + " fat dick!");
				resolvedAttack.addMessage("Your hole feels like it's on fire!");
				resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " cock glides smoothly through your irritated anal mucosa!");
				resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " rhythmic thrusting in and out of your asshole is emasculating!");
				resolvedAttack.addMessage("You are red-faced and embarassed because of " + pronouns.getPossessive() + " butt-stuffing!");
				resolvedAttack.addMessage("Your cock is ignored!");
			}		
			else if(resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM || resolvedAttack.getForceStance() == Stance.SIXTY_NINE_BOTTOM) {
				if (enemyType == EnemyEnum.HARPY) {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " tastes horrible! Harpies are highly unhygenic!");
					resolvedAttack.addMessage("You learned Anatomy (Harpy)!");
					resolvedAttack.addMessage("You learned Behavior (Harpy)!");
					resolvedAttack.addMessage("There is a phallus in your mouth!");
					resolvedAttack.addMessage("It blew past your lips!");
					resolvedAttack.addMessage("The harpy is holding your head in place with " + pronouns.getPossessive() + " talons and balancing herself with her wings!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " flaps violently while humping your face!  Her cock tastes awful!");
				}
				else {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " stuffs her cock into your face!");
					resolvedAttack.addMessage("You suck on " + pronouns.getPossessive() + " cock!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " licks her lips!");
				}
				if (resolvedAttack.getForceStance() == Stance.SIXTY_NINE_BOTTOM) {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " deepthroats your cock!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " pistons " + pronouns.getPossessive() + " own cock in and out of your mouth!");
				}
			}
			else if (resolvedAttack.getForceStance() == Stance.FACE_SITTING_BOTTOM) {
				resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " rides your face!");
				resolvedAttack.addMessage("You receive a faceful of ass!");
			}
			else if (resolvedAttack.getForceStance() == Stance.KNOTTED_BOTTOM) {
				if (knotInflate == 0) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " powerful hips try to force something big inside!");
					resolvedAttack.addMessage("You struggle... but can't escape!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " grapefruit-sized knot slips into your rectum!  You take 4 damage!");
					resolvedAttack.addMessage("You learned about Anatomy(Wereslut)! You are being bred!");
					resolvedAttack.addMessage("Your anus is permanently stretched!");
				}
				else if (knotInflate < 3) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " tremendous knot is still lodged in your rectum!");
					resolvedAttack.addMessage("You can't dislodge it; it's too large!");
					resolvedAttack.addMessage("You're drooling!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " fat thing is plugging your shithole!");					
				}
				else {
					resolvedAttack.addMessage("The battle is over, but your ordeal has just begun!");
					resolvedAttack.addMessage("You are about to be bred like a bitch!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + "'s going to ejaculate her runny dog cum in your bowels!");	
				}
				knotInflate++;
			}
		}
		
		// all climax logic should go here
		if (resolvedAttack.isClimax()) {
			resolvedAttack.addMessage(climax());
		}
		
		for (Bonus bonus : resolvedAttack.getBonuses()) {
			String bonusDescription = bonus.getDescription(label);
			if (bonusDescription != null) {
				resolvedAttack.addMessage(bonusDescription);
			}
		}
		
		return resolvedAttack;
	}
	
	public Array<Array<String>> receiveAttack(Attack attack) {
		Array<String> result = attack.getMessages();
		
		boolean knockedDown = false;
		if (attack.isSuccessful()) {
			if (attack.getForceStance() == Stance.DOGGY_BOTTOM && bootyliciousness != null)
				result.add("They slap their hips against your " + bootyliciousness.toString().toLowerCase() + " booty!");
			
			if (attack.isAttack() || attack.isClimax() || attack.getLust() > 0) {
				result.add(attack.getUser() + " used " + attack.getName() +  " on " + (secondPerson ? label.toLowerCase() : label) + "!");
			}
			
			struggle += attack.getGrapple();
			if (attack.isClimax()) {
				struggle = 0;
			}
			if (attack.getForceStance() == Stance.BALANCED) {
				result.add(attack.getUser() + " broke free!");
				if (stance == Stance.FELLATIO_BOTTOM) {
					result.add("It slips out of your mouth and you get to your feet!");
				}
				else if (stance == Stance.SIXTY_NINE_BOTTOM) {
					result.add("You spit out their cock and push them off!");
				}
				else if (stance == Stance.HANDY_BOTTOM) {
					
				}
				else if (stance.isAnalReceptive()) {
					result.add("It pops out of your ass and you get to your feet!");
				}
			}
			
			int damage = attack.getDamage();
			if (!attack.ignoresArmor()) {
				damage -= getDefense();
			}
			
			if (damage > 0) {	
				currentHealth -= damage;
				result.add("The blow strikes for " + damage + " damage!");
				int bleed = attack.getBleeding();
				if (bleed > 0 && canBleed()) {
					result.add("It opens wounds! +" + bleed + " blood loss!");
					statuses.put(StatusType.BLEEDING.toString(), statuses.get(StatusType.BLEEDING.toString(), 0) + bleed);
				}
			}
			
			int knockdown = attack.getForce();
			knockdown -= getTraction();
			if (knockdown > 0) {
				if (!alreadyIncapacitated()) {
					stability -= knockdown;
					result.add("It's a solid blow! It reduces balance by " + knockdown + "!");
					if (stability <= 0) {
						result.add(label + (secondPerson ? " are " : " is ") + "knocked to the ground!");
						setStabilityToMin();
						stance = Stance.SUPINE;
						knockedDown = true;
					}
				}
			}
			
			int trip = attack.getTrip();
			if (trip >= 100) {
				if (!alreadyIncapacitated()) {
					setStabilityToMin();
					stance = Stance.PRONE;
					result.add(label + (secondPerson ? " are " : " is ") + "tripped and "+ (secondPerson ? "fall" : "falls") +" prone!");
					knockedDown = true;
				}
			}
			
			int armorSunder = attack.getArmorSunder();
			if (armorSunder > 0) {
				// this shouldn't lower baseDefense, instead sundering armor
				if (baseDefense > 0) {
					result.add("It's an armor shattering blow! It reduces armor by " + (armorSunder > baseDefense ? baseDefense : armorSunder) + "!");
					baseDefense -= armorSunder;
					if (baseDefense < 0) baseDefense = 0;
					
				}
			}
			
			int gutcheck = attack.getGutCheck();
			if (gutcheck > 0) {
				if (!alreadyIncapacitated()) {
					currentStamina -= gutcheck;
					result.add("It's a blow to the stomach! It reduces stamina by " + gutcheck + "!");
					if (currentStamina <= 0) {
						result.add(label + (secondPerson ? " fall " : " falls ") + "to the ground!");
						setStabilityToMin();
						stance = Stance.PRONE;
						knockedDown = true;
					}
				}
			}
			
			int disarm = attack.getDisarm();
			if (disarm > 0) {
				if (disarm >= 100) {
					if (disarm()) {
						result.add((secondPerson ? "You are " : label + " is ") + "disarmed!");
					}
				}
			}
			
			Stance forcedStance = attack.getForceStance();
			if (forcedStance != null) {
				result.add(label + (secondPerson ? " are " : " is ") + "forced into " + forcedStance.toString() + " stance!");
				stance = forcedStance;
				if (forcedStance == Stance.PRONE || forcedStance == Stance.SUPINE) {
					setStabilityToMin();
				}
				else if (forcedStance == Stance.KNEELING && stability > getMaxStability() / 2) {
					stability = getMaxStability() / 2;
				}
			}
			
			String lustIncrease = increaseLust();
			if (lustIncrease != null) result.add(lustIncrease);
			
			if (attack.getLust() > 0) {
				lustIncrease = increaseLust(attack.getLust());
				if (lustIncrease != null) result.add(lustIncrease);
				result.add(label + (secondPerson ? " are seduced" : " is seduced") + "! " + (secondPerson ? " Your " : " Their ") + "lust raises by " + attack.getLust() + "!");
			}	
			
			String internalShotText = null;
			if (attack.getClimaxType() == ClimaxType.ANAL) {
				internalShotText = fillButt(attack.getClimaxVolume());
			}
			else if (attack.getClimaxType() == ClimaxType.ORAL) {
				internalShotText = fillMouth(1);
			}
			if (internalShotText != null) result.add(internalShotText);
			
			if (buttful > 0 && !stance.isAnalReceptive()) result.add(getLeakMessage());
			if (mouthful > 0 && !stance.isOralReceptive()) result.add(getDroolMessage());
		}
		if (!alreadyIncapacitated() && !knockedDown) {
			// you tripped
			if (stability <= 0) {
				stance = Stance.PRONE;
				result.add(label + (secondPerson ? " lose your" : " loses their") + " footing and " + (secondPerson ? "trip" : "trips") + "!");
				setStabilityToMin();
			}
			// you blacked out
			else if (currentStamina <= 0) {
				result.add(label + " runs out of breath and " + (secondPerson ? "collapse" : "collapses") + "!");
				stance = Stance.SUPINE;
			}
		}
		
		if (currentHealth <= 0) {
			result.add(label + (secondPerson ? " are " : " is ")  + "defeated!");
		}
		Array<Array<String>> results =  new Array<Array<String>>();
		results.add(result);
		results.add(new Array<String>());
		return results;
	}
	
	protected abstract String climax();
	protected boolean canBleed() { return true; }
	
	public String consumeItem(Item item) {
		ItemEffect effect = item.getUseEffect();
		if (effect == null) { return "Item cannot be used."; }
		String result = "";
		switch (effect.getType()) {
			case HEALING:
				int currentHealth = getCurrentHealth();
				modHealth(effect.getMagnitude());
				result = "You used " + item.getName() + " and restored " + String.valueOf(getCurrentHealth() - currentHealth) + " health!";
				break;
			// this should perform buff stacking if need be - but these item buffs should be permanent until consumed
			case BONUS_STRENGTH:
				result = "You used " + item.getName() + " and temporarily increased Strength by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.STRENGTH_BUFF.toString(), effect.getMagnitude());
				break;
			case BONUS_ENDURANCE:
				result = "You used " + item.getName() + " and temporarily increased Endurance by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.ENDURANCE_BUFF.toString(), effect.getMagnitude());
				break;
			case BONUS_AGILITY:
				result = "You used " + item.getName() + " and temporarily increased Agility by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.AGILITY_BUFF.toString(), effect.getMagnitude());
				break;
			case MEAT:
				result = "You ate the " + item.getName() + "! Food stores increased by 5.";
				modFood(effect.getMagnitude());
				break;
			case BANDAGE:
				int currentBleed = statuses.get(StatusType.BLEEDING.toString(), 0);
				if (currentBleed != 0) {
					result = "You applied the " + item.getName() + " to staunch bleeding by " + Math.max(currentBleed, effect.getMagnitude()) + "!";
					statuses.put(StatusType.BLEEDING.toString(), Math.max(currentBleed - effect.getMagnitude(), 0));
				}
				break;
			default:
				break;
			
		}	
		inventory.removeValue(item, true);
		return result;
	}
	
	public boolean disarm() {
		if (weapon != null && weapon.isDisarmable()) {
			disarmedWeapon = weapon;
			weapon = null;
			return true;
		}
		return false;
		
	}
	protected String fillMouth(int mouthful) {
		this.mouthful += mouthful;
		return null;
	}
	protected String fillButt(int buttful) {
		this.buttful += buttful;
		return null;
	}
	
	protected void drainMouth() {
		mouthful = 0;
	}
	
	protected void drainButt() {
		buttful--;
	}

	public String getCumInflationPath() {
		if (buttful >= 20) {
			return AssetEnum.STUFFED_BELLY.getPath();
		}
		else if (buttful >= 10) {
			return AssetEnum.FULL_BELLY.getPath();
		}
		else if (buttful >= 5 || mouthful >= 10) {
			return AssetEnum.BIG_BELLY.getPath();
		}
		else {
			return AssetEnum.FLAT_BELLY.getPath(); 
		}
	}
	
	protected abstract String getLeakMessage();
	protected abstract String getDroolMessage();
	
	protected abstract String increaseLust();
	protected abstract String increaseLust(int lustIncrease);
	
	protected OrderedMap<Stat, Integer> getStats() {
		OrderedMap<Stat, Integer> stats = new OrderedMap<Stat, Integer>();
		for (Stat stat: Stat.values()) {
			stats.put(stat, getStat(stat));
		}
		return stats;
	}
	
	protected ObjectMap<Stat, Integer> getRawStats() {
		ObjectMap<Stat, Integer> stats = new ObjectMap<Stat, Integer>();
		for (Stat stat: Stat.values()) {
			stats.put(stat, getRawStat(stat));
		}
		return stats;
	}
	
	protected int getStat(Stat stat) {
		return stepDown(getRawStat(stat));
	}
	

	public int getRawStat(Stat stat) {
		switch(stat) {
			case STRENGTH: return getStrength();
			case ENDURANCE: return getEndurance();
			case AGILITY: return getAgility();
			case PERCEPTION: return getPerception();
			case MAGIC: return getMagic();
			case CHARISMA: return getCharisma();
			default: return -1;
		}
	}
	
	public int getBaseStat(Stat stat) {
		switch(stat) {
			case STRENGTH: return getBaseStrength();
			case ENDURANCE: return getBaseEndurance();
			case AGILITY: return getBaseAgility();
			case PERCEPTION: return getBasePerception();
			case MAGIC: return getBaseMagic();
			case CHARISMA: return getBaseCharisma();
			default: return -1;
		}
	}
	
	private int getBaseCharisma() {
		return baseCharisma;
	}

	private int getBaseMagic() {
		return baseMagic;
	}

	private int getBasePerception() {
		return basePerception;
	}

	private int getBaseAgility() {
		return baseAgility;
	}

	private int getBaseEndurance() {
		return baseEndurance;
	}
	
	private int getBaseStrength() {
		return baseStrength;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public String getStanceTransform(Technique firstTechnique) {
		String stanceTransform = firstTechnique.getStance().toString();
		String vowels = "aeiou";
		String article = vowels.indexOf(Character.toLowerCase(stanceTransform.charAt(0))) != -1 ? "an" : "a";
		if (oldStance != null && oldStance.toString().equals(stanceTransform)) {
			return "";
		}
		return label + " adopt" + (secondPerson ? "" : "s") + " " + article + " " + stanceTransform + " stance! ";
 	}
	
	
	public String getLustImagePath() {
		int lustLevel = lust > 7 ? 2 : lust > 3 ? 1 : 0;
		return "arousal/" + phallus.label + lustLevel + ".png";
	}
	
	public boolean outOfStamina(Technique technique) {
		return getStaminaMod(technique) >= currentStamina;
	}
	
	protected boolean outOfStability(Technique technique) {
		return technique.getStabilityCost() - getStabilityRegen() >= stability;
	}
	
	
	public boolean outOfStaminaOrStability(Technique technique) {
		 return outOfStamina(technique) || outOfStability(technique);
	}

	public boolean lowStaminaOrStability(Technique technique) {
		return getStaminaMod(technique) >= currentStamina - 5 || technique.getStabilityCost() - getStabilityRegen() >= stability - 5;	
	}
	
	protected boolean lowStability() {
		return stability <= 5;
	}
	
	protected boolean isErect() {
		return lust > 7;
	}
	
	public String getDefeatMessage() {
		return label + (secondPerson ? " are " : " is ") + "defeated!";
	}
	
	public void modFood(Integer foodChange) { food += foodChange; if (food < 0) food = 0; }
	
	protected int getClimaxVolume() {
		return 3;
	}
	
	public int getBleed() {
		return statuses.get(StatusType.BLEEDING.toString(), 0);
	}
	
	protected String properCase(String sample) {
		return sample.substring(0, 1).toUpperCase() + sample.substring(1);
	}
	
	private enum StanceType {
		ANAL,
		ANAL_BOTTOM,
		ORAL,
		ORAL_BOTTOM,
		HANDJOB,
		HANDJOB_BOTTOM,
		FACESIT,
		FACESIT_BOTTOM,
		INCAPACITATED,
		NORMAL
	}
	
	public enum Stance {
		BALANCED (AssetEnum.BALANCED.getPath()),
		DEFENSIVE (AssetEnum.DEFENSIVE.getPath()),
		OFFENSIVE (AssetEnum.OFFENSIVE.getPath()),
		BLITZ (AssetEnum.BLITZ.getPath()),
		COUNTER (AssetEnum.COUNTER.getPath()),
		STONEWALL (AssetEnum.BALANCED.getPath()),
		PRONE (StanceType.INCAPACITATED, AssetEnum.PRONE.getPath(), false, false, true),
		SUPINE (StanceType.INCAPACITATED, AssetEnum.SUPINE.getPath(), false, false, true),
		KNEELING (AssetEnum.KNEELING.getPath(), false, true, true),
		AIRBORNE (AssetEnum.AIRBORNE.getPath(), true, false, false), 
		CASTING (AssetEnum.CASTING.getPath()),
		ITEM (AssetEnum.ITEM.getPath()), 
		
		FULL_NELSON (AssetEnum.FULL_NELSON.getPath()), 
		DOGGY (StanceType.ANAL, AssetEnum.DOGGY.getPath()), 
		ANAL (StanceType.ANAL, AssetEnum.ANAL.getPath()), 
		STANDING (StanceType.ANAL, AssetEnum.STANDING.getPath()),
		HANDY (StanceType.HANDJOB, AssetEnum.HANDY.getPath()),
		COWGIRL (StanceType.ANAL, AssetEnum.COWGIRL.getPath()),
		KNOTTED (StanceType.ANAL, AssetEnum.KNOTTED.getPath()), 
		FELLATIO (StanceType.ORAL, AssetEnum.FELLATIO.getPath()), 
		FACE_SITTING(StanceType.FACESIT, AssetEnum.FACE_SITTING.getPath()),
		SIXTY_NINE(StanceType.ORAL, AssetEnum.SIXTY_NINE.getPath()),
		
		FULL_NELSON_BOTTOM (AssetEnum.FULL_NELSON.getPath()), 
		DOGGY_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.DOGGY.getPath()), 
		ANAL_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.ANAL.getPath()), 
		STANDING_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.STANDING.getPath()),
		HANDY_BOTTOM (StanceType.HANDJOB_BOTTOM, AssetEnum.HANDY.getPath()),
		COWGIRL_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.COWGIRL.getPath()),
		KNOTTED_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.KNOTTED.getPath()), 
		FELLATIO_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.FELLATIO.getPath()), 
		FACE_SITTING_BOTTOM (StanceType.FACESIT_BOTTOM, AssetEnum.FACE_SITTING.getPath()),
		SIXTY_NINE_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.SIXTY_NINE.getPath()),
		
		ERUPT (AssetEnum.ERUPT.getPath()), 
		;
		// need to create: boolean anal, boolean oral, boolean method erotic, boolean incapacitated
		private final String texturePath;
		private final StanceType type;
		public final boolean receivesHighAttacks;
		public final boolean receivesMediumAttacks;
		public final boolean receivesLowAttacks;
		
		private Stance(String texturePath) {
			this(StanceType.NORMAL, texturePath, true, true, true);
		}
		
		private Stance(StanceType type, String texturePath) {
			this(type, texturePath, true, true, true);
		}
		
		private Stance(String texturePath, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
			this(StanceType.NORMAL, texturePath, receivesHigh, receivesMedium, receivesLow);
		}
		
		private Stance(StanceType type, String texturePath, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
			this.type = type;
			this.texturePath = texturePath;
			receivesHighAttacks = receivesHigh;
			receivesMediumAttacks = receivesMedium;
			receivesLowAttacks = receivesLow;
		}
		public String getPath() { return texturePath; }
		
		public boolean isErotic() {
			 return isEroticReceptive() || isEroticPenetration();
		}
		
		public boolean isEroticReceptive() {
			return type == StanceType.ANAL_BOTTOM || type == StanceType.ORAL_BOTTOM || type == StanceType.HANDJOB_BOTTOM || type == StanceType.FACESIT_BOTTOM;
		}
		
		public boolean isEroticPenetration() {
			return type == StanceType.ANAL || type == StanceType.ORAL || type == StanceType.HANDJOB || type == StanceType.FACESIT;
		}
		
		public boolean isIncapacitating() {
			return type == StanceType.INCAPACITATED;
		}
		
		public boolean isAnalReceptive() {
			return type == StanceType.ANAL_BOTTOM;
		}
		
		public boolean isOralReceptive() {
			return type == StanceType.ORAL_BOTTOM;
		}
		
		public boolean isAnalPenetration() {
			return type == StanceType.ANAL;
		}
		public boolean isOralPenetration() {
			return type == StanceType.ORAL;
		}
		
		public boolean isIncapacitatingOrErotic() {
			return isErotic() || isIncapacitating(); 
		}
	}
	
	protected enum PhallusType {
		SMALL("Small"),
		NORMAL("Human"),
		MONSTER("Monster");
		private final String label;

		PhallusType(String label) {
		    this.label = label;
		}
	}
	
	public enum Stat {
		STRENGTH(AssetEnum.STRENGTH.getPath(), "Strength determines raw attack power, which affects damage,\nhow much attacks unbalance an enemies, and contests\nof strength, such as wrestling, struggling,\nor weapon locks."),
		ENDURANCE(AssetEnum.ENDURANCE.getPath(), "Endurance determines stamina and resilience, which affects\nyour ability to keep up an assault without getting tired,\nyour ability to shrug off low damage attacks,\nand wear heavier armor without becoming exhausted."),
		AGILITY(AssetEnum.AGILITY.getPath(), "Agility determines balance and skill, affecting your ability\nto keep a sure footing even while doing acrobatic\nmaneuvers, getting unblockable attacks against\nenemies, and evading enemy attacks."),
		PERCEPTION(AssetEnum.PERCEPTION.getPath(), "Perception determines your ability to see what attacks an\nenemy may use next and prepare accordingly, as well as\nyour base scouting ability, which determines what\ninformation you can see about upcoming areas."),
		MAGIC(AssetEnum.MAGIC.getPath(), "Magic determines your magical capabilities, such as how\npowerful magic spells are, and how many of them\nyou can cast before becoming magically exhausted."),
		CHARISMA(AssetEnum.CHARISMA.getPath(), "Charisma determines your ability to influence an enemy,\ngetting them to calm down and listen to reason,\nenraging them, or seducing them.");

		private final String path;
		private final String description;
		private Stat(String path, String description) {
			this.path = path;
			this.description = description;
		}
		public String getPath() {
			return path;
		}
		public String getDescription() {
			return description;
		}
	}

	protected enum PronounSet {
		MALE ("he", "him", "his", "himself"),
		FEMALE ("she", "her", "her", "herself"),
		SECOND_PERSON ("you", "you", "your", "yourself")
		;
		private final String nominative, objective, possessive, reflexive;
		private PronounSet(String nominative, String objective, String possessive, String reflexive) {
			this.nominative = nominative;
			this.objective = objective;
			this.possessive = possessive;
			this.reflexive = reflexive;
		}
		public String getNominative() { return nominative; }
		public String getObjective() { return objective; }
		public String getPossessive() { return possessive; }
		public String getReflexive() { return reflexive; }
		
	}
}
