package com.scythe.musicgenerator.core;

import com.scythe.musicgenerator.core.Note.Notation;
import com.scythe.musicgenerator.defines.Degree;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;


public class Main
{
	public static void main(String[] args)
	{
		Note.notation(Notation.FRENCH);
		
		for(int noteName = 0; noteName < Note.Name.NB_NAME; noteName++)
		{
			Note tonic = new Note(noteName, Note.Accidental.NONE);
			Scale scale = ScaleFactory.NaturalMode.generate(tonic, Mode.EOLIAN);
			
			System.out.println("Selected scale: " + scale);
			
			for(int noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
			{
				Note baseNote = scale.note(noteIndex);
				Note perfectFifth = baseNote.getNoteAtInterval(Interval.PERFECT_FIFTH, scale.accidental());
				
				if(!scale.isIn(perfectFifth))
				{
					Note note = scale.note((noteIndex + 4) % scale.noteCnt());
					
					System.out.println("\tPerfect fifth of " + baseNote + " (" + perfectFifth + ") is not in the scale... (" + note + ")");
				}
			}
			
			System.out.println();
		}
	}
}

