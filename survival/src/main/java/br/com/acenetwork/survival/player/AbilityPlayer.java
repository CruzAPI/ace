package br.com.acenetwork.survival.player;

import br.com.acenetwork.commons.player.CommonPlayer;
import br.com.acenetwork.survival.ability.Ability;

public interface AbilityPlayer extends CommonPlayer
{
	Ability getAbility();
	void setAbility(Ability ability);
}