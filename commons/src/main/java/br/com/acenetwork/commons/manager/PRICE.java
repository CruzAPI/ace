package br.com.acenetwork.commons.manager;

import org.bukkit.Material;

public class PRICE
{
	private final Material type;
	private final double base;
	private final double limit;
	
	public PRICE(Material type, double base, double limit)
	{
		this.type = type;
		this.base = base;
		this.limit = limit;
	}
	
	public int getLiquidity()
	{
		return (int) (base * 100.0D * limit);
	}
	
	public int getMarketCap()
	{
		return (int) (100.0D * limit);
	}
}