package br.com.acenetwork.commons.constants;

public enum Language
{
	ENGLISH("en_us"), PORTUGUESE("pt_br");

	private String name;

	Language(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}	
}