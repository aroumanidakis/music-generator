package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.TimedElement.Duration;

@SuppressWarnings("serial")
public class Bar extends ArrayList<TimedElement>
{
	public Bar()
	{
		mSignature = new TimeSignature("4/4");
	}
	
	public Bar(TimeSignature signature)
	{
		mSignature = signature;
	}
	
	public Bar(String timeSignature)
	{
		mSignature = new TimeSignature(timeSignature);
	}
	
	public boolean isValid()
	{
		return isRhythmSignatureValid() && isNotesValid();
	}
	
	private boolean isRhythmSignatureValid()
	{
		return (mSignature.numerator() == 0 || mSignature.denominator() == 0 || Duration.convertInTimeSignature(mSignature.denominator()) == 0) ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mSignature.numerator() * Duration.convertInTime(mSignature.denominator());
		
		float realTotalTime = 0;
		for(int i = 0; i < size(); i++)
		{
			realTotalTime += get(i).durationInTime();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	public TimeSignature signature()
	{
		return mSignature;
	}
	
	@Override
	public String toString()
	{
		String str = mSignature.numerator() + "/" + Duration.convertInTimeSignature(mSignature.denominator()) + " ";
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + " ";
		}
		
		return str;
	}
	
	private TimeSignature mSignature;
}
