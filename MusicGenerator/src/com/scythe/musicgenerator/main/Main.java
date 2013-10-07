package com.scythe.musicgenerator.main;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.Note.Notation;
import com.scythe.musicgenerator.core.Scale;
import com.scythe.musicgenerator.core.TimedNote;
import com.scythe.musicgenerator.defines.Degree;
import com.scythe.musicgenerator.defines.Duration;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;
import com.scythe.musicgenerator.factories.BarFactory;
import com.scythe.musicgenerator.factories.ScaleFactory;

public class Main
{
	public static void main(String[] args)
	{
		Note.notation(Notation.FRENCH);
		
		//validBarTest();
		//perfectFifthTest();
		testBarFactory();
	}
	
	public static void validBarTest()
	{
		int[] rhythmSignature = new int[2];
		rhythmSignature[0] = 4;
		rhythmSignature[1] = Duration.SINGLE;
		
		TimedNote[] notes = new TimedNote[4];
		notes[0] = new TimedNote(new Note(Note.Name.A), Duration.SINGLE, false);
		notes[1] = new TimedNote(new Note(Note.Name.A), Duration.SINGLE, false);
		notes[2] = new TimedNote(new Note(Note.Name.A), Duration.SINGLE, false);
		notes[3] = new TimedNote(new Note(Note.Name.A), Duration.SINGLE, false);
		
		Bar bar = new Bar(rhythmSignature, notes);
		
		System.out.println("Bar is fully valid: " + (bar.isFullyValid() ? "YES" : "NO"));
	}
	
	public static void perfectFifthTest()
	{
		Note tonic = new Note(Note.Name.A, Note.Accidental.NONE);
		
		for(int mode = 0; mode < Mode.NB_MODES; mode++)
		{
			Scale scale = ScaleFactory.NaturalMode.generate(tonic, mode);
			System.out.println("Selected scale: " + scale);
			
			for(int noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
			{
				Note baseNote = scale.note(noteIndex);
				Note perfectFifth = baseNote.getNoteAtInterval(Interval.PERFECT_FIFTH, scale.accidental());
				
				if(!scale.isIn(perfectFifth, true))
				{
					System.out.println("degree " + Degree.toString(noteIndex) + " chord can't be a power chord");
					break;
				}
			}
			
			continue;
		}
		
		System.out.println();
	}
	
	public static void testBarFactory()
	{
		System.out.println(BarFactory.generateNonMelodic(new int[]{9, Duration.HALF}));
	}
}

