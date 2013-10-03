package com.scythe.musicgenerator.core;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Note.Accidental;
import com.scythe.musicgenerator.core.Note.Name;
import com.scythe.musicgenerator.defines.Mode;

public class ScaleFactory
{
	private static boolean mVerbose = false;
	
	public static void verbose(boolean verbose)
	{
		mVerbose = verbose;
	}
	
	public static class NaturalMode
	{
		private static final int[] IONIAN_INTERVALS = {2, 2, 1, 2, 2, 2, 1};
		
		public static ArrayList<Scale> generate()
		{
			ArrayList<Scale> scales = new ArrayList<Scale>();
			
			for(int tonicName = 0; tonicName < Note.Name.NB_NAME; tonicName++)
			{
				for(int accidental = 0; accidental < Note.Accidental.NB_ACCIDENTAL; accidental++)
				{
					if((tonicName == Note.Name.B && accidental == Note.Accidental.SHARP) || (tonicName == Note.Name.E && accidental == Note.Accidental.SHARP) || (tonicName == Note.Name.C && accidental == Note.Accidental.FLAT) || (tonicName == Note.Name.F && accidental == Note.Accidental.FLAT))
					{
						continue;
					}
					
					Note tonic = new Note(tonicName, accidental);
					for(int mode = 0; mode < Mode.NB_MODE; mode++)
					{
						try
						{	
							scales.add(generate(tonic, mode));
						}
						catch(IllegalArgumentException e)
						{
							if(mVerbose)
							{
								System.out.println(e.getMessage());
							}
						}
					}
				}
			}
			
			return scales;
		}
		
		public static ArrayList<Scale> generate(Note tonic)
		{
			ArrayList<Scale> scales = new ArrayList<Scale>();
			
			for(int mode = 0; mode < Mode.NB_MODE; mode++)
			{
				try
				{
					scales.add(generate(tonic, mode));
				}
				catch (IllegalArgumentException e)
				{
					if(mVerbose)
					{
						System.out.println(e.getMessage());
					}
				}
			}
			
			return scales;
		}
		
		public static ArrayList<Scale> generate(int mode)
		{
			ArrayList<Scale> scales = new ArrayList<Scale>();
			
			for(int name = 0; name < Note.Name.NB_NAME; name++)
			{
				for(int accidental = 0; accidental < Note.Accidental.NB_ACCIDENTAL; accidental++)
				{
					Note tonic = new Note(name, accidental);
					try
					{
						scales.add(generate(tonic, mode));
					}
					catch (IllegalArgumentException e)
					{
						if(mVerbose)
						{
							System.out.println(e.getMessage());
						}
					}
				}
			}
			
			return scales;
		}
		
		public static Scale generate(Note tonic, int mode) throws IllegalArgumentException
		{
			int[] intervals = shift(mode);
			
			Note[] notes = new Note[7];
			notes[0] = tonic;
			
			for(int i = 1; i < 7; i++)
			{
				Note nextNote = getHalfToneUpperNote(notes[i - 1], intervals[i - 1]);
				
				if(nextNote == null)
				{
					throw new IllegalArgumentException("The scale " + tonic + " " + Mode.toString(mode) + " doesn't exists.");
				}
				
				notes[i] = nextNote;
			}
			
			return new Scale(mode, notes);
		}
		
		private static int[] shift(int shift)
		{
			int[] newIntervals = new int[IONIAN_INTERVALS.length];
			
			for(int i = 0; i < newIntervals.length; i++)
			{
				newIntervals[(i + shift) % 7] = IONIAN_INTERVALS[i];
			}
			
			return newIntervals;
		}
		
		private static Note getHalfToneUpperNote(Note refNote, int halfTones)
		{
			Note newNote = new Note((refNote.name() + 1) % Name.NB_NAME, refNote.accidental());
			
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
		
		private static int getHalfToneDifference(Note lowNote, Note highNote)
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
						notes[i].name((notes[i].name() + 1) % Name.NB_NAME);
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
					notes[0].name((notes[0].name() + 1) % Name.NB_NAME);
					notes[0].accidental(Accidental.FLAT);
				}
				
				halfToneCnt++;
			}
			
			return halfToneCnt;
		}
	}
}
