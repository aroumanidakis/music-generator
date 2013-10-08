package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Duration;

public class Note 
{
	public Note(int name, int accidental, int duration, boolean dotted)
	{
		mName = name;
		mAccidental = accidental;
		mIsTimed = true;
		mDuration = duration;
		mDotted = dotted;
	}
	
	public Note(int name, int accidental)
	{
		mName = name;
		mAccidental = accidental;
		mIsTimed = false;
		mDuration = 0;
		mDotted = false;
	}
	
	public Note(int name)
	{
		mName = name;
		mAccidental = Accidental.NONE;
		mIsTimed = false;
		mDuration = 0;
		mDotted = false;
	}
	
	public Note(Note note)
	{
		mName = note.mName;
		mAccidental = note.mAccidental;
		mIsTimed = note.mIsTimed;
		mDuration = note.mDuration;
		mDotted = note.mDotted;
	}
	
	public int name()
	{
		return mName;
	}
	
	public void name(int name)
	{
		mName = name;
	}
	
	public int accidental()
	{
		return mAccidental;
	}
	
	public void accidental(int accidental)
	{
		mAccidental = accidental;
	}
	
	public boolean isTimed()
	{
		return mIsTimed;
	}
	
	public int duration()
	{
		return mDuration;
	}
	
	public float durationInTime()
	{
		float duration = Duration.convertInTime(mDuration);
		return mDotted ? duration * 1.5f : duration;
	}
	
	public boolean dotted()
	{
		return mDotted;
	}
	
	@Override
	public String toString()
	{
		return (mIsTimed ? durationInTime() : "") + Name.toString(mName) + Accidental.toString(mAccidental);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Note note = (Note) obj;
		
		return mName == note.name() && mAccidental == note.accidental() ? true : false;
	}
	
	public boolean equalsNormalized(Note note)
	{
		Note note1 = new Note(note);
		Note note2 = new Note(this);
		
		note1.normalize();
		note2.normalize();
		
		return note1.name() == note2.name() && note1.accidental() == note2.accidental() ? true : false;
	}
	
	public Note getNoteAtInterval(int interval, int accidental)
	{
		Note note = new Note(this);
		
		for(int i = 0; i < interval; i++)
		{
			if(note.name() == Note.Name.E)
			{
				note.name(Note.Name.F);
			}
			else if(note.name() == Note.Name.B)
			{
				note.name(Note.Name.C);
			}
			else if(accidental == Accidental.FLAT)
			{
				if(note.accidental() == Accidental.FLAT)
				{
					note.accidental(Accidental.NONE);
				}
				else
				{
					note.name((note.name() + 1) % Note.Name.NB_NAME);
					note.accidental(Note.Accidental.FLAT);
				}
			}
			else
			{
				if(note.accidental() == Accidental.SHARP)
				{
					note.name((note.name() + 1) % Note.Name.NB_NAME);
					note.accidental(Note.Accidental.NONE);
				}
				else
				{
					note.accidental(Accidental.SHARP);
				}
			}
		}
		
		return note;
	}
	
	public void normalize()
	{
		if(mName == Name.C && mAccidental == Accidental.FLAT)
		{
			mName = Name.B;
			mAccidental = Accidental.NONE;
		}
		else if(mName == Name.E && mAccidental == Accidental.SHARP)
		{
			mName = Name.F;
			mAccidental = Accidental.NONE;
		}
		else if(mName == Name.F && mAccidental == Accidental.FLAT)
		{
			mName = Name.E;
			mAccidental = Accidental.NONE;
		}
		else if(mName == Name.B && mAccidental == Accidental.SHARP)
		{
			mName = Name.C;
			mAccidental = Accidental.NONE;
		}
	}
	
	public static void notation(int notation)
	{
		mNotation = notation;
	}
	
	public static class Name
	{
		public static final int NB_NAME = 7;
		
		public static final int A = 0;
		public static final int B = 1;
		public static final int C = 2;
		public static final int D = 3;
		public static final int E = 4;
		public static final int F = 5;
		public static final int G = 6;
		
		public static String toString(int note)
		{
			switch(note)
			{
				case A: return mNotation == Notation.FRENCH ? "LA" : "A";
				case B: return mNotation == Notation.FRENCH ? "SI" : "B";
				case C: return mNotation == Notation.FRENCH ? "DO" : "C";
				case D: return mNotation == Notation.FRENCH ? "RE" : "D";
				case E: return mNotation == Notation.FRENCH ? "MI" : "E";
				case F: return mNotation == Notation.FRENCH ? "FA" : "F";
				case G: return mNotation == Notation.FRENCH ? "SOL" : "G";
			}
			
			return "";
		}
	}
	
	public static class Accidental
	{
		public static final int NB_ACCIDENTAL = 3;
		
		public static final int NONE = 0;
		public static final int SHARP = 1;
		public static final int FLAT = 2;
		
		public static String toString(int accidental)
		{
			switch(accidental)
			{
				case SHARP: return "#";
				case FLAT: return "b";
			}
			
			return "";
		}
	}
	
	public static class Notation
	{
		public static final int AMERICAN = 0;
		public static final int FRENCH = 1;
	}
	
	private int mName;
	private int mAccidental;
	
	private boolean mIsTimed;
	private int mDuration;
	private boolean mDotted;
	
	private static int mNotation = Notation.AMERICAN;
}
