package br.com.acenetwork.commons.inventory;

import br.com.acenetwork.commons.executor.BanCMD;
import br.com.acenetwork.commons.player.CommonPlayer;

public class Banishment extends Timer
{
	private final String user;
	private final String reason;
	
	public Banishment(CommonPlayer cp, String user, String reason)
	{
		super(cp);
		
		this.user = user;
		this.reason = reason;
	}
	
	@Override
	public void confirmed()
	{
		BanCMD.ban(cp.getPlayer(), cp.getLocale(), user, currentTimeMillis(), cp.getTag(), reason);
	}
}
