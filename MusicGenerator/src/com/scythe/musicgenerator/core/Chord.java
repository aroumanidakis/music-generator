package com.scythe.musicgenerator.core;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Chord extends TimedElement
{
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
}
