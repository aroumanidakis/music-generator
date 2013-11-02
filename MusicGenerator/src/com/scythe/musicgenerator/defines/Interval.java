package com.scythe.musicgenerator.defines;

public class Interval
{
	public static class Name
	{
		public static final int COUNT = 7;
		
		public static final int SECOND	= 0;
		public static final int THIRD	= 1;
		public static final int FOURTH	= 2;
		public static final int FIFTH	= 3;
		public static final int SIXTH	= 4;
		public static final int SEVENTH	= 5;
		public static final int OCTAVE	= 6;
		
		public static String toString(int interval)
		{
			switch(interval)
			{
				case SECOND: return "2nd";
				case THIRD: return "3rd";
				case FOURTH: return "4th";
				case FIFTH: return "5th";
				case SIXTH: return "6th";
				case SEVENTH: return "7th";
				case OCTAVE: return "Ove";
			}
			
			return "";
		}
	}
	
	public static class Qualification
	{
		public static final int COUNT = 5;
		
		public static final int DIMINISHED	= 0;
		public static final int MINOR		= 1;
		public static final int PERFECT		= 2;
		public static final int MAJOR		= 3;
		public static final int AUGMENTED	= 4;
		
		public static String toString(int qualification)
		{
			switch(qualification)
			{
				case DIMINISHED:	return "dim.";
				case MINOR:			return "min.";
				case PERFECT:		return "per.";
				case MAJOR:			return "maj.";
				case AUGMENTED:		return "aug.";
			}
			
			return "";
		}
	}
}
