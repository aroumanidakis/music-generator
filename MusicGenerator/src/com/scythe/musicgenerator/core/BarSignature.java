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
			mTimeCnt = Integer.parseInt(fields[0]);
			tmp = Integer.parseInt(fields[1]);
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
		
		mRefTime = refTime;
	}
	
	private void invalidate()
	{
		mIsValid = false;
		mTimeCnt = 0;
		mRefTime = 0;
	}
	
	public int timeCnt()
	{
		return mTimeCnt;
	}
	
	public int refTime()
	{
		return mRefTime;
	}
	
	public boolean isValid()
	{
		return mIsValid;
	}
	
	private int mTimeCnt;
	private int mRefTime;
	private boolean mIsValid;
}
