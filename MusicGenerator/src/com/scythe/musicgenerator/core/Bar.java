package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Duration;

public class Bar
{
	public Bar(int[] rhythmSignature, TimedNote[] notes)
	{
		mRhythmSignature = rhythmSignature;
		mNotes = notes;
	}
	
	public boolean isFullyValid()
	{
		return isRhythmSignatureValid() && isNotesValid();
	}
	
	public boolean isRhythmSignatureValid()
	{
		return mRhythmSignature.length != 2 || Duration.convert(mRhythmSignature[1]) == 0 ? false : true;
	}
	
	public boolean isNotesValid()
	{
		float expectedTotalTime = mRhythmSignature[0] * Duration.convert(mRhythmSignature[1]);
		
		float realTotalTime = 0;
		for(int i = 0; i < mNotes.length; i++)
		{
			realTotalTime += mNotes[i].duration();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	private int[] mRhythmSignature;
	private TimedNote[] mNotes;
}
