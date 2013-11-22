package com.scythe.musicgenerator.core;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Chord extends TimedElement
{
	public static final int NOTE_CNT_MAX = 5;
	
	public Chord(int duration, boolean dotted)
	{
		super(duration, dotted);
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

	public void reverse(int inversion)
	{
		if(size() < 3 || (size() < 4 && inversion == Inversion.THIRD) || (size() < 5 && inversion == Inversion.FOURTH))
		{
			System.out.println("The number of notes (" + size() + ") of chord is too smal to process " + Inversion.toString(inversion));
			return;
		}
		
		for(int i = 0; i <= inversion; i++)
		{
			Note firstNote = get(0);
			for(int j = 0; j < size() - 1; j++)
			{
				set(j, get(j + 1));
			}
			
			firstNote.octave(firstNote.octave() + 1);
			set(size() - 1, firstNote);
		}
	}
	
	@Override
	public boolean add(Note element)
	{
		if(size() < NOTE_CNT_MAX)
		{
			boolean changed = super.add(new Note(element));
			if(changed)
			{
				checkOctaves();
				return true;
			}
		}
		
		return false;
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
	
	public static class Inversion
	{
		public static final int COUNT = 4;
		
		public static final int FIRST = 0;
		public static final int SECOND = 1;
		public static final int THIRD = 2;
		public static final int FOURTH = 3;
		
		public static String toString(int inversion)
		{
			switch(inversion)
			{
				case FIRST: return "First inversion";
				case SECOND: return "Second inversion";
				case THIRD: return "Third inversion";
				case FOURTH: return "Fourth inversion";
			}
			
			return "Unknown inversion";
		}
	}
}
