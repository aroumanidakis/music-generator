package com.scythe.musicgenerator.core;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Chord extends TimedElement
{
	public static final int THIRD = 0x01;
	public static final int FIFTH = 0x02;
	public static final int SEVENTH = 0x04;
	public static final int OCTAVE = 0x08;
	
	public Chord(int duration, boolean dotted)
	{
		super(duration, dotted);
		mFundamentalIndex = 0;
	}
	
	public Chord(int duration)
	{
		super(duration);
		mFundamentalIndex = 0;
	}
	
	public Note fundamental()
	{
		return isEmpty() ? null : get(mFundamentalIndex);
	}
	
	public boolean transpose(int transposition)
	{
		ArrayList<Note> notes = new ArrayList<Note>(this);
		
		for(Note note : notes)
		{
			if(!note.octave(note.octave() + transposition))
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

	public boolean reverseByBottom(int inversion)
	{
		if(size() < 2 || inversion <= 0)
		{
			return false;
		}
		
		Note fundamental = fundamental();
		
		for(int i = 0; i < inversion; i++)
		{
			Note firstNote = get(0);
			if(!firstNote.octave(firstNote.octave() + 1))
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
	
	public boolean reverseByTop(int inversion)
	{
		if(size() < 2 || inversion <= 0)
		{
			return false;
		}
		
		Note fundamental = fundamental();
		
		for(int i = 0; i < inversion; i++)
		{
			Note lastNote = get(size() - 1);
			if(!lastNote.octave(lastNote.octave() - 1))
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
	
	public void dynamics(int dynamics)
	{
		for(Note note : this)
		{
			note.dynamics(dynamics);
		}
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
			if(get(i).name() > get(i + 1).name())
			{
				get(i + 1).octave(get(i).octave() + 1);
			}
			else if(get(i).octave() != get(i + 1).octave())
			{
				get(i + 1).octave(get(i).octave());
			}
		}
	}
	
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
