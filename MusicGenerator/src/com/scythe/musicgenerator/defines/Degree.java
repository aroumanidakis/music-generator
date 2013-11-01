package com.scythe.musicgenerator.defines;

public class Degree
{
	public static final int COUNT = 7;
	
	public static final int I = 0;
	public static final int II = 1;
	public static final int III = 2;
	public static final int IV = 3;
	public static final int V = 4;
	public static final int VI = 5;
	public static final int VII = 6;
	
	public static String toString(int degree)
	{
		switch(degree)
		{
			case I: return "I";
			case II: return "II";
			case III: return "III";
			case IV: return "IV";
			case V: return "V";
			case VI: return "VI";
			case VII: return "VII";
		}
		
		return "";
	}
}
