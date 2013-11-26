package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Note.Accidental;
import com.scythe.musicgenerator.core.Note.Name;
import com.scythe.musicgenerator.core.TimedElement.Duration;
import com.scythe.musicgenerator.midi.MidiWriter;

/**
 * Class intended to represent a diatonic scale. It contains a generator able to build a scale from a tonic and one of the seven diatonic modes. <br />
 * The double sharp and double flat or higher is not supported. Check if the instantiation has been well done by calling isValid() method.
 * @author Scythe
 * @see DiatonicScale.Mode
 */
@SuppressWarnings("serial")
public class DiatonicScale extends ArrayList<Note>
{
	/**
	 * Main constructor of the class. It will generate automatically the requested scale when it is possible.
	 * @param tonic The scale tonic.
	 * @param mode The scale mode.
	 * @see DiatonicScale.Mode
	 */
	public DiatonicScale(Note tonic, int mode)
	{
		mMode = mode;
		mIsValid = true;
		
		mIntervals = shift(mMode);
		
		add(tonic);
		
		for(int i = 1; i < 7; i++)
		{
			Note nextNote = getHalfToneUpperNote(get(i - 1), mIntervals[i - 1]);
			if(nextNote == null)
			{
				mIsValid = false;
				return;
			}
			
			add(nextNote);
		}
		
		mAccidental = Note.Accidental.NONE;
		
		for(Note note : this)
		{
			if(mAccidental == Note.Accidental.NONE)
			{
				if(note.getAccidental() == Note.Accidental.SHARP)
				{
					mAccidental = Note.Accidental.SHARP;
					break;
				}
				else if(note.getAccidental() == Note.Accidental.FLAT)
				{
					mAccidental = Note.Accidental.FLAT;
					break;
				}
			}
		}
		
		if(get(0).getName() != Note.Name.C)
		{
			for(int i = indexOfC(); i < size(); i++)
			{
				get(i).octave(get(i).getOctave() + 1);
			}
		}
	}
	
	/**
	 * Gets the scale tonic.
	 * @return The scale tonic.
	 */
	public Note getTonic()
	{
		return get(0);
	}
	
	/**
	 * Gets the scale mode.
	 * @return The scale mode.
	 * @see DiatonicScale.Mode
	 */
	public int getMode()
	{
		return mMode;
	}
	
	/**
	 * Gets the accidental of the hole scale.
	 * @return the scale accidental.
	 */
	public int getAccidental()
	{
		return mAccidental;
	}
	
	/**
	 * Checks if the instance has been correctly made.
	 * @return false if any problems occurred during the instantiation, true otherwise.
	 */
	public boolean isValid()
	{
		return mIsValid;
	}
	
