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
		
		Scale scale = null;
		
		while(true)
		{
			int name = (int)(Math.random() * Note.Name.NB_NAME);
			int accidental = (int)(Math.random() * Note.Accidental.NB_ACCIDENTAL);
			
			Note tonic = new Note(name, accidental);
			int mode = (int)(Math.random() * Mode.NB_MODE);
			
			try
			{
				scale = ScaleFactory.NaturalMode.generate(tonic, mode);
			}
			catch(IllegalArgumentException e)
			{
				continue;
			}
			
			if((!scale.hasStrangeNote()) && scale.isIn(scale.note(0).getNoteAtInterval(Interval.PERFECT_FIFTH, scale.accidental())))
			{
				break;
			}
		}
		
		System.out.println("Selected scale: " + scale);
		
		int[] degrees = new int[4];
		
		for(int i = 0; i < degrees.length; i++)
		{
			degrees[i] = i == 0 ? 0 : (int)(Math.random() * Degree.NB_DEGREES);
		}
		
		Grid grid = new Grid(degrees);
		
		System.out.println("Generated grid: " + grid);
		
		for(int i = 0; i < grid.degreesCnt(); i++)
		{
			int fundamentalIndex = grid.degree(i);
			
			Note[] chordNotes = new Note[2];
			
			chordNotes[0] = scale.note(fundamentalIndex);
			chordNotes[1] = chordNotes[0].getNoteAtInterval(Interval.PERFECT_FIFTH, scale.accidental());
			
			Chord chord = new Chord(chordNotes);
			System.out.println(chord);
		}
		
		System.out.println(new TimedNote(scale.note((int)(Math.random() * scale.noteCnt())), (int)(Math.pow(2, (int)(Math.random() * 5))), ((int)(Math.random() * 2) == 0 ? true : false)));
	}
}

