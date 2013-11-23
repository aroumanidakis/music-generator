package com.scythe.musicgenerator.main;

import java.util.ArrayList;

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
		//generate();
		//testSimpleMelody();
		testChordInversion();
		testChordTransposition();
		testAutomaticChordInversionAndTransposition();
		testChordGeneration();
	}
	
	private static void generate()
	{
		DiatonicScale scale = DiatonicScale.random();
		System.out.println("Generated scale: " + scale);
		
		Grid grid = Grid.random(4, true);
		System.out.println("Generated grid: " + grid);
		
		System.out.println("Rhythm generation...");
		ArrayList<Bar> rhythmTrack = generatePowerChords(grid, scale);
		
		System.out.println("Melody generation...");
		ArrayList<Bar> melodyTrack = new ArrayList<Bar>();
		for(int barCnt = 0; barCnt < rhythmTrack.size(); barCnt++)
		{
			Bar bar = null;
			while(bar == null || bar.size() < 8)
			{
				bar = BarFactory.Melody.generateRhythm(new TimeSignature("4/4"));
			}
			
			for(int i = 0; i < bar.size(); i++)
			{
				TimedElement element = bar.get(i);
				
				if(i == 0)
				{
					Note firstNote = rhythmTrack.get(barCnt).get(0).get(0);
					
					int noteIndex;
					for(noteIndex = 0; noteIndex < scale.size(); noteIndex++)
					{
						if(scale.get(noteIndex).equals(firstNote))
						{
							break;
						}
					}
					
					if(Math.random() < 0.5)
					{
						scale.getNoteAtUpperInterval(noteIndex, Interval.Name.FIFTH, firstNote);
					}
					
					element.add(firstNote);
				}
				else
				{
					Note previousNote = bar.get(i - 1).get(0);
					int noteIndex;
					for(noteIndex = 0; noteIndex < scale.size(); noteIndex++)
					{
						if(scale.get(noteIndex).equals(previousNote))
						{
							break;
						}
					}
					
					int indexVar = ((int)(Math.random() * 5)) - 2;
					
					noteIndex += indexVar;
					
					if(noteIndex < 0)
					{
						noteIndex += scale.size();
					}
					
					noteIndex %= scale.size();
					
					element.add(scale.get(noteIndex));
				}
			}
			
			System.out.println("Modified bar: " + bar);
			melodyTrack.add(bar);
		}
		
		MidiWriter midiWriter = new MidiWriter("song.mid");
		midiWriter.addTrack(melodyTrack, "melody");
		midiWriter.addTrack(rhythmTrack, "rhythm");
		midiWriter.write();
	}
	
	private static boolean validateGridForScale(Grid grid, DiatonicScale scale)
	{
		for(int degreeIndex = 0; degreeIndex < grid.size(); degreeIndex++)
		{
			Note fifth = new Note();
			int qualification = scale.getNoteAtUpperInterval(grid.get(degreeIndex), Interval.Name.FIFTH, fifth);
			
			if(qualification != Interval.Qualification.PERFECT)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static ArrayList<Bar> generatePowerChords(Grid grid, DiatonicScale scale)
	{
		ArrayList<Bar> track = new ArrayList<Bar>();
		
		for(int degreeIndex = 0; degreeIndex < grid.size(); degreeIndex++)
		{
			Note fundamental = scale.get(grid.get(degreeIndex));
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(grid.get(degreeIndex), Interval.Name.FIFTH, fifth);
			
			Note octave = new Note(fundamental);
			octave.octave(octave.octave() + 1);
			
			System.out.println("Power chord from degree " + Degree.toString(grid.get(degreeIndex)) + ": " + fundamental + " " + fifth + " " + octave);
			
			TimedElement timedElement = new TimedElement(Duration.DOUBLE);
			timedElement.add(fundamental);
			timedElement.add(fifth);
			timedElement.add(octave);
			
			Bar bar = new Bar();
			bar.add(timedElement);
			bar.add(timedElement);
			
			track.add(bar);
		}
		
		return track;
	}
	
	private static void testSimpleMelody()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.random(4, true);
		System.out.println("Selected grid: " + grid);
		
		TimeSignature signature = new TimeSignature((Math.random() < 0.5) ? "4/4" : "6/8");
		System.out.println("Selected time signature: " + signature);
		
		ArrayList<Bar> bassTrack = new ArrayList<Bar>();
		ArrayList<Bar> accompanimentTrack = new ArrayList<Bar>();
		ArrayList<Bar> melodyTrack = new ArrayList<Bar>();
		
		int nbQuarterNotePerDegree = (signature.equals(new TimeSignature("4/4"))) ? 4 : 3;
		
		int[] degrees = new int[2];
		for(int i = 0; i < 2; i++)
		{
			Bar bar;
			
			degrees[0] = grid.get((2 * i));
			degrees[1] = grid.get((2 * i) + 1);
			
			bar = BarFactory.Bass.generateSimple(signature, scale, degrees);
			bassTrack.add(bar);
			
			bar = BarFactory.Accompaniment.generateSimple(signature, scale, degrees);
			accompanimentTrack.add(bar);

			bar = new Bar(signature);
			
			for(int currentDegree = 2 * i; currentDegree <= (2 * i) + 1; currentDegree++)
			{
				for(int noteIndex = 0; noteIndex < nbQuarterNotePerDegree; noteIndex++)
				{
					TimedElement te = new TimedElement(Duration.HALF);
					if(true)//noteIndex == 0)
					{
						Note baseNote;
						
						if(Math.random() < 0.5)
						{
							baseNote = new Note(scale.get(currentDegree));
						}
						else
						{
							baseNote = new Note();
							scale.getNoteAtUpperInterval(currentDegree, Interval.Name.FIFTH, baseNote);
						}
						
						baseNote.octave(baseNote.octave() + 2);
						te.add(baseNote);
					}
					
					bar.add(te);
				}
			}
			
			melodyTrack.add(bar);
		}
		
		int tempo = (int)(Math.random() * 100) + 60;
		System.out.println("Selected tempo: " + tempo);
		
		MidiWriter midiWriter = new MidiWriter("SimpleMelody.mid");
		midiWriter.tempo(tempo);
		midiWriter.scale(scale);
		
		bassTrack.addAll(bassTrack);
		accompanimentTrack.addAll(accompanimentTrack);
		melodyTrack.addAll(melodyTrack);
		
		midiWriter.addTrack(bassTrack, "bass");
		midiWriter.addTrack(accompanimentTrack, "accompaniment");
		midiWriter.addTrack(melodyTrack, "melody");
		midiWriter.write();
	}
	
	public static void testChordInversion()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.random();
		System.out.println("Selected degree: " + Degree.toString(degree));

		Bar bar = new Bar();
		
		Chord chord = Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH);
		System.out.println(chord);
		
		bar.add(chord);
		
		for(int inversion = 1; inversion < 8; inversion++)
		{
			chord = Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			if(chord.reverse(inversion))
			{
				System.out.println("inv " + chord);
				bar.add(chord);
			}
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter("chords.mid");
		midiWriter.addTrack(track);
		midiWriter.write();
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
			chord = Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			
			if(chord.transpose(transp))
			{
				System.out.println("up transp. " + chord);
				bar.add(chord);
			}
			else
			{
				break;
			}
			
			chord = Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH);
			
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
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter writer = new MidiWriter("transp.mid");
		writer.addTrack(track);
		writer.write();
	}
	
	public static void testAutomaticChordInversionAndTransposition()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.random(4, false);
		System.out.println("Selected grid: " + grid);
		
		Bar bar = new Bar();
		
		for(int degree : grid)
		{
			Chord chord = Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH | Chord.SEVENTH);
			System.out.println("Chord: " + chord);
			bar.add(chord);
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter writer = new MidiWriter("automatic.mid");
		writer.addTrack(track);
		writer.write();
	}
	
	public static void testChordGeneration()
	{
		DiatonicScale scale = DiatonicScale.random((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.random();
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Bar bar = new Bar();

		bar.add(Chord.generate(Duration.SINGLE, false, scale, degree, Chord.FIFTH));
		bar.add(Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.FIFTH));
		bar.add(Chord.generate(Duration.SINGLE, false, scale, degree, Chord.THIRD | Chord.SEVENTH));
		bar.add(Chord.generate(Duration.SINGLE, false, scale, degree, Chord.FIFTH | Chord.OCTAVE | Chord.THIRD));
		
		System.out.println("bar: " + bar);
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter writer = new MidiWriter("chordGeneration.mid");
		writer.addTrack(track);
		writer.write();
	}
}

