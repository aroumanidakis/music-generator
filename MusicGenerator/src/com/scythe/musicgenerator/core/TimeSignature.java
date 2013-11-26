package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.core.TimedElement.Duration;

/**
 * Class intended to handle a time signature.
 * @author Scythe
 */
public class TimeSignature
{
	/**
	 * Main constructor of the class.
	 * @param signature The time signature as String. Examples: "3/4", "4/4", "6/8", etc...
	 */
	public TimeSignature(String signature)
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
		
		if(refTime == -1)
		{
			invalidate();
		}
		
		mDenominator = refTime;
	}
	
	/**
	 * Gets the key signature numerator.
	 * @return The key signature numerator.
	 */
	public int getNumerator()
	{
		return mNumerator;
	}
	
	/**
	 * Gets the key signature denominator.
	 * @return The key signature denominator.
	 */
	public int getDenominator()
	{
		return mDenominator;
	}
	
	/**
	 * Gets the number of times the time signature counts. Only regular bars are supported for the time being.
	 * @return 2, 3 or 4 if the bar is regular, -1 otherwise.
	 */
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
	
	/**
	 * Gets the type of time signature which can be Type.BINARY, Type.TERNARY, TYPE.ASYMMETRIC.
	 * @see TimeSignature.Type
	 * @return the type of time signature.
	 */
	public int getType()
	{
		if(mNumerator == 2 || mNumerator == 3 || mNumerator == 4)
		{
			return Type.BINARY;
		}
		else if(mNumerator == 6 || mNumerator == 9 || mNumerator == 12)
		{
			return Type.TERNARY;
		}
		else
		{
			return Type.ASYMMETRIC;
		}
	}
	
	/**
	 * Checks if the instance of TimeSignature is valid. It can be invalid when a wrong String has been passed to the constructor.
	 * @return true if the instance is valid, false otherwise.
	 */
	public boolean isValid()
	{
		return mIsValid;
	}
	
	public static TimeSignature getRandom()
	{
		String[] signatures = {"2/1", "2/2", "2/4", "2/8", "3/1", "3/2", "3/4", "3/8", "4/1", "4/2", "4/4", "4/8", "6/2", "6/4", "6/8", "6/16", "9/2", "9/4", "9/8", "9/16", "12/2", "12/4", "12/8", "12/16"};
		return new TimeSignature(signatures[(int)(Math.random() * (signatures.length - 1))]);
	}
	
	@Override
	public boolean equals(Object signature)
	{
		TimeSignature sig = (TimeSignature)signature;
		return mNumerator == sig.mNumerator && mDenominator == sig.mDenominator;
	}
	
	@Override
	public String toString()
	{
		return mNumerator + "/" + Duration.convertInTimeSignature(mDenominator);
	}
	
	private void invalidate()
	{
		mIsValid = false;
		mNumerator = 0;
		mDenominator = 0;
	}
	
	/**
	 * Class of constant values for TimeSignature types.
	 * @author Scythe
	 */
	public static class Type
	{
		public static final int BINARY = 0;
		public static final int TERNARY = 1;
		public static final int ASYMMETRIC = 2;
	}
	
	private int mNumerator;
	private int mDenominator;
	private boolean mIsValid;
}
