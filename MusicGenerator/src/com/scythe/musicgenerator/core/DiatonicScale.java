package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Note.Accidental;
import com.scythe.musicgenerator.core.Note.Name;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;

public class DiatonicScale
{
	public DiatonicScale(Note tonic, int mode)
	{
		mMode = mode;
		mIsValid = true;
		
		mIntervals = shift(mMode);
		
		mNotes = new ArrayList<Note>();
		mNotes.add(tonic);
		
		for(int i = 1; i < 7; i++)
		{
			Note nextNote = getHalfToneUpperNote(mNotes.get(i - 1), mIntervals[i - 1]);
			
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
		
		if(mNotes.get(0).name() != Note.Name.C)
		{
			for(int i = indexOfC(); i < mNotes.size(); i++)
			{
				mNotes.get(i).octave(mNotes.get(i).octave() + 1);
			}
		}
	}
	
	public Note tonic()
	{
		return mNotes.get(0);
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
	
	public int getNoteAtUpperInterval(int baseNoteIndex, int interval, Note note)
	{
		Note n = mNotes.get((baseNoteIndex + interval + 1) % mNotes.size());
		
		note.name(n.name());
		note.accidental(n.accidental());
		note.octave(n.octave());
		
		int diatHalfToneCnt = 0;
		int toneCnt = 0;
		
		for(int i = baseNoteIndex; i < baseNoteIndex + interval + 1; i++)
		{
			int halfToneInterval = mIntervals[i % mIntervals.length];
			
			if(halfToneInterval == 1)
			{
				diatHalfToneCnt++;
			}
			else if(halfToneInterval == 2)
			{
				toneCnt++;
			}
		}
		
		switch(interval)
		{
			case Interval.Name.SECOND:
			{
				if(toneCnt == 0 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.MINOR;
				}
				else if(toneCnt == 1 && diatHalfToneCnt == 0)
				{
					return Interval.Qualification.MAJOR;
				}
				
				break;
			}
			case Interval.Name.THIRD:
			{
				if(toneCnt == 0 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 1 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.MINOR;
				}
				else if(toneCnt == 2 && diatHalfToneCnt == 0)
				{
					return Interval.Qualification.MAJOR;
				}
				
				break;
			}
			case Interval.Name.FOURTH:
			{
				if(toneCnt == 1 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 2 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.PERFECT;
				}
				else if(toneCnt == 3 && diatHalfToneCnt == 0)
				{
					return Interval.Qualification.AUGMENTED;
				}
				
				break;
			}
			case Interval.Name.FIFTH:
			{
				if(toneCnt == 2 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 3 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.PERFECT;
				}
				else if(toneCnt == 4 && diatHalfToneCnt == 0)
				{
					return Interval.Qualification.AUGMENTED;
				}
				
				break;
			}
			case Interval.Name.SIXTH:
			{
				if(toneCnt == 2 && diatHalfToneCnt == 3)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 3 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.MINOR;
				}
				else if(toneCnt == 4 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.MAJOR;
				}
				
				break;
			}
			case Interval.Name.SEVENTH:
			{
				if(toneCnt == 3 && diatHalfToneCnt == 3)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 4 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.MINOR;
				}
				else if(toneCnt == 5 && diatHalfToneCnt == 1)
				{
					return Interval.Qualification.MAJOR;
				}
				
				break;
			}
			case Interval.Name.OCTAVE:
			{
				if(toneCnt == 4 && diatHalfToneCnt == 3)
				{
					return Interval.Qualification.DIMINISHED;
				}
				else if(toneCnt == 5 && diatHalfToneCnt == 2)
				{
					return Interval.Qualification.PERFECT;
				}
				
				break;
			}
		}
		
		return -1;
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
	
	private int indexOfC()
	{
		int indexC;
		for(indexC = 0; mNotes.get(indexC).name() != Note.Name.C; indexC++);
		return indexC;
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
		Note newNote = new Note((refNote.name() + 1) % Name.COUNT, refNote.accidental());
		
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
					notes[i].name((notes[i].name() + 1) % Name.COUNT);
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
				notes[0].name((notes[0].name() + 1) % Name.COUNT);
				notes[0].accidental(Accidental.FLAT);
			}
			
			halfToneCnt++;
		}
		
		return halfToneCnt;
	}
	
	private int mMode;
	private ArrayList<Note> mNotes;
	private int mAccidental;
	
	private boolean mIsValid;
	
	private int[] mIntervals;
	
	private static final int[] IONIAN_INTERVALS = {2, 2, 1, 2, 2, 2, 1};
}
