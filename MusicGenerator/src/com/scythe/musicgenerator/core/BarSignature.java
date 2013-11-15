package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Duration;

public class BarSignature
{
	public BarSignature(String signature)
	{
		if(!signature.contains("/"))
		{
			invalidate();
		}
		
		String[] fields = signature.split("/");
		
		if(fields.length != 2)
		{
			invalidate();
		}
		
		int tmp = 0;
		try
		{
			mNumerator = Integer.parseInt(fields[0].trim());
			tmp = Integer.parseInt(fields[1].trim());
		}
		catch(NumberFormatException e)
		{
			invalidate();
		}
		
		int refTime = Duration.reverseConvert(tmp);
		
		if(refTime == 0)
		{
			invalidate();
		}
		
		mDenominator = refTime;
	}
	
	private void invalidate()
	{
		mIsValid = false;
		mNumerator = 0;
		mDenominator = 0;
	}
	
	public int numerator()
	{
		return mNumerator;
	}
	
	public int denominator()
	{
		return mDenominator;
	}
	
	public int getNumberOfTimes()
	{
		if(mNumerator == 2 || mNumerator == 6)
		{
			return 2;
		}
		else if(mNumerator == 3 || mNumerator == 9)
		{
			return 3;
		}
		else if(mNumerator == 4 || mNumerator == 12)
		{
			return 4;
		}
		else
		{
			return -1;
		}
	}
	
	public int getType()
	{
		if(mNumerator == 2 || mNumerator == 3 || mNumerator == 4)
		{
			return Type.SIMPLE;
		}
		else if(mNumerator == 6 || mNumerator == 9 || mNumerator == 12)
		{
			return Type.COMPOSED;
		}
		else
		{
			return -1;
		}
	}
	
	public boolean isValid()
	{
		return mIsValid;
	}
	
	@Override
	public boolean equals(Object signature)
	{
		BarSignature sig = (BarSignature)signature;
		return mNumerator == sig.mNumerator && mDenominator == sig.mDenominator;
	}
	
	@Override
	public String toString()
	{
		return mNumerator + "/" + Duration.convertInRhythmSignature(mDenominator);
	}
	
	public static class Type
	{
		public static final int COUNT = 2;
		
		public static final int SIMPLE		= 0;
		public static final int COMPOSED	= 1;
	}
	
	private int mNumerator;
	private int mDenominator;
	private boolean mIsValid;
}
