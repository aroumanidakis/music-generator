package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.TimedElement.Duration;
import com.scythe.musicgenerator.midi.MidiWriter;

/**
 * Container class representing a bar.
 * @author Scythe
 *
 */
@SuppressWarnings("serial")
public class Bar extends ArrayList<TimedElement>
{
	/**
	 * Default constructor. Makes a 4/4 bar.
	 */
	public Bar()
	{
		mSignature = new TimeSignature("4/4");
	}
	
	/**
	 * Constructor.
	 * @param timeSignature The time signature as a String. Examples: "4/4, "9/8", etc...
	 */
	public Bar(String timeSignature)
	{
		mSignature = new TimeSignature(timeSignature);
	}
	
	/**
	 * Constructor.
	 * @param signature A TimeSignature instantiated.
	 */
	public Bar(TimeSignature signature)
	{
		mSignature = signature;
	}
	
	/**
	 * Checks if the bar is valid, including time signature validation and total note times validations.
	 * @return true if the bar is valid, false otherwise.
	 */
	public boolean isValid()
	{
		return isTimeSignatureValid() && isNotesValid();
	}
	
	/**
	 * Gets the time signature of the bar.
	 * @return The time signature.
	 */
	public TimeSignature getSignature()
	{
		return mSignature;
	}
	
	/**
	 * Writes the bar in a simple MIDI file.
	 * @param fileName The MIDI file name to write.
	 */
	public void toMidiFile(String fileName)
	{
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(this);
		
		MidiWriter midiWriter = new MidiWriter(fileName);
		midiWriter.addTrack(track, "Bar");
		midiWriter.write();
	}
	
	@Override
	public String toString()
	{
		String str = mSignature.getNumerator() + "/" + Duration.convertInTimeSignature(mSignature.getDenominator()) + " ";
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + " ";
		}
		
		return str;
	}
	
	/**
	 * Generates a fully parametrized bar of accompaniment.
	 * @param signature The bar time signature.
	 * @param scale The scale used to build the bar.
	 * @param degrees the degrees to put in the bar.
	 * @param division sets the duration of used notes. Passing 0 make the time signature, 1 twice the number of notes, on so on... -1 can be passed for ternary bars.
	 * @param strongTimesNotes Notes to be put on strong times. Works as flag, example: Chord.THIRD | Chord.FIFTH | Chord.SEVENTH. Passing 0 put only the fundamental.
	 * @param halfStrongTimesNotes Notes to be put on half strong times. Works as flag, example: Chord.THIRD | Chord.FIFTH. Passing 0 put only the fundamental.
	 * @param weakTimesNotes Notes to be put on weak times. Works as flag, example: Chord.THIRD. Passing 0 put only the fundamental.
	 * @return The generated bar is it could be generated, null otherwise (An error message is printed for each problem).
	 */
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
					if(signature.getDenominator() == Duration.WHOLE)
					{
						System.out.println("The division -1 can not be applied on " + signature + " time signature.");
						return null;
					}
					else
					{
						duration = signature.getDenominator() - 1;
						dotted = true;
						nbElements = signature.getNumerator() / 3;
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
				duration = signature.getDenominator();
				duration += division;
				
				if(Duration.convertInTime(duration) == 0)
				{
					System.out.println("The divion is two high for this time signature.");
					return null;
				}
				
				dotted = false;
				
				nbElements = signature.getNumerator();
				
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
			chord.setDynamics(dynamics);
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
	
	/**
	 * Generates a bar containing an arpeggio.
	 * @param signature The time signature.
	 * @param scale The scale to use to build the bar.
	 * @param degree The degree taken for arpeggio generation.
	 * @return The generated bar or null if the time signature is not supported (3/*, 6/*, 9/* and 12/*).
	 */
	public static Bar generateArpeggio(TimeSignature signature, DiatonicScale scale, int degree)
	{
		Chord chord = new Chord(Duration.QUARTER);
		chord.add(scale.get(degree));
		
		int[] intervals = new int[6];
		
		for(int i = 0; i < 2; i++)
		{
			intervals[3 * i] = Interval.Name.THIRD;
			intervals[(3 * i) + 1] = Interval.Name.FIFTH;
			intervals[(3 * i) + 2] = Interval.Name.OCTAVE;
		}
		
		for(int interval : intervals)
		{
			Note note = new Note();
			scale.getNoteAtUpperInterval(degree, interval, note);
			chord.add(note);
		}
		
		Bar bar = new Bar(signature);
		
		if(signature.getNumerator() == 2)
		{
			for(int index : new int[]{0, 2, 3, 2})
			{
				TimedElement te = new TimedElement(signature.getDenominator() + 1);
				te.add(chord.get(index));
				bar.add(te);
			}
		}
		else if(signature.getNumerator() == 3)
		{
			System.out.println(signature + " not supported yet.");
			return null;
		}
		else if(signature.getNumerator() == 4)
		{
			for(int index : new int[]{0, 2, 3, 4, 5, 4, 3, 2})
			{
				TimedElement te = new TimedElement(signature.getDenominator() + 1);
				te.add(chord.get(index));
				bar.add(te);
			}
		}
		else if(signature.getNumerator() == 6)
		{
			for(int index : new int[]{0, 2, 3, 4, 3, 2})
			{
				TimedElement te = new TimedElement(signature.getDenominator());
				te.add(chord.get(index));
				bar.add(te);
			}
		}
		else if(signature.getNumerator() == 9)
		{
			System.out.println(signature + " not supported yet.");
			return null;
		}
		else if(signature.getNumerator() == 12)
		{
			System.out.println(signature + " not supported yet.");
			return null;
		}
		
		return bar;
	}
	
	private boolean isTimeSignatureValid()
	{
		return (mSignature.getNumerator() == 0 || Duration.convertInTimeSignature(mSignature.getDenominator()) == 0) ? false : true;
	}
	
	private boolean isNotesValid()
	{
		float expectedTotalTime = mSignature.getNumerator() * Duration.convertInTime(mSignature.getDenominator());
		
		float realTotalTime = 0;
		for(int i = 0; i < size(); i++)
		{
			realTotalTime += get(i).getDurationInTimes();
		}
		
		return expectedTotalTime == realTotalTime;
	}
	
	private TimeSignature mSignature;
}
