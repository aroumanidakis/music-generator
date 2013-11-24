package com.scythe.musicgenerator.factories;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimeSignature;
import com.scythe.musicgenerator.core.TimedElement;
import com.scythe.musicgenerator.core.TimedElement.Duration;

public class BarFactory
{
	public static class Bass
	{
		public static Bar generateSimple(TimeSignature signature, DiatonicScale scale, int[] degrees)
		{
			int numberOfTimes = signature.getNumberOfTimes();
			if(numberOfTimes == -1)
			{
				System.out.println(signature + " not supported yet.");
				return null;
			}
			
			if(degrees.length > signature.numerator())
			{
				System.out.println(degrees.length + " degrees can not be inserted in a " + signature + " bar.");
				return null;
			}
			
			if(signature.numerator() % degrees.length != 0)
			{
				System.out.println("The number of degrees (" + degrees.length + ") is not compatible with the bar structure (" + signature + ")");
				return null;
			}
			
			int notesPerDegree = signature.numerator() / degrees.length;
			int currentDegreeIndex = 0;
			int degreeNotesAdded = 0;
			
			int notesPerTime = signature.numerator() / numberOfTimes;
			int currentTime = 1;
			int timeNotesAdded = 0;
			
			Bar bar = new Bar(signature);
			for(int noteIndex = 0; noteIndex < signature.numerator(); noteIndex++)
			{
				int velocity = Note.Dynamics.MEZZOPIANO;
				if(timeNotesAdded == 0)
				{
					if(currentTime == 1)
					{
						velocity = Note.Dynamics.FORTE;
					}
					else if(currentTime == 3 && numberOfTimes == 4)
					{
						velocity = Note.Dynamics.MEZZOFORTE;
					}
				}
				
				int degree = degrees[currentDegreeIndex];
				
				Note fundamental = new Note(scale.get(degree));
				fundamental.dynamics(velocity);
				fundamental.octave(1);
				
				TimedElement timedElement = new TimedElement(signature.denominator());
				timedElement.add(fundamental);
				
				bar.add(timedElement);
				
				timeNotesAdded++;
				if(timeNotesAdded == notesPerTime)
				{
					currentTime++;
					timeNotesAdded = 0;
				}
				
				degreeNotesAdded++;
				if(degreeNotesAdded == notesPerDegree)
				{
					currentDegreeIndex++;
					degreeNotesAdded = 0;
				}
			}
			
			return bar;
		}
	}
	
	public static class Melody
	{
		public static Bar generateRhythm(TimeSignature signature)
		{
			ArrayList<TimedElementToCut> timedElementsToCut = createBeginningTimedElements(signature);
			
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
			
			Bar bar = new Bar(signature);
			for(TimedElementToCut element : timedElementsToCut)
			{
				bar.add(element.timedElement());
			}
			
			return bar;
		}
		
		private static ArrayList<TimedElementToCut> createBeginningTimedElements(TimeSignature signature)
		{
			/*
			float totalTime = signature.numerator() * Duration.convertInTime(signature.denominator());
			
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
			
			*/
			int numberOfTimes = signature.getNumberOfTimes();
			if(numberOfTimes == -1)
			{
				System.out.println(signature + " not supported yet.");
				return null;
			}
			
			ArrayList<TimedElementToCut> timedElements = new ArrayList<TimedElementToCut>();
			
			switch(signature.getType())
			{
				case TimeSignature.Type.BINARY:
				{
					for(int i = 0; i < signature.numerator(); i++)
					{
						timedElements.add(new TimedElementToCut(new TimedElement(signature.denominator()), cuttingChanceCoeff));
					}
					
					break;
				}
				case TimeSignature.Type.TERNARY:
				{
					for(int i = 0; i < signature.numerator() / 3; i++)
					{
						timedElements.add(new TimedElementToCut(new TimedElement(signature.denominator(), true), cuttingChanceCoeff));
					}
					
					break;
				}
			}
			
			return timedElements;
		}
		
		private static float cuttingChanceCoeff = 0.65f;
		
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
				
				if(mTimedElement.duration() != Duration.SIXTEENTH)
				{
					if(Math.random() < mCuttingChance)
					{
						if(mTimedElement.dotted())
						{
							timedElement1 = new TimedElement(mTimedElement.duration());
							timedElement2 = new TimedElement(mTimedElement.duration() + 1);
						}
						else
						{
							if(mTimedElement.duration() == Duration.HALF)
							{
								timedElement1 = new TimedElement(mTimedElement.duration() + 1);
								timedElement2 = new TimedElement(timedElement1.duration(), timedElement1.dotted());
							}
							else
							{
								if(Math.random() < 0.4)
								{	
									timedElement1 = new TimedElement(mTimedElement.duration() + 1);
									timedElement2 = new TimedElement(timedElement1.duration(), timedElement1.dotted());
								}
								else
								{
									timedElement1 = new TimedElement(mTimedElement.duration() + 1, true);
									timedElement2 = new TimedElement(mTimedElement.duration() + 2);
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
}
