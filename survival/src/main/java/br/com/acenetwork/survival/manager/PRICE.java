package br.com.acenetwork.survival.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class PRICE
{
	public static final List<PRICE> LIST = new ArrayList<>();
	
	public Material type;
	public double liquidity;
	public int marketCap;
	public double maxPrice;
	public int sellLimit;
	
	public double basePrice;
	public double liquidityMultiplier;
	
	public PRICE(Material type, double basePrice, int sellLimit, double liquidityMultiplier)
	{
		this.type = type;
		this.basePrice = basePrice;
		this.sellLimit = sellLimit;
		this.liquidityMultiplier = liquidityMultiplier;
		
		maxPrice = basePrice * 2.0D;
		liquidity = basePrice * sellLimit * liquidityMultiplier;
		marketCap = (int) (sellLimit * liquidityMultiplier);
		
		LIST.add(this);
	}
}