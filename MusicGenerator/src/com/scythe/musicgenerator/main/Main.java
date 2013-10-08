package com.scythe.musicgenerator.main;

import java.io.File;
import java.io.FileOutputStream;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.Note.Notation;
import com.scythe.musicgenerator.core.Scale;
import com.scythe.musicgenerator.defines.Degree;
import com.scythe.musicgenerator.defines.Duration;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;
import com.scythe.musicgenerator.factories.BarFactory;
import com.scythe.musicgenerator.factories.ScaleFactory;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		Note.notation(Notation.AMERICAN);
		
		validBarTest();
		perfectFifthTest();
		barFactoryTest();
	}
	
	public static void validBarTest()
	{
		System.out.println("VALID BAR TEST\n");
		
		int[] rhythmSignature = new int[2];
		rhythmSignature[0] = 4;
		rhythmSignature[1] = Duration.SINGLE;
		
		Note[] notes = new Note[4];
		notes[0] = new Note(Note.Name.A, Note.Accidental.NONE, Duration.SINGLE, false);
		notes[1] = new Note(Note.Name.A, Note.Accidental.NONE, Duration.SINGLE, false);
		notes[2] = new Note(Note.Name.A, Note.Accidental.NONE, Duration.SINGLE, false);
		notes[3] = new Note(Note.Name.A, Note.Accidental.NONE, Duration.SINGLE, false);
		
		Bar bar = new Bar(rhythmSignature, notes);
		
		System.out.println("Bar is fully valid: " + (bar.isFullyValid() ? "YES" : "NO"));
		System.out.println();
	}
	
	public static void perfectFifthTest()
	{
		System.out.println("PERFECT FIFTH TEST\n");
		
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
	
	public static void barFactoryTest() throws Exception
	{
		System.out.println("BAR FACTORY TEST\n");
		
		FileOutputStream file = new FileOutputStream(new File("rhythms.txt"));
		
		for(int i = 0; i < 30; i++)
		{
			int[] signature = (Math.random() < 0.5f) ? new int[]{4, Duration.SINGLE} : new int[]{6, Duration.HALF};
			
			Bar bar = BarFactory.generateNonMelodic(signature);
			
			String barStr = bar.toString();
			System.out.println(barStr + (bar.isFullyValid() ? " (valid)" : " (not valid)"));
			
			barStr += "\n";
			
			file.write(barStr.getBytes());
		}
		
		file.close();
		System.out.println();
	}
}

