package com.scythe.musicgenerator.core;

public class Chord
{
	public Chord(Note[] notes)
	{
		mNotes = notes;
	}
	
	public Note note(int index)
	{
		return mNotes[index];
	}
	
	public int noteCnt()
	{
		return mNotes.length;
	}
	
	@Override
	public String toString()
	{
		String str = "";
		for(int i = 0; i < mNotes.length; i++)
		{
			str += mNotes[i] + (i == mNotes.length - 1 ? "" : " ");
		}
		
		return str;
	}
	
	private Note[] mNotes;
}
