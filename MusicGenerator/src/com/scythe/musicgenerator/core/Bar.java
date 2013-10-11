package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Duration;

public class Bar
{
	public Bar(int[] rhythmSignature, ArrayList<TimedElement> timedElements)
	{
		mRhythmSignature = rhythmSignature;
		mTimedElements = timedElements;
	}
	
	public boolean isValid()
	{
		return isRhythmSignatureValid() && isNotesValid();
	}
	
	private boolean isRhythmSignatureValid()
	{
		return mRhythmSignature.length != 2 || Duration.convertInRhythmSignature(mRhythmSignature[1]) == 0 ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mRhythmSignature[0] * Duration.convertInTime(mRhythmSignature[1]);
		
		float realTotalTime = 0;
		for(int i = 0; i < mTimedElements.size(); i++)
		{
			realTotalTime += mTimedElements.get(i).durationInTime();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	@Override
	public String toString()
	{
		String str = mRhythmSignature[0] + "/" + Duration.convertInRhythmSignature(mRhythmSignature[1]) + " ";
		for(int i = 0; i < mTimedElements.size(); i++)
		{
			str += mTimedElements.get(i) + " ";
		}
		
		return str;
	}
	
	private int[] mRhythmSignature;
	private ArrayList<TimedElement> mTimedElements;
}
