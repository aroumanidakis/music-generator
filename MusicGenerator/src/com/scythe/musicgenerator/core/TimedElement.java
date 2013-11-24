package com.scythe.musicgenerator.core;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class TimedElement extends ArrayList<Note>
{
	public TimedElement(int duration, boolean dotted)
	{
		mDuration = duration;
		mDotted = dotted;
	}
	
	public TimedElement(int duration)
	{
		mDuration = duration;
		mDotted = false;
	}
	
	public int duration()
	{
		return mDuration;
	}
	
	public float durationInTime()
	{
		float duration = Duration.convertInTime(mDuration);
		return mDotted ? duration * 1.5f : duration;
	}
	
	public boolean dotted()
	{
		return mDotted;
	}
	
	public void toMidiFile(String fileName)
	{
		Bar bar = new Bar();
		bar.add(this);
		bar.toMidiFile(fileName);
	}
	
	@Override
	public String toString()
	{
		String str = durationInTime() + "[";
		
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + (i == size() - 1 ? "" : ", ");
		}
		
		str += "]";
		return str;
	}
	
	public static class Duration
	{
		public static final int WHOLE = 0;
		public static final int HALF = 1;
		public static final int QUARTER = 2;
		public static final int EIGHTH = 3;
		public static final int SIXTEENTH = 4;
		public static final int THIRTYSECOND = 5;
		
		public static int[] list()
		{
			return new int[]{WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH, THIRTYSECOND};
		}
		
		public static int random()
		{
			int[] list = list();
			return list[(int)(Math.random() * (list.length - 1))];
		}
		
		public static float convertInTime(int duration)
		{
			switch(duration)
			{
				case WHOLE: return 4;
				case HALF: return 2;
				case QUARTER: return 1;
				case EIGHTH: return 0.5f;
				case SIXTEENTH: return 0.25f;
				case THIRTYSECOND: return 0.125f;
			}
			
			return 0;
		}
		
		public static int convertInTimeSignature(int duration)
		{
			switch(duration)
			{
				case WHOLE:	return 1;
				case HALF: return 2;
				case QUARTER: return 4;
				case EIGHTH: return 8;
				case SIXTEENTH: return 16;
				case THIRTYSECOND: return 32;
			}
			
			return 0;
		}
		
		public static int reverseConvert(int timeSignature)
		{
			switch(timeSignature)
			{
				case 1: return WHOLE;
				case 2: return HALF;
				case 4: return QUARTER;
				case 8: return EIGHTH;
				case 16: return SIXTEENTH;
				case 32: return THIRTYSECOND;
			}
			
			return -1;
		}
		
	}
	
	private int mDuration;
	private boolean mDotted;
}
