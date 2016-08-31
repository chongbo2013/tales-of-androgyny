package com.majalis.character;

import com.majalis.character.AbstractCharacter.Stance;

/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {
	private final Techniques technique;
	private final int strength;
	private final int block;
	public Technique(Techniques technique, int strength){
		this(technique, strength, 0);
	}
	public Technique(Techniques technique, int strength, int block){
		this.technique = technique;
		this.strength = strength;
		this.block = block;
	}
	
	public int getDamage(){
		int damage = strength + technique.getPowerMod();
		if (damage < 0) damage = 0;
		return damage;
	}	
	
	public int getBlock(){
		return block + (technique == Techniques.GUARD ? 100 : 0);
	}
	
	public Stance getStance(){
		return technique.getStanceResult();
	}
	
	public String getTechniqueName(){
		return technique.toString();
	}

	// right now this is a pass-through for technique.getStaminaCost() - could be modified by player (status effect that increases stamina cost, for instance)
	public int getStaminaCost(){
		return technique.getStaminaCost();
	}
	
	public int getStabilityCost(){
		return technique.getStabilityCost();
	}
	
}
