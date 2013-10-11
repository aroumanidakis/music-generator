package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Duration;

public class Bar
{
	public Bar(BarSignature signature, ArrayList<TimedElement> timedElements)
	{
		mSignature = signature;
		mTimedElements = timedElements;
	}
	
	public boolean isValid()
	{
		return isRhythmSignatureValid() && isNotesValid();
	}
	
	private boolean isRhythmSignatureValid()
	{
		return (mSignature.timeCnt() == 0 || mSignature.refTime() == 0 || Duration.convertInRhythmSignature(mSignature.refTime()) == 0) ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mSignature.timeCnt() * Duration.convertInTime(mSignature.refTime());
		
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
		String str = mSignature.timeCnt() + "/" + Duration.convertInRhythmSignature(mSignature.refTime()) + " ";
		for(int i = 0; i < mTimedElements.size(); i++)
		{
			str += mTimedElements.get(i) + " ";
		}
		
		return str;
	}
	
	private BarSignature mSignature;
	private ArrayList<TimedElement> mTimedElements;
}
