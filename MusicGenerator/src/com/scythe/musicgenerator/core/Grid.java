package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Degree;

public class Grid
{
	public Grid(ArrayList<Integer> degrees)
	{
		mDegrees = degrees;
	}
	
	public int degree(int index)
	{
		return mDegrees.get(index);
	}
	
	public int degreesCnt()
	{
		return mDegrees.size();
	}
	
	@Override
	public String toString()
	{
		String str = "";
		
		for(int i = 0; i < mDegrees.size(); i++)
		{
			str += Degree.toString(mDegrees.get(i)) + (i == mDegrees.size() - 1 ? "" : " ");
		}
		
		return str;
	}
	
	private ArrayList<Integer> mDegrees;
}
