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
	
	public TimedNote(TimedNote note)
	{
		mNote = note.mNote;
		mDuration = note.mDuration;
		mDotted = note.mDotted;
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
		return durationInTime() + mNote.toString();
	}
	
	private Note mNote;
	private int mDuration;
	private boolean mDotted;
}
