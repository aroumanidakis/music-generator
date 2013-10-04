package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.core.Note.Notation;
import com.scythe.musicgenerator.defines.Duration;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;

public class Main
{
	public static void main(String[] args)
	{
		Note.notation(Notation.FRENCH);
		
		validBarTest();
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
		for(int noteName = 0; noteName < Note.Name.NB_NAME; noteName++)
		{
			Note tonic = new Note(noteName, Note.Accidental.NONE);
			Scale scale = ScaleFactory.NaturalMode.generate(tonic, Mode.IONIAN);
			
			System.out.println("Selected scale: " + scale);
			
			for(int noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
			{
				Note baseNote = scale.note(noteIndex);
				Note perfectFifth = baseNote.getNoteAtInterval(Interval.PERFECT_FIFTH, scale.accidental());
				
				if(!scale.isIn(perfectFifth, true))
				{
					Note note = scale.note((noteIndex + 4) % scale.noteCnt());
					
					System.out.println("\t" + (noteIndex + 1) + " Perfect fifth of " + baseNote + " (" + perfectFifth + ") is not in the scale... (" + note + ")");
				}
			}
			
			System.out.println();
		}
	}
}

