package com.scythe.musicgenerator.core;

import java.util.ArrayList;

/**
 * Class intended to manage a chord.
 * @author Scythe
 */
@SuppressWarnings("serial")
public class Chord extends TimedElement
{
	public static final int THIRD = 0x01;
	public static final int FIFTH = 0x02;
	public static final int SEVENTH = 0x04;
	public static final int OCTAVE = 0x08;
	
	/**
	 * Constructor.
	 * @param duration The chord duration.
	 * @param dotted Boolean setting if the chord is dotted or not.
	 * @see TimedElement.Duration
	 */
	public Chord(int duration, boolean dotted)
	{
		super(duration, dotted);
		mFundamentalIndex = 0;
	}
	
	/**
	 * Constructor setting dotted to false.
	 * @param duration duration The chord duration.
	 * @see TimedElement.Duration
	 */
	public Chord(int duration)
	{
		super(duration);
		mFundamentalIndex = 0;
	}
	
	/**
	 * Gets the fundamental note of the Chord.
	 * @return The fundamental note or null if no note has been set.
	 */
	public Note getFundamental()
	{
		return isEmpty() ? null : get(mFundamentalIndex);
	}
	
	/**
	 * Transposes the chord.
	 * @param transposition The number of octave the chord will be transpose of, can be negative or positive.
	 * @return true if the chord has been transposed, false if not, due to MIDI specification.
	 */
	public boolean transpose(int transposition)
	{
		ArrayList<Note> notes = new ArrayList<Note>(this);
		
		for(Note note : notes)
		{
			if(!note.octave(note.getOctave() + transposition))
			{
				return false;
			}
		}
		
		for(int i = 0; i < size(); i++)
		{
			set(i, notes.get(i));
		}
		
		return true;
	}

	/**
	 * Reverses the chord by placing the bass on the top of the chord.
	 * @param inversion The number of iterations.
	 * @return true if the chord has been reversed, false if not, due to MIDI specification.
	 */
	public boolean reverseByBottom(int inversion)
	{
		if(size() < 2 || inversion <= 0)
		{
			return false;
		}
		
		Note fundamental = getFundamental();
		
		for(int i = 0; i < inversion; i++)
		{
			Note firstNote = get(0);
			if(!firstNote.octave(firstNote.getOctave() + 1))
			{
				System.out.println("The inversion can not be done. The chord would be too high.");
				return false;
			}
			
			for(int j = 0; j < size() - 1; j++)
			{
				set(j, get(j + 1));
			}
			
			set(size() - 1, firstNote);
		}
		
		updateFundementalIndex(fundamental);
		
		return true;
	}
	
	/**
	 * Reverses the chord by placing the top note on the bass of the chord.
	 * @param inversion The number of iterations.
	 * @return true if the chord has been reversed, false if not, due to MIDI specification.
	 */
	public boolean reverseByTop(int inversion)
	{
		if(size() < 2 || inversion <= 0)
		{
			return false;
		}
		
		Note fundamental = getFundamental();
		
		for(int i = 0; i < inversion; i++)
		{
			Note lastNote = get(size() - 1);
			if(!lastNote.octave(lastNote.getOctave() - 1))
			{
				System.out.println("The inversion can not be done. The chord would be too low.");
				return false;
			}
			
			for(int j = size() - 1; j > 0; j--)
			{
				set(j, get(j - 1));
			}
			
			set(0, lastNote);
		}
		
		updateFundementalIndex(fundamental);
		
		return true;
	}
	
	/**
	 * Sets the dynamics of the chord.
	 * @param dynamics The new dynamics.
	 */
	public void setDynamics(int dynamics)
	{
		for(Note note : this)
		{
			note.dynamics(dynamics);
		}
	}
	
	/**
	 * Generates a fully parametrized chord.
	 * @param duration The chord duration
	 * @param dotted Boolean setting if the chord is dotted or not.
	 * @param scale A diatonic scale from which one the chord will be build.
	 * @param degree The degree of the chord in the scale.
	 * @param notes Notes to be put in the chord (fundamental is always put). Works as flags : for a power chord, pass Chord.FIFTH | Chord.OCTAVE. Passing 0 puts only fundamental.
	 * @return The generated Chord.
	 * @see TimedElement.Duration
	 * @see DiatonicScale
	 * @see Degree
	 */
	public static Chord generate(int duration, boolean dotted, DiatonicScale scale, int degree, int notes)
	{
		Chord chord = new Chord(duration, dotted);
		
		Note fundamental = scale.get(degree);
		chord.add(fundamental);
		
		for(int note : new int[]{THIRD, FIFTH, SEVENTH, OCTAVE})
		{
			if((notes & note) == note)
			{
				Note n = new Note();
				scale.getNoteAtUpperInterval(degree, toStdInterval(note), n);
				chord.add(n);
			}
		}
		
		return chord;
	}
	
	@Override
	public boolean add(Note element)
	{
		boolean changed = super.add(new Note(element));
		
		if(changed)
		{	
			checkOctaves();
		}
		
		return changed;
	}
	
	private void checkOctaves()
	{
		for(int i = 0; i < size() - 1; i++)
		{
			if(get(i).getName() > get(i + 1).getName())
			{
				get(i + 1).octave(get(i).getOctave() + 1);
			}
			else if(get(i).getOctave() != get(i + 1).getOctave())
			{
				get(i + 1).octave(get(i).getOctave());
			}
		}
	}
	
	private static int toStdInterval(int chordInterval)
	{
		switch(chordInterval)
		{
			case THIRD: return Interval.Name.THIRD;
			case FIFTH: return Interval.Name.FIFTH;
			case SEVENTH: return Interval.Name.SEVENTH;
			case OCTAVE: return Interval.Name.OCTAVE;
		}
		
		return Interval.Name.OCTAVE;
	}
	
	private void updateFundementalIndex(Note fundamental)
	{
		for(int i = 0; i < size(); i++)
		{
			if(get(i).equals(fundamental))
			{
				mFundamentalIndex = i;
				break;
			}
		}
	}
	
	private int mFundamentalIndex;
}
