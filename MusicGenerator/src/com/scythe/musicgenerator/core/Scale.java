package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Note.Accidental;
import com.scythe.musicgenerator.core.Note.Name;
import com.scythe.musicgenerator.defines.Mode;

public class Scale
{
	public Scale(Note tonic, int mode)
	{
		mTonic = tonic;
		mMode = mode;
		mIsValid = true;
		
		int[] intervals = shift(mMode);
		
		mNotes = new ArrayList<Note>();
		mNotes.add(mTonic);
		
		for(int i = 1; i < 7; i++)
		{
			Note nextNote = getHalfToneUpperNote(mNotes.get(i - 1), intervals[i - 1]);
			
			if(nextNote == null)
			{
				mIsValid = false;
				return;
			}
			
			mNotes.add(nextNote);
		}
		
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
	
	public Note tonic()
	{
		return mTonic;
	}
	
	public int mode()
	{
		return mMode;
	}
	
	public Note note(int index)
	{
		return mNotes.get(index);
	}
	
	public int noteCnt()
	{
		return mNotes.size();
	}
	
	public int accidental()
	{
		return mAccidental;
	}
	
	public boolean isValid()
	{
		return mIsValid;
	}
	
	public boolean isIn(Note note, boolean normalized)
	{
		for(int i = 0; i < mNotes.size(); i++)
		{
			if(normalized)
			{
				if(mNotes.get(i).equalsNormalized(note))
				{
					return true;
				}
			}
			else
			{
				if(mNotes.get(i).equals(note))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasStrangeNote()
	{
		for(int i = 0; i < mNotes.size(); i++)
		{
			if(mNotes.get(i).name() == Note.Name.C && mNotes.get(i).accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(mNotes.get(i).name() == Note.Name.E && mNotes.get(i).accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
			else if(mNotes.get(i).name() == Note.Name.F && mNotes.get(i).accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(mNotes.get(i).name() == Note.Name.B && mNotes.get(i).accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		String str = mNotes.get(0) + " " + Mode.toString(mMode) + " (";
		
		for(int i = 0; i < mNotes.size(); i++)
		{
			str += mNotes.get(i) + (i == mNotes.size() - 1 ? ")" : " ");
		}
		
		if(!mIsValid)
		{
			str += " (not valid)";
		}
		else if(hasStrangeNote())
		{
			str += " (strange)";
		}

		return str;
	}
	
	private int[] shift(int shift)
	{
		int[] newIntervals = new int[IONIAN_INTERVALS.length];
		
		for(int i = 0; i < newIntervals.length; i++)
		{
			newIntervals[(i + shift) % 7] = IONIAN_INTERVALS[i];
		}
		
		return newIntervals;
	}
	
	private Note getHalfToneUpperNote(Note refNote, int halfTones)
	{
		Note newNote = new Note((refNote.name() + 1) % Name.NB_NAMES, refNote.accidental());
		
		int halfToneDiffenrence = getHalfToneDifference(refNote, newNote);
		
		if(halfToneDiffenrence == halfTones - 1)
		{
			if(newNote.accidental() == Accidental.FLAT)
			{
				newNote.accidental(Accidental.NONE);
			}
			else if(newNote.accidental() == Accidental.NONE)
			{
				newNote.accidental(Accidental.SHARP);
			}
			else
			{
				return null;
			}
		}
		else if(halfToneDiffenrence == halfTones + 1)
		{
			if(newNote.accidental() == Accidental.SHARP)
			{
				newNote.accidental(Accidental.NONE);
			}
			else if(newNote.accidental() == Accidental.NONE)
			{
				newNote.accidental(Accidental.FLAT);
			}
			else
			{
				return null;
			}
		}
		
		return newNote;
	}
	
	private int getHalfToneDifference(Note lowNote, Note highNote)
	{	
		Note[] notes = new Note[2];
		notes[0] = new Note(lowNote);
		notes[1] = new Note(highNote);
		
		for(int i = 0; i < 2; i++)
		{
			if(notes[i].accidental() == Accidental.SHARP)
			{
				if(notes[i].name() == Name.B)
				{
					notes[i].name(Name.C);
					notes[i].accidental(Accidental.NONE);
				}
				else if(notes[i].name() == Name.E)
				{
					notes[i].name(Name.F);
					notes[i].accidental(Accidental.NONE);
				}
				else
				{
					notes[i].name((notes[i].name() + 1) % Name.NB_NAMES);
					notes[i].accidental(Accidental.FLAT);
				}
			}
			
			if(notes[i].accidental() == Accidental.FLAT)
			{
				if(notes[i].name() == Name.F)
				{
					notes[i].name(Name.E);
					notes[i].accidental(Accidental.NONE);
				}
				else if(notes[i].name() == Name.C)
				{
					notes[i].name(Name.B);
					notes[i].accidental(Accidental.NONE);
				}
			}
		}
		
		int halfToneCnt = 0;
		while(notes[0].name() != notes[1].name() || notes[0].accidental() != notes[1].accidental())
		{	
			if(notes[0].name() == Name.E && notes[0].accidental() == Accidental.NONE)
			{
				notes[0].name(Name.F);
			}
			else if(notes[0].name() == Name.B && notes[0].accidental() == Accidental.NONE)
			{
				notes[0].name(Name.C);
			}
			else if(notes[0].accidental() == Accidental.FLAT)
			{
				notes[0].accidental(Accidental.NONE);
			}
			else
			{
				notes[0].name((notes[0].name() + 1) % Name.NB_NAMES);
				notes[0].accidental(Accidental.FLAT);
			}
			
			halfToneCnt++;
		}
		
		return halfToneCnt;
	}
	
	private Note mTonic;
	private int mMode;
	private ArrayList<Note> mNotes;
	private int mAccidental;
	
	private boolean mIsValid;
	
	private static final int[] IONIAN_INTERVALS = {2, 2, 1, 2, 2, 2, 1};
}
