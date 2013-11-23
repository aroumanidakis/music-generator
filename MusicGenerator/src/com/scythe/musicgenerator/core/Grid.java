package com.scythe.musicgenerator.core;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Grid extends ArrayList<Integer>
{
	public static Grid random(int degreeCnt, boolean fixFirstAndLast)
	{
		Grid grid = new Grid();
		
		for(int i = 0; i < degreeCnt; i++)
		{
			if(fixFirstAndLast)
			{
				if(i == 0)
				{
					grid.add(Degree.I);
				}
				else if(i == degreeCnt - 1)
				{
					grid.add((Math.random() < 0.5) ? Degree.I : Degree.V);
				}
				else
				{
					grid.add(Degree.random());
				}
			}
			else
			{
				grid.add(Degree.random());
			}
		}
		
		return grid;
	}
	
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
