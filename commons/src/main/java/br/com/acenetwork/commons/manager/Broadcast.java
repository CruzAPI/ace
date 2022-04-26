package br.com.acenetwork.commons.manager;

public class Broadcast
{
	public final String key;
	public final Object[] args;
	
	public Broadcast(String key, Object... args)
	{
		this.key = key;
		this.args = args;
	}
}