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
		return (mSignature.numerator() == 0 || mSignature.denominator() == 0 || Duration.convertInTimeSignature(mSignature.denominator()) == 0) ? false : true;
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
	
	public static Bar generateAccompanimentSimpleChords(TimeSignature signature, DiatonicScale scale, int[] degrees, boolean doubleTime, int strongTimesNotes, int halfStrongTimesNotes, int weakTimesNotes)
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
			int dynamics = Note.Dynamics.MEZZOPIANO;
			int notesMask = weakTimesNotes;
			if(timeNotesAdded == 0)
			{
				if(numberOfTimes == 2)
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
				else if(numberOfTimes == 3)
				{
					if(currentTime == 1)
					{
						dynamics = Note.Dynamics.FORTE;
						notesMask = strongTimesNotes;
					}
				}
				else if(numberOfTimes == 4)
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
			
			int degree = degrees[currentDegreeIndex];
			
			if(doubleTime)
			{
				for(int i = 0; i < 2; i++)
				{
					Chord chord = Chord.generate(signature.denominator() + 1, false, scale, degree, (i == 0) ? notesMask : weakTimesNotes);
					chord.dynamics(dynamics);
					
					bar.add(chord);
				}
			}
			else
			{
				Chord chord = Chord.generate(signature.denominator(), false, scale, degree, notesMask);
				chord.dynamics(dynamics);
				
				bar.add(chord);
			}
			
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
	
	public static Bar generateArpeggio()
	{
		return null;
	}
	
	private TimeSignature mSignature;
}
