package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.defines.Dynamics;

public class Note 
{
	public static final int MIN_OCTAVE = -2;
	public static final int MAX_OCTAVE = 8;
	
	public Note()
	{
		mName = Name.C;
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mVelocity = mDefaultVelocity;
	}
	
	public Note(int name, int accidental, int octave, int velocity)
	{
		name(name);
		accidental(accidental);
		octave(octave);
		velocity(velocity);
	}
	
	public Note(int name, int accidental, int octave)
	{
		name(name);
		accidental(accidental);
		octave(octave);
		mVelocity = mDefaultVelocity;
	}
	
	public Note(int name, int accidental)
	{
		name(name);
		accidental(accidental);
		mOctave = mDefaultOctave;
		mVelocity = mDefaultVelocity;
	}
	
	public Note(int name)
	{
		name(name);
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mVelocity = mDefaultVelocity;
	}
	
	public Note(Note note)
	{
		mName = note.mName;
		mAccidental = note.mAccidental;
		mOctave = note.mOctave;
		mVelocity = note.mVelocity;
	}
	
	public int name()
	{
		return mName;
	}
	
	public void name(int name)
	{
		if(name >= 0 & name < Name.COUNT)
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
	
	public int velocity()
	{
		return mVelocity;
	}
	
	public void velocity(int velocity)
	{
		if(velocity < 0)
		{
			mVelocity = 0;
		}
		else if(velocity > 127)
		{
			mVelocity = 127;
		}
		else
		{
			mVelocity = velocity;
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
		mDefaultOctave = defaultOctave;
	}
	
	public static void defaultVelocity(int defaultVelocity)
	{
		if(defaultVelocity < 0)
		{
			mDefaultVelocity = 0;
		}
		else if(defaultVelocity > 127)
		{
			mDefaultVelocity = 127;
		}
		else
		{
			mDefaultVelocity = defaultVelocity;
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
	 
	private static int mDefaultOctave = 2;
	private static int mDefaultVelocity = Dynamics.MEZZOFORTE;
	
	private int mName;
	private int mAccidental;
	private int mOctave;
	private int mVelocity;
}
