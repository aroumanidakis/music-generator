package com.scythe.musicgenerator.factories;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.BarSignature;
import com.scythe.musicgenerator.core.TimedElement;
import com.scythe.musicgenerator.defines.Duration;

public class BarFactory
{
	public static Bar generateRhythm(BarSignature signature)
	{
		ArrayList<TimedElementToCut> timedElementsToCut = createBeginningTimedElements(signature.timeCnt() * Duration.convertInTime(signature.refTime()));
		
		boolean newCuts = true;
		while(newCuts)
		{
			newCuts = false;
			for(int i = 0; i < timedElementsToCut.size(); i++)
			{
				if(timedElementsToCut.get(i).cuttingChance() != 0)
				{
					ArrayList<TimedElementToCut> cuts = timedElementsToCut.get(i).randomCutting();
					
					timedElementsToCut.remove(i);
					timedElementsToCut.addAll(i, cuts);
					
					newCuts = true;
					break;
				}
			}
		}
		
		ArrayList<TimedElement> timedElements = new ArrayList<TimedElement>();
		for(TimedElementToCut element : timedElementsToCut)
		{
			timedElements.add(element.timedElement());
		}
		
		return new Bar(signature, timedElements);
	}
	
	private static ArrayList<TimedElementToCut> createBeginningTimedElements(float totalTime)
	{
		ArrayList<TimedElementToCut> timedElements = new ArrayList<TimedElementToCut>();
		
		float timeToAdd = totalTime;
		while(timeToAdd > 0)
		{
			for(int duration = 0; duration < Duration.COUNT; duration++)
			{
				if((Duration.convertInTime(duration) * 1.5) <= timeToAdd)
				{
					timedElements.add(new TimedElementToCut(new TimedElement(duration, true), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration) * 1.5;
					break;
				}
				
				if(Duration.convertInTime(duration) <= timeToAdd)
				{
					timedElements.add(new TimedElementToCut(new TimedElement(duration, false), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration);
					break;
				}
			}
		}
		
		for(int i = 0; i < timedElements.size(); i++)
		{
			int randomIndex = (int)(Math.random() * timedElements.size());
			
			TimedElementToCut tmp = timedElements.get(i);
			timedElements.set(i, timedElements.get(randomIndex));
			timedElements.set(randomIndex, tmp);
		}
		
		return timedElements;
	}
	
	private static float cuttingChanceCoeff = 0.85f;
	
	private static class TimedElementToCut
	{
		public TimedElementToCut(TimedElement timedElement, float cuttingChance)
		{
			mTimedElement = timedElement;
			mCuttingChance = cuttingChance;
		}
		
		public ArrayList<TimedElementToCut> randomCutting()
		{
			ArrayList<TimedElementToCut> cuts = new ArrayList<TimedElementToCut>();
			TimedElement timedElement1, timedElement2;
			
			if(mTimedElement.duration() != Duration.QUARTER)
			{
				if(Math.random() < mCuttingChance)
				{
					if(mTimedElement.dotted())
					{
						timedElement1 = new TimedElement(mTimedElement.duration(), false);
						timedElement2 = new TimedElement(mTimedElement.duration() + 1, false);
					}
					else
					{
						if(mTimedElement.duration() == Duration.HALF)
						{
							timedElement1 = new TimedElement(mTimedElement.duration() + 1, false);
							timedElement2 = new TimedElement(timedElement1.duration(), timedElement1.dotted());
						}
						else
						{
							if(Math.random() < 0.4)
							{	
								timedElement1 = new TimedElement(mTimedElement.duration() + 1, false);
								timedElement2 = new TimedElement(timedElement1.duration(), timedElement1.dotted());
							}
							else
							{
								timedElement1 = new TimedElement(mTimedElement.duration() + 1, true);
								timedElement2 = new TimedElement(mTimedElement.duration() + 2, false);
							}
						}
					}
					
					cuts.add(new TimedElementToCut(timedElement1, mCuttingChance * cuttingChanceCoeff));
					cuts.add(new TimedElementToCut(timedElement2, mCuttingChance * cuttingChanceCoeff));
				}
			}
			
			if(cuts.size() == 0)
			{
				cuts.add(new TimedElementToCut(mTimedElement, 0));
			}
			
			for(int i = 0; i < cuts.size(); i++)
			{
				int randomIndex = (int)(Math.random() * cuts.size());
				
				TimedElementToCut tmp = cuts.get(i);
				cuts.set(i, cuts.get(randomIndex));
				cuts.set(randomIndex, tmp);
			}
			
			return cuts;
		}
		
		public TimedElement timedElement()
		{
			return mTimedElement;
		}
		
		public float cuttingChance()
		{
			return mCuttingChance;
		}
		
		private TimedElement mTimedElement;
		private float mCuttingChance;
	}
}
