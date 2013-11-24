package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.TimedElement.Duration;
import com.scythe.musicgenerator.midi.MidiWriter;

@SuppressWarnings("serial")
public class Bar extends ArrayList<TimedElement>
{
	public Bar()
	{
		mSignature = new TimeSignature("4/4");
	}
	
	public Bar(TimeSignature signature)
	{
		mSignature = signature;
	}
	
	public Bar(String timeSignature)
	{
		mSignature = new TimeSignature(timeSignature);
	}
	
	public boolean isValid()
	{
		return isRhythmSignatureValid() && isNotesValid();
	}
	
	private boolean isRhythmSignatureValid()
	{
		return (mSignature.numerator() == 0 || Duration.convertInTimeSignature(mSignature.denominator()) == 0) ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mSignature.numerator() * Duration.convertInTime(mSignature.denominator());
		
		float realTotalTime = 0;
		for(int i = 0; i < size(); i++)
		{
			realTotalTime += get(i).durationInTime();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	public TimeSignature signature()
	{
		return mSignature;
	}
	
	@Override
	public String toString()
	{
		String str = mSignature.numerator() + "/" + Duration.convertInTimeSignature(mSignature.denominator()) + " ";
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + " ";
		}
		
		return str;
	}
	
	public void toMidiFile(String fileName)
	{
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(this);
		
		MidiWriter midiWriter = new MidiWriter(fileName);
		midiWriter.addTrack(track, "Bar");
		midiWriter.write();
	}
	
	public static Bar generateSimple(TimeSignature signature, DiatonicScale scale, int[] degrees, int division, int strongTimesNotes, int halfStrongTimesNotes, int weakTimesNotes)
	{
		int nbTimes = signature.getNumberOfTimes();
		int signatureType = signature.getType();
		
		int duration;
		boolean dotted;
		int nbElements;
		if(signatureType == TimeSignature.Type.BINARY || signatureType == TimeSignature.Type.TERNARY)
		{
			if(division < 0)
			{
				if(signatureType == TimeSignature.Type.TERNARY && division == -1)
				{
					if(signature.denominator() == Duration.WHOLE)
					{
						System.out.println("The division -1 can not be applied on " + signature + " time signature.");
						return null;
					}
					else
					{
						duration = signature.denominator() - 1;
						dotted = true;
						nbElements = signature.numerator() / 3;
					}
				}
				else
				{
					System.out.println("The division " + division + " can not be applied.");
					return null;
				}
			}
			else
			{
				duration = signature.denominator();
				duration += division;
				
				if(Duration.convertInTime(duration) == 0)
				{
					System.out.println("The divion is two high for this time signature.");
					return null;
				}
				
				dotted = false;
				
				nbElements = signature.numerator();
				
				for(int i = 0; i < division; i++)
				{
					nbElements *= 2;
				}
			}
		}
		else
		{
			System.out.println(signature + " not supported yet in this generation function.");
			return null;
		}
		
		if(nbElements % degrees.length != 0)
		{
			System.out.println(degrees.length + " degrees can not be placed in " + nbElements + " elements.");
			return null;
		}
		
		int nbElementsPerDegree = nbElements / degrees.length;
		int currentDegree = 0;
		int degreeElementsAdded = 0;
		
		int nbElementsPerTime = nbElements / nbTimes;
		int currentTime = 1;
		int timeElementsAdded = 0;
		
		Bar bar = new Bar(signature);
		for(int noteIndex = 0; noteIndex < nbElements; noteIndex++)
		{
			int dynamics = Note.Dynamics.MEZZOPIANO;
			int notesMask = weakTimesNotes;
			if(timeElementsAdded == 0)
			{
				if(nbTimes == 2)
				{
					if(currentTime == 1)
					{
						dynamics = Note.Dynamics.FORTE;
						notesMask = strongTimesNotes;
					}
					else
					{
						dynamics = Note.Dynamics.MEZZOFORTE;
						notesMask = halfStrongTimesNotes;
					}
				}
				else if(nbTimes == 3)
				{
					if(currentTime == 1)
					{
						dynamics = Note.Dynamics.FORTE;
						notesMask = strongTimesNotes;
					}
				}
				else if(nbTimes == 4)
				{
					if(currentTime == 1)
					{
						dynamics = Note.Dynamics.FORTE;
						notesMask = strongTimesNotes;
					}
					else if(currentTime == 3)
					{
						dynamics = Note.Dynamics.MEZZOFORTE;
						notesMask = halfStrongTimesNotes;
					}
				}
			}
			
			int degree = degrees[currentDegree];
			
			Chord chord = Chord.generate(duration, dotted, scale, degree, notesMask);
			chord.dynamics(dynamics);
			bar.add(chord);
			
			timeElementsAdded++;
			if(timeElementsAdded == nbElementsPerTime)
			{
				currentTime++;
				timeElementsAdded = 0;
			}
			
			degreeElementsAdded++;
			if(degreeElementsAdded == nbElementsPerDegree)
			{
				currentDegree++;
				degreeElementsAdded = 0;
			}
		}
		
		return bar;
	}
	
	public static Bar generateArpeggio()
	{
		return null;
	}
	
	private TimeSignature mSignature;
}
