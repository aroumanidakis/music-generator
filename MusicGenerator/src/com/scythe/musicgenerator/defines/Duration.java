package com.scythe.musicgenerator.defines;


public class Duration
{
	public static final int COUNT = 5;
	
	public static final int QUADRUPLE = 0;
	public static final int DOUBLE = 1;
	public static final int SINGLE = 2;
	public static final int HALF = 3;
	public static final int QUARTER = 4;
	
	public static float convertInTime(int duration)
	{
		switch(duration)
		{
			case QUADRUPLE:	return 4;
			case DOUBLE:	return 2;
			case SINGLE:	return 1;
			case HALF:		return 0.5f;
			case QUARTER:	return 0.25f;
		}
		
		return 0;
	}
	
	public static int convertInRhythmSignature(int duration)
	{
		switch(duration)
		{
			case QUADRUPLE:	return 1;
			case DOUBLE:	return 2;
			case SINGLE:	return 4;
			case HALF:		return 8;
			case QUARTER:	return 16;
		}
		
		return 0;
	}
	
	public static int reverseConvert(int rhythmSignature)
	{
		switch(rhythmSignature)
		{
			case 1:		return QUADRUPLE;
			case 2:		return DOUBLE;
			case 4:		return SINGLE;
			case 8:		return HALF;
			case 16:	return QUARTER;
		}
		
		return 0;
	}
}
