package com.scythe.musicgenerator.defines;

public class Duration
{
	public static final int QUADRUPLE = 1;
	public static final int DOUBLE = 2;
	public static final int SINGLE = 4;
	public static final int HALF = 8;
	public static final int QUARTER = 16;
	
	public static float convert(int duration)
	{
		switch(duration)
		{
			case QUADRUPLE: return 4;
			case DOUBLE: return 2;
			case SINGLE: return 1;
			case HALF: return 0.5f;
			case QUARTER: return 0.25f;
		}
		
		return 0;
	}
}
