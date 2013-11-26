package com.scythe.musicgenerator.core;

import java.util.ArrayList;

/**
 * Container class representing a note (or several) with a time information. <br />
 * <br />
 * Here is an example of usage to make a C major perfect chord during a whole note : <br />
 * <br />
 * {@code TimedElement timedElement = new TimedElement(Duration.WHOLE);} <br />
 * {@code timedElement.add(new Note(Note.Name.C));} <br />
 * {@code timedElement.add(new Note(Note.Name.E));} <br />
 * {@code timedElement.add(new Note(Note.Name.G));} <br />
 * <br />
 * {@code // if you wanna listen to it} <br />
 * {@code timedElement.toMidiFile("CMajorPerfectChord.mid");} <br />
 * 
 * @author Scythe
 * @see Note
 * @see TimedElement.Duration
 */
@SuppressWarnings("serial")
public class TimedElement extends ArrayList<Note>
{
	/**
	 * Constructor setting the element not dotted.
	 * @param duration The timed element duration.
	 * @see TimedElement.Duration
	 */
	public TimedElement(int duration)
	{
		mDuration = duration;
		mDotted = false;
	}
	
	/**
	 * Constructor.
	 * @param duration The element duration.
	 * @param dotted Boolean setting if the element is dotted of not.
	 * @see TimedElement.Duration
	 */
	public TimedElement(int duration, boolean dotted)
	{
		mDuration = duration;
		mDotted = dotted;
	}
	
	/**
	 * Gets the element duration.
	 * @return The element duration.
	 * @see TimedElement.Duration
	 */
	public int getDuration()
	{
		return mDuration;
	}
	
	/**
	 * Gets the element duration in times unit.
	 * @return The element duration in times unit.
	 */
	public float getDurationInTimes()
	{
		float duration = Duration.convertInTime(mDuration);
		return mDotted ? duration * 1.5f : duration;
	}
	
	/**
	 * Gets if the element is dotted or not.
	 * @return true if the element is dotted, false otherwise.
	 */
	public boolean isDotted()
	{
		return mDotted;
	}
	
	/**
	 * Write the element in a simple MIDI file.
	 * @param fileName The MIDI file name to write.
	 */
	public void toMidiFile(String fileName)
	{
		Bar bar = new Bar();
		bar.add(this);
		bar.toMidiFile(fileName);
	}
	
	@Override
	public String toString()
	{
		String str = getDurationInTimes() + "[";
		
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + (i == size() - 1 ? "" : ", ");
		}
		
		str += "]";
		return str;
	}
	
	/**
	 * Class of constant values for musical durations.
	 * @author Scythe
	 */
	public static class Duration
	{
		public static final int WHOLE = 0;
		public static final int HALF = 1;
		public static final int QUARTER = 2;
		public static final int EIGHTH = 3;
		public static final int SIXTEENTH = 4;
		public static final int THIRTYSECOND = 5;
		
		/**
		 * Converts the duration is time unit.
		 * @param duration A value of duration from constants.
		 * @return The number of time corresponding to the duration as a float, or 0 if the constant is unknown.
		 */
		public static float convertInTime(int duration)
		{
			switch(duration)
			{
				case WHOLE: return 4;
				case HALF: return 2;
				case QUARTER: return 1;
				case EIGHTH: return 0.5f;
				case SIXTEENTH: return 0.25f;
				case THIRTYSECOND: return 0.125f;
			}
			
			return 0;
		}
		
		/**
		 * Converts a duration constant into time signature number.
		 * @param duration A value of duration from constants.
		 * @return The duration as time signature number, or 0 if the constant is unknown.
		 */
		public static int convertInTimeSignature(int duration)
		{
			switch(duration)
			{
				case WHOLE:	return 1;
				case HALF: return 2;
				case QUARTER: return 4;
				case EIGHTH: return 8;
				case SIXTEENTH: return 16;
				case THIRTYSECOND: return 32;
			}
			
			return 0;
		}
		
		/**
		 * Converts a time signature denominator into a Duration constant.
		 * @param timeSignature a time signature denominator.
		 * @return the corresponding Duration constant, or -1 if the constant is unknown.
		 */
		public static int reverseConvert(int timeSignature)
		{
			switch(timeSignature)
			{
				case 1: return WHOLE;
				case 2: return HALF;
				case 4: return QUARTER;
				case 8: return EIGHTH;
				case 16: return SIXTEENTH;
				case 32: return THIRTYSECOND;
			}
			
			return -1;
		}
		
	}
	
	private int mDuration;
	private boolean mDotted;
}
