package br.com.acenetwork.survival.ability;

import org.bukkit.event.Listener;

import br.com.acenetwork.survival.player.SurvivalPlayer;

public abstract class Ability implements Listener
{
	protected final SurvivalPlayer sp;
	
	public Ability(SurvivalPlayer sp)
	{
		this.sp = sp;
	}
}