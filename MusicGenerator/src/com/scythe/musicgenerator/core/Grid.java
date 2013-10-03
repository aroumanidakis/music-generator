package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Degree;

public class Grid
{
	public Grid(int[] degrees)
	{
		mDegrees = degrees;
	}
	
	public int degree(int index)
	{
		return mDegrees[index];
	}
	
	public int degreesCnt()
	{
		return mDegrees.length;
	}
	
	@Override
	public String toString()
	{
		String str = "";
		
		for(int i = 0; i < mDegrees.length; i++)
		{
			str += Degree.toString(mDegrees[i]) + (i == mDegrees.length - 1 ? "" : " ");
		}
		
		return str;
	}
	
	private int[] mDegrees;
}
