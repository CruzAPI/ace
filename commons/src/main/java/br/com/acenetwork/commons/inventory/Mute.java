package br.com.acenetwork.commons.inventory;

import br.com.acenetwork.commons.executor.MuteCMD;
import br.com.acenetwork.commons.player.CommonPlayer;

public class Mute extends Timer
{
	private final String user;
	private final String reason;
	
	public Mute(CommonPlayer cp, String user, String reason)
	{
		super(cp);
		
		this.user = user;
		this.reason = reason;
	}
	
	@Override
	public void confirmed()
	{
		MuteCMD.mute(cp.getPlayer(), user, currentTimeMillis(), cp.getTag(), reason);
	}
}
