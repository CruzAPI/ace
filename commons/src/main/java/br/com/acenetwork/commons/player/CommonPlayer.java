package br.com.acenetwork.commons.player;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import br.com.acenetwork.commons.CommonsHotbar;
import br.com.acenetwork.commons.CommonsScoreboard;
import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.executor.Balance;
import br.com.acenetwork.commons.inventory.GUI;

public interface CommonPlayer extends Listener
{
	CommonsScoreboard getCommonsScoreboard();
	void setCommonsScoreboard(CommonsScoreboard commonsScoreboard);
	CommonsHotbar getCommonsHotbar();
	double getBalance(Balance.Type balanceType);
	void setBalance(Balance.Type balanceType, double balance) throws IOException;
	double getMaxBalance(Balance.Type balanceType);
	void setMaxBalance(Balance.Type balanceType, double balance) throws IOException;
	void setCommonsHotbar(CommonsHotbar commonsHotbar);
	Tag getTag();
	boolean setTag(Tag tag);
	boolean setInvis(boolean value);
	boolean isInvis();
	void setSpecs(boolean value);
	boolean canSpecs();
	boolean delete();
	void setCombat(boolean value);
	boolean isCombat();
	boolean isCombat(long ms);
	void setPlayerCombat(boolean value);
	boolean isPlayerCombat();
	boolean isPlayerCombat(long ms);
	void setIgnoreInvisAndSpecs(boolean value);
	boolean getIgnoreInvisAndSpecs();
	void sendMessage(String key, Object... args);
	boolean hasPermission(String perm);
	void setGUI(GUI gui);
	GUI getGUI();
	String getClan();
	Tag getBestTag();
	Player getLastPlayerDamage();
	Player getPlayer();
	void reset();
}
