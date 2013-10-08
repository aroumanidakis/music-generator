package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Duration;

public class Bar
{
	public Bar(int[] rhythmSignature, Note[] notes)
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
		return mRhythmSignature.length != 2 || Duration.convertInRhythmSignature(mRhythmSignature[1]) == 0 ? false : true;
	}
	
	public boolean isNotesValid()
	{
		float expectedTotalTime = mRhythmSignature[0] * Duration.convertInTime(mRhythmSignature[1]);
		
		float realTotalTime = 0;
		for(int i = 0; i < mNotes.length; i++)
		{
			if(!mNotes[i].isTimed())
			{
				return false;
			}
			
			realTotalTime += mNotes[i].durationInTime();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	@Override
	public String toString()
	{
		String str = mRhythmSignature[0] + "/" + Duration.convertInRhythmSignature(mRhythmSignature[1]) + " ";
		for(int i = 0; i < mNotes.length; i++)
		{
			str += mNotes[i].toString() + " ";
		}
		
		return str;
	}
	
	private int[] mRhythmSignature;
	private Note[] mNotes;
}
