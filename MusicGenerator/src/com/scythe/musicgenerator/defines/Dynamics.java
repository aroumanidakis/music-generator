package com.scythe.musicgenerator.defines;

public class Dynamics
{
	public static final int PIANISSISSIMO = 16;
	public static final int PIANISSIMO = 32;
	public static final int PIANO = 48;
	public static final int MEZZOPIANO = 64;
	public static final int MEZZOFORTE = 80;
	public static final int FORTE = 96;
	public static final int FORTISSIMO = 110;
	public static final int FORTISSISSIMO = 126;
	
	public static String toString(int dynamics)
	{
		switch(dynamics)
		{
			case PIANISSISSIMO:	return "ppp";
			case PIANISSIMO:	return "pp";
			case PIANO:			return "p";
			case MEZZOPIANO:	return "mp";
			case MEZZOFORTE:	return "mf";
			case FORTE:			return "f";
			case FORTISSIMO:	return "ff";
			case FORTISSISSIMO:	return "fff";
		}
		
		return "";
	}
}
