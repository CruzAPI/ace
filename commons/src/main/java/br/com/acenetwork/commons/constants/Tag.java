package br.com.acenetwork.commons.constants;

public enum Tag
{
	OWNER("§4"), ADMIN("§c"), MOD("§5"), TRIAL_MOD("§d"), DEFAULT("§7");

	private String color;
	
	Tag(String color)
	{
		this.color = color;
	}

	@Override
	public String toString()
	{
		return color;
	}

	public String getPermission()
	{
		return "cmd.tag." + name().toLowerCase().replace("_", "");
	}

	public String getName()
	{
		return this + this.name();
	}
}
