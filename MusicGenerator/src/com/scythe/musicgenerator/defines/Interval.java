package com.scythe.musicgenerator.defines;

public class Interval
{
	public static final int NB_INTERVALS = 12;
	
	public static final int UNISON = 0;
	public static final int MINOR_SECOND = 1;
	public static final int MAJOR_SECOND = 2;
	public static final int MINOR_THIRD = 3;
	public static final int MAJOR_THIRD = 4;
	public static final int PERFECT_FOURTH = 5;
	public static final int DIMINISHED_FIFTH = 6;
	public static final int PERFECT_FIFTH = 7;
	public static final int MINOR_SIXTH = 8;
	public static final int MAJOR_SIXTH = 9;
	public static final int MINOR_SEVENTH = 10;
	public static final int MAJOR_SEVENTH = 11;
	
	public static String toString(int interval)
	{
		switch(interval)
		{
			case UNISON: return "UNISON";
			case MINOR_SECOND: return "MINOR_SECOND";
			case MAJOR_SECOND: return "MAJOR_SECOND";
			case MINOR_THIRD: return "MINOR_THIRD";
			case MAJOR_THIRD: return "MAJOR_THIRD";
			case PERFECT_FOURTH: return "PERFECT_FOURTH";
			case DIMINISHED_FIFTH: return "DIMINISHED_FIFTH";
			case PERFECT_FIFTH: return "PERFECT_FIFTH";
			case MINOR_SIXTH: return "MINOR_SIXTH";
			case MAJOR_SIXTH: return "MAJOR_SIXTH";
			case MINOR_SEVENTH: return "MINOR_SEVENTH";
			case MAJOR_SEVENTH: return "MAJOR_SEVENTH";
		}
		
		return "";
	}
}
