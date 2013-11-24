package com.scythe.musicgenerator.main;

import java.util.ArrayList;

import javax.swing.plaf.SliderUI;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Chord;
import com.scythe.musicgenerator.core.Degree;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.DiatonicScale.Mode;
import com.scythe.musicgenerator.core.Grid;
import com.scythe.musicgenerator.core.Interval;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimeSignature;
import com.scythe.musicgenerator.core.TimedElement;
import com.scythe.musicgenerator.core.TimedElement.Duration;
import com.scythe.musicgenerator.factories.BarFactory;
import com.scythe.musicgenerator.midi.MidiWriter;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		//testSimpleMelody();
		//testChordInversion();
		//testChordTransposition();
		//testAutomaticChordTransposition();
		//testChordGeneration();
		
		String[] signatures = {"2/1", "2/2", "2/4", "2/8", "3/1", "3/2", "3/4", "3/8", "4/1", "4/2", "4/4", "4/8", "6/2", "6/4", "6/8", "6/16", "9/2", "9/4", "9/8", "9/16", "12/2", "12/4", "12/8", "12/16"};
		
		DiatonicScale scale = new DiatonicScale(new Note(), Mode.IONIAN);
		int[] degrees = {Degree.I};
		
		Bar bar = null;
		for(String sig : signatures)
		{
			ArrayList<Bar> track = new ArrayList<Bar>();
			
			TimeSignature signature = new TimeSignature(sig);
			for(int division = -1; division < 10; division++)
			{
				bar = Bar.generateAccompanimentSimpleChords(signature, scale, degrees, division, Chord.FIFTH | Chord.OCTAVE, Chord.FIFTH, 0);
				if(bar != null)
				{
					if(!bar.isValid())
					{
						System.out.println("INVALID GENERATED BAR !!!!" + sig + " " + division);
						for(TimedElement te : bar)
						{
							System.out.println(te);
						}
					}
					else
					{
						track.add(bar);
					}
				}
			}
			
			MidiWriter writer = new MidiWriter("bar" + signature.numerator() + "_" + Duration.convertInTimeSignature(signature.denominator()) + ".mid");
			writer.addTrack(track);
			writer.write();
		}
	}
	
	private static void testSimpleMelody()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.random(4, true);
		System.out.println("Selected grid: " + grid);
		
		String[] signatures = {"2/2", "2/4", "2/8", "3/2", "3/4", "3/8", "4/2", "4/4", "4/8"};
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		
		for(String sig : signatures)
		{
			TimeSignature signature = new TimeSignature(sig);
			
			int[] degrees = new int[1];
			for(int i = 0; i < 4; i++)
			{
				degrees[0] = grid.get(i);
				//track.add(Bar.generateAccompanimentSimpleChords(signature, scale, degrees, true, Chord.FIFTH | Chord.OCTAVE, Chord.FIFTH, 0));
			}
		}
		
		int tempo = (int)(Math.random() * 100) + 60;
		System.out.println("Selected tempo: " + tempo);
		
		MidiWriter midiWriter = new MidiWriter("SimpleMelody.mid");
		midiWriter.tempo(tempo);
		midiWriter.scale(scale);
		
		midiWriter.addTrack(track);
		midiWriter.write();
	}
	
	public static void testChordInversion()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.random();
		System.out.println("Selected degree: " + Degree.toString(degree));

		Bar bar = new Bar();
		
		Chord chord = Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH);
		System.out.println(chord);
		
		bar.add(chord);
		
		for(int inversion = 1; inversion < 8; inversion++)
		{
			chord = Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			if(chord.reverseByTop(inversion))
			{
				System.out.println("inv " + chord);
				System.out.println("fundamental: " + chord.fundamental());
				
				bar.add(chord);
			}
		}
		
		bar.toMidiFile("chords.mid");
	}
	
	public static void testChordTransposition()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.random();
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Bar bar = new Bar();
		
		Chord chord;
		
		int transp = 1;
		while(true)
		{
			chord = Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			
			if(chord.transpose(transp))
			{
				System.out.println("up transp. " + chord);
				bar.add(chord);
			}
			else
			{
				break;
			}
			
			chord = Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			
			if(chord.transpose(transp * - 1))
			{
				System.out.println("down transp. " + chord);
				bar.add(chord);
			}
			else
			{
				break;
			}
			
			transp++;
		}
		
		bar.toMidiFile("transp.mid");
	}
	
	public static void testAutomaticChordTransposition()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.random(4, false);
		System.out.println("Selected grid: " + grid);
		
		Bar bar = new Bar();
		
		for(int degree : grid)
		{
			Chord chord = Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			System.out.println("Chord: " + chord);
			bar.add(chord);
		}
		
		bar.toMidiFile("automatic.mid");
	}
	
	public static void testChordGeneration()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		scale.toMidiFile("scale.mid");
		
		int degree = Degree.random();
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Bar bar = new Bar();

		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.FIFTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.SEVENTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.FIFTH | Chord.OCTAVE | Chord.THIRD));
		
		System.out.println("bar: " + bar);
		
		bar.toMidiFile("chordGeneration.mid");
	}
}

