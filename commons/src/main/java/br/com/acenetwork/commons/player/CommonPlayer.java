package br.com.acenetwork.commons.player;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import br.com.acenetwork.commons.constants.Tag;
import br.com.acenetwork.commons.inventory.GUI;

public interface CommonPlayer extends Listener
{
	UUID getUniqueID();
	String getUUID();
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
	void toggleScoreboard();
	void updateScoreboard();
	Player getPlayer();
	void reset();
}
