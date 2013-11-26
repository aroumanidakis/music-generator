package com.scythe.musicgenerator.core;

import java.util.ArrayList;

/**
 * Container class representing a chord grid. <br />
 * <br />
 * Here is an example of usage: <br />
 * <br/ >
 * {@code Grid grid = new Grid();} <br />
 * {@code grid.add(Degree.I);} <br />
 * {@code grid.add(Degree.I);} <br />
 * {@code grid.add(Degree.IV);} <br />
 * {@code grid.add(Degree.V);} <br />
 *  
 * @author Scythe
 * @see Degree
 */
@SuppressWarnings("serial")
public class Grid extends ArrayList<Integer>
{
	/**
	 * Gets a random instance of Grid.
	 * @param degreeCnt The number of degrees.
	 * @param fixFirstAndLast Boolean to fix first degree to I and last to I or V.
	 * @return A random Grid.
	 */
	public static Grid getRandom(int degreeCnt, boolean fixFirstAndLast)
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
					grid.add(Degree.getRandom());
				}
			}
			else
			{
				grid.add(Degree.getRandom());
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
