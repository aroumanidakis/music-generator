package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.core.TimedElement.Duration;

/**
 * Class intended to represent a note. <br />
 * <br />
 * A note is defined by : <br />
 * - A name (A, B, C, D, E, F or G) <br />
 * - An accidental (#, b or none) <br />
 * - An octave between -2 and 8 (in accordance with MIDI specifications) <br />
 * - A dynamics between 0 and 127 (in accordance with MIDI specifications) <br />
 * 
 * @author Scythe
 */
public class Note 
{
	public static final int MIN_OCTAVE = -2;
	public static final int MAX_OCTAVE = 8;
	
	/**
	 * Default constructor. Instantiates a C, octave 2, Mezzoforte. 
	 */
	public Note()
	{
		mName = Name.C;
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	/**
	 * Constructor.
	 * @param name The note name.
	 * @param accidental The note accidental.
	 * @param octave The note octave.
	 * @param dynamics The note Dynamics.
	 * @see Note.Name
	 * @see Note.Accidental
	 * @see Note.Dynamics
	 */
	public Note(int name, int accidental, int octave, int dynamics)
	{
		setName(name);
		setAccidental(accidental);
		setOctave(octave);
		setDynamics(dynamics);
	}
	
	/**
	 * Constructor setting automatically the default dynamics.
	 * @param name The note name.
	 * @param accidental The note accidental.
	 * @param octave The note octave.
	 * @see Note.Name
	 * @see Note.Accidental
	 * @see Note.Dynamics
	 */
	public Note(int name, int accidental, int octave)
	{
		setName(name);
		setAccidental(accidental);
		setOctave(octave);
		mDynamics = mDefaultDynamics;
	}
	
	/**
	 * Constructor setting automatically the default octave and dynamics.
	 * @param name The note name.
	 * @param accidental The note accidental.
	 * @see Note.Name
	 * @see Note.Accidental
	 */
	public Note(int name, int accidental)
	{
		setName(name);
		setAccidental(accidental);
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	/**
	 * Constructor setting automatically the default octave and dynamics and the accidental to none.
	 * @param name The note name.
	 * @see Note.Name
	 */
	public Note(int name)
	{
		setName(name);
		mAccidental = Accidental.NONE;
		mOctave = mDefaultOctave;
		mDynamics = mDefaultDynamics;
	}
	
	/**
	 * Constructor by copy.
	 * @param note The instance to copy.
	 */
	public Note(Note note)
	{
		mName = note.mName;
		mAccidental = note.mAccidental;
		mOctave = note.mOctave;
		mDynamics = note.mDynamics;
	}
	
	/**
	 * Gets the note name.
	 * @return The note name.
	 * @see Note.Name
	 */
	public int getName()
	{
		return mName;
	}
	
	/**
	 * Sets the note name. Will work only if the parameter is a Note.Name constant value.
	 * @param name The note name.
	 * @see Note.Name
	 */
	public void setName(int name)
	{
		if(name >= Name.getList()[0] && name <= Name.getList()[Name.getList().length - 1])
		{
			mName = name;
		}
	}
	
	/**
	 * Gets the note accidental.
	 * @return The note accidental.
	 * @see Note.Accidental
	 */
	public int getAccidental()
	{
		return mAccidental;
	}
	
	/**
	 * Sets the note accidental. Will work only if the parameter is a Note.Accidental constant value.
	 * @param accidental The note accidental.
	 * @see Note.Accidental
	 */
	public void setAccidental(int accidental)
	{
		if(accidental >= Accidental.getList()[0] && accidental <= Accidental.getList()[Accidental.getList().length - 1])
		{
			mAccidental = accidental;
		}
	}
	
	/**
	 * Gets the note octave.
	 * @return The note octave.
	 */
	public int getOctave()
	{
		return mOctave;
	}
	
	/**
	 * Sets the note octave. Will work only if it fits with MIDI specifications : a note should be between C-2 and G8.
	 * @param octave The note octave.
	 * @return true if the octave has been changed, false otherwise.
	 */
	public boolean setOctave(int octave)
	{
		if(octave >= MIN_OCTAVE && octave <= MAX_OCTAVE)
		{
			if(octave == MAX_OCTAVE && (mName > Name.G || (mName == Name.G && mAccidental == Accidental.SHARP) || (mName == Name.A && mAccidental == Accidental.FLAT)))
			{
				System.out.println(Name.toString(mName) + Accidental.toString(mAccidental) + octave + " is not supported.");
				return false;
			}
			else
			{
				mOctave = octave;
				return true;
			}
		}
		else
		{
			System.out.println(Name.toString(mName) + Accidental.toString(mAccidental) + octave + " is not supported.");
			return false;
		}
	}
	
	/**
	 * Gets the note dynamics.
	 * @return The note dynamics.
	 */
	public int getDynamics()
	{
		return mDynamics;
	}
	
	/**
	 * Sets the note dynamics. Will work only if it fits with MIDI specifications : a velocity should be 0 and 127.
	 * @param dynamics the note dynamics.
	 */
	public void setDynamics(int dynamics)
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
	
	/**
	 * Gets a random instance of Note.
	 * @return A random note.
	 */
	public static Note getRandom()
	{
		return new Note(Name.getRandom(), Accidental.getRandom());
	}
	
	/**
	 * Writes the note in a simple MIDI file.
	 * @param fileName The file name to write.
	 */
	public void toMidiFile(String fileName)
	{
		TimedElement te = new TimedElement(Duration.QUARTER);
		te.add(this);
		te.toMidiFile(fileName);
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
		
		return mName == note.getName() && mAccidental == note.getAccidental() ? true : false;
	}
	
	/**
	 * Compares the instance with another note about highness.
	 * @param note An other Note.
	 * @return 1 if the passed Note is higher, - 1 if it is lower, 0 if it is the same.
	 */
	public int compare(Note note)
	{
		if(equals(note))
		{
			return 0;
		}
		else
		{
			if(note.getOctave() < mOctave)
			{
				return -1;
			}
			else if(note.getOctave() > mOctave)
			{
				return 1;
			}
			else
			{
				return (note.getName() < mName) ? -1 : 1;
			}
		}
	}
	
	/**
	 * Gets the number of half tones between two notes.
	 * @param lowNote The low note.
	 * @param highNote The high note.
	 * @return The number of half tones.
	 */
	public static int getHalfToneDifference(Note lowNote, Note highNote, boolean octaveCare)
	{	
		Note[] notes = new Note[2];
		notes[0] = new Note(lowNote);
		notes[1] = new Note(highNote);
		
		for(int i = 0; i < 2; i++)
		{
			if(notes[i].getAccidental() == Accidental.SHARP)
			{
				if(notes[i].getName() == Name.B)
				{
					notes[i].setName(Name.C);
					notes[i].setAccidental(Accidental.NONE);
				}
				else if(notes[i].getName() == Name.E)
				{
					notes[i].setName(Name.F);
					notes[i].setAccidental(Accidental.NONE);
				}
				else
				{
					notes[i].setName((notes[i].getName() + 1) % Name.getList().length);
					notes[i].setAccidental(Accidental.FLAT);
				}
			}
			
			if(notes[i].getAccidental() == Accidental.FLAT)
			{
				if(notes[i].getName() == Name.F)
				{
					notes[i].setName(Name.E);
					notes[i].setAccidental(Accidental.NONE);
				}
				else if(notes[i].getName() == Name.C)
				{
					notes[i].setName(Name.B);
					notes[i].setAccidental(Accidental.NONE);
				}
			}
		}
		
		int halfToneCnt = 0;
		while(notes[0].getName() != notes[1].getName() || notes[0].getAccidental() != notes[1].getAccidental() || (octaveCare ? notes[0].getOctave() != notes[1].getOctave() : false))
		{
			if(notes[0].getName() == Name.E && notes[0].getAccidental() == Accidental.NONE)
			{
				notes[0].setName(Name.F);
			}
			else if(notes[0].getName() == Name.B && notes[0].getAccidental() == Accidental.NONE)
			{
				notes[0].setName(Name.C);
				if(octaveCare)
				{
					notes[0].setOctave(notes[0].getOctave() + 1);
				}
			}
			else if(notes[0].getAccidental() == Accidental.FLAT)
			{
				notes[0].setAccidental(Accidental.NONE);
			}
			else
			{
				notes[0].setName((notes[0].getName() + 1) % Name.getList().length);
				notes[0].setAccidental(Accidental.FLAT);
			}
			
			halfToneCnt++;
		}
		
		return halfToneCnt;
	}
	
	/**
	 * Sets the default octave for the next instantiations.
	 * @param defaultOctave The new default octave.
	 */
	public static void setDefaultOctave(int defaultOctave)
	{
		if(defaultOctave >= MIN_OCTAVE && defaultOctave <= MAX_OCTAVE)
		{
			mDefaultOctave = defaultOctave;
		}
	}
	
	/**
	 * Sets the default dynamics for the next instantiations.
	 * @param defaultDynamics The new default dynamics.
	 */
	public static void setDefaultDynamics(int defaultDynamics)
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
	
	/**
	 * Class of constants for note names.
	 * @author Scythe
	 */
	public static class Name
	{
		public static final int C = 0;
		public static final int D = 1;
		public static final int E = 2;
		public static final int F = 3;
		public static final int G = 4;
		public static final int A = 5;
		public static final int B = 6;
		
		/**
		 * Gets the list of existing constants.
		 * @return The list of constants.
		 */
		public static int[] getList()
		{
			return new int[]{C, D, E, F, G, A, B};
		}
		
		/**
		 * Gets a random value of constant.
		 * @return A random value of constant.
		 */
		public static int getRandom()
		{
			int[] list = getList();
			return list[(int)(Math.random() * (list.length - 1))];
		}

		public static String toString(int note)
		{
			switch(note)
			{
				case C: return "C";
				case D: return "D";
				case E: return "E";
				case F: return "F";
				case G: return "G";
				case A: return "A";
				case B: return "B";
			}
			
			return "";
		}
	}
	
	/**
	 * Class of consants for accidentals.
	 * @author Scythe
	 */
	public static class Accidental
	{
		public static final int NONE = 0;
		public static final int SHARP = 1;
		public static final int FLAT = 2;
		
		/**
		 * Gets the list of existing constants.
		 * @return The list of constants.
		 */
		public static int[] getList()
		{
			return new int[]{NONE, SHARP, FLAT};
		}
		
		/**
		 * Gets a random value of constant.
		 * @return A random value of constant.
		 */
		public static int getRandom()
		{
			int[] list = getList();
			return list[(int)(Math.random() * (list.length - 1))];
		}
		
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
	
	/**
	 * Class of constants for Dynamics
	 * @author Scythe
	 */
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
		
		/**
		 * Gets the list of existing constants.
		 * @return The list of constants.
		 */
		public static int[] getList()
		{
			return new int[]{PIANISSISSIMO, PIANISSIMO, PIANO, MEZZOPIANO, MEZZOFORTE, FORTE, FORTISSIMO, FORTISSISSIMO};
		}
		
		/**
		 * Gets a random value of constant.
		 * @return A random value of constant.
		 */
		public static int getRandom()
		{
			int[] list = getList();
			return list[(int)(Math.random() * (list.length - 1))];
		}
		
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
