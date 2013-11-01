package com.scythe.musicgenerator.core;

public class Note 
{	
	public Note(int name, int accidental, int octave)
	{
		mName = name;
		mAccidental = accidental;
		mOctave = octave;
	}
	
	public Note(int name, int accidental)
	{
		mName = name;
		mAccidental = accidental;
		mOctave = mDefaultOctave;
	}
	
	public Note(int name)
	{
		mName = name;
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
	}
	
	public Note(Note note)
	{
		mName = note.mName;
		mAccidental = note.mAccidental;
		mOctave = note.mOctave;
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
	
	public int octave()
	{
		return mOctave;
	}
	
	public void octave(int octave)
	{
		mOctave = octave;
	}
	
	@Override
	public String toString()
	{
		return Name.toString(mName) + Accidental.toString(mAccidental) + mOctave;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Note note = (Note) obj;
		
		return mName == note.name() && mAccidental == note.accidental() ? true : false;
	}
	
	public boolean equalsOctave(Note note)
	{
		return mName == note.name() && mAccidental == note.accidental() && mOctave == note.mOctave ? true : false;
	}
	
	public boolean equalsNormalized(Note note)
	{
		Note note1 = new Note(note);
		Note note2 = new Note(this);
		
		note1.normalize();
		note2.normalize();
		
		return note1.name() == note2.name() && note1.accidental() == note2.accidental() ? true : false;
	}
	
	public void normalize()
	{
		if(mName == Name.C && mAccidental == Accidental.FLAT)
		{
			mName = Name.B;
			mAccidental = Accidental.NONE;
			mOctave--;
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
			mOctave++;
		}
	}
	
	public static void defaultOctave(int defaultOctave)
	{
		mDefaultOctave = defaultOctave;
	}
	
	public static class Name
	{
		public static final int COUNT = 7;
		
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
				case A: return "A";
				case B: return "B";
				case C: return "C";
				case D: return "D";
				case E: return "E";
				case F: return "F";
				case G: return "G";
			}
			
			return "";
		}
	}
	
	public static class Accidental
	{
		public static final int COUNT = 3;
		
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
	
	private static int mDefaultOctave = 4;
	private int mName;
	private int mAccidental;
	private int mOctave;
}
