package com.scythe.musicgenerator.defines;

public class Mode
{
	public static final int NB_MODES = 7;
	
	public static final int IONIAN		= 0;
	public static final int DORIAN		= 6;
	public static final int PHRYGIAN	= 5;
	public static final int LYDIAN		= 4;
	public static final int MIXOLYDIAN	= 3;
	public static final int EOLIAN		= 2;
	public static final int LOCRIAN		= 1;
	
	public static String toString(int mode)
	{
		switch(mode)
		{
			case IONIAN		: return "IONIAN (major)";
			case DORIAN		: return "DORIAN";
			case PHRYGIAN	: return "PHRYGIAN";
			case LYDIAN		: return "LYDIAN";
			case MIXOLYDIAN	: return "MIXOLYDIAN";
			case EOLIAN		: return "EOLIAN (minor)";
			case LOCRIAN	: return "LOCRIAN";
		}
		
		return "";
	}
}
