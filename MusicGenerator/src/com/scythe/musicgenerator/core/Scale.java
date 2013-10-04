package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Mode;

public class Scale
{
	public Scale(int mode, Note[] notes)
	{
		mMode = mode;
		mNotes = notes;
		mAccidental = Note.Accidental.NONE;
		
		for(Note note : mNotes)
		{
			if(mAccidental == Note.Accidental.NONE)
			{
				if(note.accidental() == Note.Accidental.SHARP)
				{
					mAccidental = Note.Accidental.SHARP;
					break;
				}
				else if(note.accidental() == Note.Accidental.FLAT)
				{
					mAccidental = Note.Accidental.FLAT;
					break;
				}
			}
		}
	}
	
	public int mode()
	{
		return mMode;
	}
	
	public Note note(int index)
	{
		return mNotes[index];
	}
	
	public int noteCnt()
	{
		return mNotes.length;
	}
	
	public int accidental()
	{
		return mAccidental;
	}
	
	public boolean isIn(Note note, boolean normalized)
	{
		for(int i = 0; i < mNotes.length; i++)
		{
			if(normalized)
			{
				if(mNotes[i].equalsNormalized(note))
				{
					return true;
				}
			}
			else
			{
				if(mNotes[i].equals(note))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasStrangeNote()
	{
		for(int i = 0; i < mNotes.length; i++)
		{
			if(mNotes[i].name() == Note.Name.C && mNotes[i].accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(mNotes[i].name() == Note.Name.E && mNotes[i].accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
			else if(mNotes[i].name() == Note.Name.F && mNotes[i].accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(mNotes[i].name() == Note.Name.B && mNotes[i].accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		String str = mNotes[0] + " " + Mode.toString(mMode) + " (";
		
		for(int i = 0; i < mNotes.length; i++)
		{
			str += mNotes[i] + (i == mNotes.length - 1 ? ")" : " ");
		}
		
		return str;
	}
	
	private int mMode;
	private Note[] mNotes;
	private int mAccidental;
}
