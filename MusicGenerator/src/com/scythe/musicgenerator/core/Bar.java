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
		return (mSignature.numerator() == 0 || mSignature.denominator() == 0 || Duration.convertInRhythmSignature(mSignature.denominator()) == 0) ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mSignature.numerator() * Duration.convertInTime(mSignature.denominator());
		
		float realTotalTime = 0;
		for(int i = 0; i < mTimedElements.size(); i++)
		{
			realTotalTime += mTimedElements.get(i).durationInTime();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	public BarSignature signature()
	{
		return mSignature;
	}
	
	public ArrayList<TimedElement> elements()
	{
		return mTimedElements;
	}
	
	@Override
	public String toString()
	{
		String str = mSignature.numerator() + "/" + Duration.convertInRhythmSignature(mSignature.denominator()) + " ";
		for(int i = 0; i < mTimedElements.size(); i++)
		{
			str += mTimedElements.get(i) + " ";
		}
		
		return str;
	}
	
	private BarSignature mSignature;
	private ArrayList<TimedElement> mTimedElements;
}
