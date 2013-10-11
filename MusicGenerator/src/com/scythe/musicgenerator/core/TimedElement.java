package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Duration;

public class TimedElement
{
	public TimedElement(int duration, boolean dotted)
	{
		mDuration = duration;
		mDotted = dotted;
		mNotes = new ArrayList<Note>();
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
	
	public void addNote(Note note)
	{
		mNotes.add(note);
	}
	
	public void removeNote(int index)
	{
		mNotes.remove(index);
	}
	
	public int noteCnt()
	{
		return mNotes.size();
	}
	
	@Override
	public String toString()
	{
		String str = durationInTime() + "[";
		
		for(int i = 0; i < mNotes.size(); i++)
		{
			str += mNotes.get(i) + (i == mNotes.size() - 1 ? "" : ", ");
		}
		
		str += "]";
		return str;
	}
	
	private int mDuration;
	private boolean mDotted;
	private ArrayList<Note> mNotes;
}
