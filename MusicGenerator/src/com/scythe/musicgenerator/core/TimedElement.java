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
		public static final int QUADRUPLE = 0;
		public static final int DOUBLE = 1;
		public static final int SINGLE = 2;
		public static final int HALF = 3;
		public static final int QUARTER = 4;
		
		public static int[] list()
		{
			return new int[]{QUADRUPLE, DOUBLE, SINGLE, HALF, QUARTER};
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
				case QUADRUPLE: return 4;
				case DOUBLE: return 2;
				case SINGLE: return 1;
				case HALF: return 0.5f;
				case QUARTER: return 0.25f;
			}
			
			return 0;
		}
		
		public static int convertInTimeSignature(int duration)
		{
			switch(duration)
			{
				case QUADRUPLE:	return 1;
				case DOUBLE: return 2;
				case SINGLE: return 4;
				case HALF: return 8;
				case QUARTER: return 16;
			}
			
			return 0;
		}
		
		public static int reverseConvert(int timeSignature)
		{
			switch(timeSignature)
			{
				case 1: return QUADRUPLE;
				case 2: return DOUBLE;
				case 4: return SINGLE;
				case 8: return HALF;
				case 16: return QUARTER;
			}
			
			return 0;
		}
	}
	
	private int mDuration;
	private boolean mDotted;
}
