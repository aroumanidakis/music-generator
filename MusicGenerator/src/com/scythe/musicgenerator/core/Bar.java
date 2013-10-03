package com.scythe.musicgenerator.core;

public class Bar
{
	public Bar(int[] rhythmSignature, TimedNote[] notes)
	{
		mRhythmSignature = rhythmSignature;
		mNotes = notes;
	}
	
	private int[] mRhythmSignature;
	private TimedNote[] mNotes;
}
