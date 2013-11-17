package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Duration;

@SuppressWarnings("serial")
public class TimedElement extends ArrayList<Note>
{
	public TimedElement(int duration, boolean dotted)
	{
		mDuration = duration;
		mDotted = dotted;
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
	
	private int mDuration;
	private boolean mDotted;
}