	/**
	 * Checks if the scale contains strange notes like Cb, E#, Fb, B#.
	 * @return true if one of these four notes is in the scale, false otherwise.
	 */
	public boolean hasStrangeNote()
	{
		for(int i = 0; i < size(); i++)
		{
			if(get(i).getName() == Note.Name.C && get(i).getAccidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(get(i).getName() == Note.Name.E && get(i).getAccidental() == Note.Accidental.SHARP)
			{
				return true;
			}
			else if(get(i).getName() == Note.Name.F && get(i).getAccidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(get(i).getName() == Note.Name.B && get(i).getAccidental() == Note.Accidental.SHARP)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets a note at the specified upper interval.
	 * @param baseNoteIndex The base degree in the scale.
	 * @param interval The interval wanted.
	 * @param note An instantiated Note to be felt.
	 * @return the qualification of the interval requested.
	 * @see Interval.Name
	 * @see Interval.Qualification
	 */
	public int getNoteAtUpperInterval(int baseNoteIndex, int interval, Note note)
	{
		int octaveIncr = 0;
		int newNoteIndex = baseNoteIndex;
		for(int i = 0; i < interval + 1; i++)
		{
			newNoteIndex++;
			if(newNoteIndex % size() == 0)
			{
				octaveIncr++;
			}
		}
		
		Note n = get(newNoteIndex % size());
		
		if(note != null)
		{
			note.setName(n.getName());
			note.setAccidental(n.getAccidental());
			note.octave(n.getOctave() + octaveIncr);
		}
		
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
	
	/**
	 * Writes the scale in a simple MIDI file.
	 * @param fileName the MIDI file name to write.
	 */
	public void toMidiFile(String fileName)
	{
		Bar bar = new Bar("4/4");
		for(int noteIndex = 0; noteIndex < size(); noteIndex++)
		{
			TimedElement te = new TimedElement(Duration.HALF);
			te.add(get(noteIndex));
			bar.add(te);
		}
		
		Note note = new Note(get(0));
		note.octave(note.getOctave() + 1);
		TimedElement te = new TimedElement(Duration.HALF);
		te.add(note);
		bar.add(te);
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter(fileName);
		midiWriter.addTrack(track, this.toString());
		midiWriter.write();
	}
	
	/**
	 * Gets the number of accidentals being in the scale.
	 * @return The number of accientals.
	 */
	public int getNumberOfAccidental()
	{
		int numberOfAccidental = 0;
		for(Note note : this)
		{
			if(note.getAccidental() != Note.Accidental.NONE)
			{
				numberOfAccidental++;
			}
		}
		
		return numberOfAccidental;
	}
	
	/**
	 * Gets a random, but valid, instance of Scale.
	 * @return A random Scale.
	 */
	public static DiatonicScale getRandom()
	{
		DiatonicScale scale = null;
		while(scale == null || !scale.isValid())
		{
			scale = new DiatonicScale(Note.getRandom(), Mode.getRandom());
		}
		
		return scale;
	}
	
	/**
	 * Gets a random, but valid, instance of Scale in accordance with passed Mode.
	 * @param mode The wanted mode.
	 * @return A random scale.
	 */
	public static DiatonicScale getRandom(int mode)
	{
		DiatonicScale scale = null;
		while(scale == null || !scale.isValid())
		{
			scale = new DiatonicScale(Note.getRandom(), mode);
		}
		
		return scale;
	}
	
	/**
	 * Gets the number of half tones between two notes.
	 * @param lowNote The low note.
	 * @param highNote The high note.
	 * @return The number of half tones.
	 */
	public int getHalfToneDifference(Note lowNote, Note highNote)
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
		while(notes[0].getName() != notes[1].getName() || notes[0].getAccidental() != notes[1].getAccidental())
		{	
			if(notes[0].getName() == Name.E && notes[0].getAccidental() == Accidental.NONE)
			{
				notes[0].setName(Name.F);
			}
			else if(notes[0].getName() == Name.B && notes[0].getAccidental() == Accidental.NONE)
			{
				notes[0].setName(Name.C);
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
	
	@Override
	public String toString()
	{
		String str = get(0) + " " + Mode.toString(mMode) + " [";
		
		for(int i = 0; i < size(); i++)
		{
			str += get(i) + (i == size() - 1 ? "]" : " ");
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
		for(indexC = 0; get(indexC).getName() != Note.Name.C; indexC++);
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
		Note newNote = new Note((refNote.getName() + 1) % Name.getList().length, refNote.getAccidental());
		
		int halfToneDiffenrence = getHalfToneDifference(refNote, newNote);
		
		if(halfToneDiffenrence == halfTones - 1)
		{
			if(newNote.getAccidental() == Accidental.FLAT)
			{
				newNote.setAccidental(Accidental.NONE);
			}
			else if(newNote.getAccidental() == Accidental.NONE)
			{
				newNote.setAccidental(Accidental.SHARP);
			}
			else
			{
				return null;
			}
		}
		else if(halfToneDiffenrence == halfTones + 1)
		{
			if(newNote.getAccidental() == Accidental.SHARP)
			{
				newNote.setAccidental(Accidental.NONE);
			}
			else if(newNote.getAccidental() == Accidental.NONE)
			{
				newNote.setAccidental(Accidental.FLAT);
			}
			else
			{
				return null;
			}
		}
		
		return newNote;
	}
	
	/**
	 * Class of constants for diatonic modes.
	 * @author Scythe
	 */
	public static class Mode
	{
		public static final int IONIAN = 0;
		public static final int DORIAN = 6;
		public static final int PHRYGIAN = 5;
		public static final int LYDIAN = 4;
		public static final int MIXOLYDIAN = 3;
		public static final int EOLIAN = 2;
		public static final int LOCRIAN = 1;
		
		/**
		 * Gets the list of existing constants.
		 * @return The list of constants.
		 */
		public static int[] getList()
		{
			return new int[]{IONIAN, DORIAN, PHRYGIAN, LYDIAN, MIXOLYDIAN, EOLIAN, LOCRIAN};
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
		
		public static String toString(int mode)
		{
			switch(mode)
			{
				case IONIAN: return "Ionian (maj.)";
				case DORIAN: return "Dorian";
				case PHRYGIAN: return "Phrygian";
				case LYDIAN: return "Lydian";
				case MIXOLYDIAN: return "Mixolydian";
				case EOLIAN: return "Eolian (min.)";
				case LOCRIAN: return "Locrian";
			}
			
			return "";
		}
	}
	
	private int mMode;
	private int mAccidental;
	private boolean mIsValid;
	private int[] mIntervals;
	
	private static final int[] IONIAN_INTERVALS = {2, 2, 1, 2, 2, 2, 1};
}
