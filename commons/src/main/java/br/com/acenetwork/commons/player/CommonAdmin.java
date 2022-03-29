package br.com.acenetwork.commons.player;

public interface CommonAdmin extends CommonPlayer
{
	void setBuild(boolean value);
	boolean canBuild();
}