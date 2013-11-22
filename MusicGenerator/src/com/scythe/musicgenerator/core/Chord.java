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
	}
	
	public Chord(int duration)
	{
		super(duration);
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

	public boolean reverse(int inversion)
	{
		if(inversion <= 0)
		{
			return false;
		}
		
		for(int i = 0; i <= inversion; i++)
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
		
		return true;
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
		
		if((notes & THIRD) == THIRD)
		{
			System.out.println("Adding THIRD");
			
			Note note = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.THIRD, note);
			chord.add(note);
		}
		
		if((notes & FIFTH) == FIFTH)
		{
			System.out.println("Adding FIFTH");
			
			Note note = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, note);
			chord.add(note);
		}
		
		if((notes & SEVENTH) == SEVENTH)
		{
			System.out.println("Adding SEVENTH");
			
			Note note = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.SEVENTH, note);
			chord.add(note);
		}
		
		if((notes & OCTAVE) == OCTAVE)
		{
			System.out.println("Adding OCTAVE");
			
			Note note = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.OCTAVE, note);
			chord.add(note);
		}
		
		return chord;
	}
}
