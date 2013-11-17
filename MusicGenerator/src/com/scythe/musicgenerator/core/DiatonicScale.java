package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Note.Accidental;
import com.scythe.musicgenerator.core.Note.Name;
import com.scythe.musicgenerator.core.TimedElement.Duration;
import com.scythe.musicgenerator.midi.MidiWriter;

@SuppressWarnings("serial")
public class DiatonicScale extends ArrayList<Note>
{
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
		
		if(get(0).name() != Note.Name.C)
		{
			for(int i = indexOfC(); i < size(); i++)
			{
				get(i).octave(get(i).octave() + 1);
			}
		}
	}
	
	public Note tonic()
	{
		return get(0);
	}
	
	public int mode()
	{
		return mMode;
	}
	
	public Note note(int index)
	{
		return get(index);
	}
	
	public int noteCnt()
	{
		return size();
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
		for(int i = 0; i < size(); i++)
		{
			if(normalized)
			{
				if(get(i).equalsNormalized(note))
				{
					return true;
				}
			}
			else
			{
				if(get(i).equals(note))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasStrangeNote()
	{
		for(int i = 0; i < size(); i++)
		{
			if(get(i).name() == Note.Name.C && get(i).accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(get(i).name() == Note.Name.E && get(i).accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
			else if(get(i).name() == Note.Name.F && get(i).accidental() == Note.Accidental.FLAT)
			{
				return true;
			}
			else if(get(i).name() == Note.Name.B && get(i).accidental() == Note.Accidental.SHARP)
			{
				return true;
			}
		}
		
		return false;
	}
	
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
		
		note.name(n.name());
		note.accidental(n.accidental());
		note.octave(n.octave() + octaveIncr);
		
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
	
	public void toMidiFile(String fileName)
	{
		Bar bar = new Bar(new TimeSignature("8/4"));
		for(int noteIndex = 0; noteIndex < size(); noteIndex++)
		{
			TimedElement te = new TimedElement(Duration.SINGLE, false);
			te.add(get(noteIndex));
			bar.add(te);
		}
		
		Note note = new Note(get(0));
		note.octave(note.octave() + 1);
		TimedElement te = new TimedElement(Duration.SINGLE, false);
		te.add(note);
		bar.add(te);
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter(fileName);
		midiWriter.addTrack(track, "scale");
		midiWriter.write();
	}
	
	public int getNumberOfAccidental()
	{
		int numberOfAccidental = 0;
		for(Note note : this)
		{
			if(note.accidental() != Note.Accidental.NONE)
			{
				numberOfAccidental++;
			}
		}
		
		return numberOfAccidental;
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
		for(indexC = 0; get(indexC).name() != Note.Name.C; indexC++);
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
	
	public static class Mode
	{
		public static final int COUNT = 7;
		
		public static final int IONIAN		= 0;
		public static final int DORIAN		= 6;
		public static final int PHRYGIAN	= 5;
		public static final int LYDIAN		= 4;
		public static final int MIXOLYDIAN	= 3;
		public static final int EOLIAN		= 2;
		public static final int LOCRIAN		= 1;
		
		public static String toString(int mode)
		{
			switch(mode)
			{
				case IONIAN		: return "IONIAN (major)";
				case DORIAN		: return "DORIAN";
				case PHRYGIAN	: return "PHRYGIAN";
				case LYDIAN		: return "LYDIAN";
				case MIXOLYDIAN	: return "MIXOLYDIAN";
				case EOLIAN		: return "EOLIAN (minor)";
				case LOCRIAN	: return "LOCRIAN";
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
