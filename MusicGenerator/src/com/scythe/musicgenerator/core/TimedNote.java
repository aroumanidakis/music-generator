package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Duration;

public class TimedNote
{
	public TimedNote(Note note, int duration, boolean dotted)
	{
		mNote = note;
		mDuration = duration;
		mDotted = dotted;
	}
	
	public Note note()
	{
		return mNote;
	}
	
	public void note(Note note)
	{
		mNote = note;
	}
	
	public int duration()
	{
		return mDuration;
	}
	
	public float realDuration()
	{
		float duration = Duration.convert(mDuration);
		
		if(mDotted)
		{
			duration += mDuration * 0.5f;
		}
		
		return duration;
	}
	
	public boolean dotted()
	{
		return mDotted;
	}
	
	@Override
	public String toString()
	{
		return realDuration() + mNote.toString();
	}
	
	private Note mNote;
	private int mDuration;
	private boolean mDotted;
}
