package com.scythe.musicgenerator.core;

public class Note 
{
	public static final int MIN_OCTAVE = -2;
	public static final int MAX_OCTAVE = 8;
	
	public Note()
	{
		mName = Name.C;
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	public Note(int name, int accidental, int octave, int dynamics)
	{
		name(name);
		accidental(accidental);
		octave(octave);
		dynamics(dynamics);
	}
	
	public Note(int name, int accidental, int octave)
	{
		name(name);
		accidental(accidental);
		octave(octave);
		mDynamics = mDefaultDynamics;
	}
	
	public Note(int name, int accidental)
	{
		name(name);
		accidental(accidental);
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	public Note(int name)
	{
		name(name);
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	public Note(Note note)
	{
		mName = note.mName;
		mAccidental = note.mAccidental;
		mOctave = note.mOctave;
		mDynamics = note.mDynamics;
	}
	
	public int name()
	{
		return mName;
	}
	
	public void name(int name)
	{
		if(name >= 0 && name < Name.COUNT)
		{
			mName = name;
		}
		else
		{
			mName = Name.C;
		}
	}
	
	public int accidental()
	{
		return mAccidental;
	}
	
	public void accidental(int accidental)
	{
		if(accidental >= 0 && accidental < Accidental.COUNT)
		{
			mAccidental = accidental;
		}
		else
		{
			mAccidental = Accidental.NONE;
		}
		
		mAccidental = accidental;
	}
	
	public int octave()
	{
		return mOctave;
	}
	
	public void octave(int octave)
	{
		if(octave >= MIN_OCTAVE && octave <= MAX_OCTAVE)
		{
			mOctave = octave;
		}
		else
		{
			mOctave = mDefaultOctave;
		}
	}
	
	public int dynamics()
	{
		return mDynamics;
	}
	
	public void dynamics(int dynamics)
	{
		if(dynamics < 0)
		{
			mDynamics = 0;
		}
		else if(dynamics > 127)
		{
			mDynamics = 127;
		}
		else
		{
			mDynamics = dynamics;
		}
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
	
	public byte toMidiNoteNumber()
	{
		int noteNumber = 24 + (mOctave * 12);
		
		switch(mName)
		{
			case Name.D:
			{
				noteNumber += 2;
				break;
			}
			case Name.E:
			{
				noteNumber += 4;
				break;
			}
			case Name.F:
			{
				noteNumber += 5;
				break;
			}
			case Name.G:
			{
				noteNumber += 7;
				break;
			}
			case Name.A:
			{
				noteNumber += 9;
				break;
			}
			case Name.B:
			{
				noteNumber += 11;
				break;
			}
		}
		
		switch(mAccidental)
		{
			case Accidental.SHARP:
			{
				noteNumber++;
				break;
			}
			case Accidental.FLAT:
			{
				noteNumber--;
			}
		}
		
		if(noteNumber < 0)
		{
			System.out.println("The note (" + this + ") is to low for midi format.");
			noteNumber = 0;
		}
		else if(noteNumber > 127)
		{
			System.out.println("The note (" + this + ") is to high for midi format.");
			noteNumber = 127;
		}
		
		return (byte)noteNumber;
	}
	
	public static void defaultOctave(int defaultOctave)
	{
		if(defaultOctave >= MIN_OCTAVE && defaultOctave <= MAX_OCTAVE)
		{
			mDefaultOctave = defaultOctave;
		}
	}
	
	public static void defaultDynamics(int defaultDynamics)
	{
		if(defaultDynamics < 0)
		{
			mDefaultDynamics = 0;
		}
		else if(defaultDynamics > 127)
		{
			mDefaultDynamics = 127;
		}
		else
		{
			mDefaultDynamics = defaultDynamics;
		}
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
	
	public static class Dynamics
	{
		public static final int PIANISSISSIMO = 16;
		public static final int PIANISSIMO = 32;
		public static final int PIANO = 48;
		public static final int MEZZOPIANO = 64;
		public static final int MEZZOFORTE = 80;
		public static final int FORTE = 96;
		public static final int FORTISSIMO = 110;
		public static final int FORTISSISSIMO = 126;
		
		public static String toString(int dynamics)
		{
			switch(dynamics)
			{
				case PIANISSISSIMO:	return "ppp";
				case PIANISSIMO:	return "pp";
				case PIANO:			return "p";
				case MEZZOPIANO:	return "mp";
				case MEZZOFORTE:	return "mf";
				case FORTE:			return "f";
				case FORTISSIMO:	return "ff";
				case FORTISSISSIMO:	return "fff";
			}
			
			return "";
		}
	}
	 
	private static int mDefaultOctave = 2;
	private static int mDefaultDynamics = Dynamics.MEZZOFORTE;
	
	private int mName;
	private int mAccidental;
	private int mOctave;
	private int mDynamics;
}
