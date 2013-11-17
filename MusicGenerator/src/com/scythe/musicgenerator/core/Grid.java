package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.defines.Degree;

@SuppressWarnings("serial")
public class Grid extends ArrayList<Integer>
{
	@Override
	public String toString()
	{
		String str = "";
		
		for(int i = 0; i < size(); i++)
		{
			str += Degree.toString(get(i)) + (i == size() - 1 ? "" : " ");
		}
		
		return str;
	}
}
