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
		//testBarFactory();
		//testFourChords();
		//testSimpleMelody();
		//testChordInversion();
		//testChordTransposition();
		testAutomaticChordInversionAndTransposition();
	}
	
	public static void generate()
	{
		System.out.println("Scale generation...");
		
		DiatonicScale scale = null;
		while(scale == null || !scale.isValid() || scale.hasStrangeNote())
		{
			scale = generateRandomScale();
		}
		
		System.out.println("Generated scale: " + scale);
		System.out.println("Grid generation...");
		
		Grid grid = null;
		while(grid == null || !validateGridForScale(grid, scale))
		{
			grid = generateRandomGrid();
		}
		
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
	
	public static DiatonicScale generateRandomScale()
	{
		int mode = (int)(Math.random() * Mode.COUNT);
		int tonicName = (int)(Math.random() * Note.Name.COUNT);
		int tonicAccidental = (int)(Math.random() * Note.Accidental.COUNT);
		return new DiatonicScale(new Note(tonicName, tonicAccidental), mode);
	}
	
	public static Grid generateRandomGrid()
	{
		Grid grid = new Grid();
		
		grid.add(Degree.I);
		for(int i = 0; i < 3; i++)
		{
			int degree = (int)(Math.random() * Degree.COUNT);
			grid.add(degree);
		}
		
		return grid;
	}
	
	public static boolean validateGridForScale(Grid grid, DiatonicScale scale)
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
	
	public static ArrayList<Bar> generatePowerChords(Grid grid, DiatonicScale scale)
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
	
	private static void testBarFactory()
	{
		DiatonicScale scale = null;
		while(scale == null || !scale.isValid())
		{
			Note tonic = new Note((int)(Math.random() * Note.Name.COUNT), (int)(Math.random() * Note.Accidental.COUNT));
			int mode = (Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN;
			scale = new DiatonicScale(tonic, mode);
		}

		System.out.println(scale);
		
		Grid grid = new Grid();
		grid.add(Degree.I);
		grid.add(Degree.VI);
		grid.add(Degree.IV);
		grid.add(Degree.V);
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		ArrayList<Bar> track2 = new ArrayList<Bar>();
		
		for(int i = 0; i < 4; i++)
		{
			for(int degree : grid)
			{
				int[] degrees = new int[1];
				degrees[0] = degree;
				Bar bar = BarFactory.Accompaniment.generateSimple(new TimeSignature("4/4"), scale, degrees);
				track.add(bar);
				
				Bar bar2 = new Bar();
				
				TimedElement te = bar.get(0);
				Note note = new Note(te.get(0));
				note.octave(0);
				note.dynamics(Note.Dynamics.FORTISSISSIMO);
				
				te = new TimedElement(Duration.SINGLE);
				te.add(note);
				
				for(int j = 0; j < 4; j++)
				{
					bar2.add(te);
				}
				
				track2.add(bar2);
			}
		}
		
		MidiWriter midiWriter = new MidiWriter("accompaniment.mid");
		midiWriter.tempo(140);
		midiWriter.scale(scale);
		midiWriter.addTrack(track, "accompaniment");
		midiWriter.addTrack(track2, "bass");
		midiWriter.write();
		
		System.out.println("accompaniment.mid written.");
	}
	
	private static void testFourChords()
	{
		Note.defaultOctave(2);
		
		Note tonic = new Note(Note.Name.F, Note.Accidental.SHARP);
		int mode = Mode.EOLIAN;
		TimeSignature signature = new TimeSignature("4/4");
		
		DiatonicScale scale = new DiatonicScale(tonic, mode);
		
		Bar bar = new Bar(signature);
		for(int degree : new int[]{Degree.I, Degree.VI, Degree.IV, Degree.V})
		{
			Note fundamental = scale.get(degree);
			
			Note third = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.THIRD, third);
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, fifth);
			
			Note seventh = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.SEVENTH, seventh);
			
			TimedElement te = new TimedElement(Duration.SINGLE);
			te.add(fundamental);
			te.add(third);
			te.add(fifth);
			te.add(seventh);
			
			bar.add(te);
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter("fourChords.mid");
		midiWriter.tempo(60);
		midiWriter.addTrack(track, "intru");
		midiWriter.write();
	}
	
	private static void testSimpleMelody()
	{
		DiatonicScale scale = null;
		
		while(scale == null || scale.hasStrangeNote() || !scale.isValid())
		{
			Note tonic = new Note((int)(Math.random() * Note.Name.COUNT), (int)(Math.random() * Note.Accidental.COUNT));
			int mode = (Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN;
			scale = new DiatonicScale(tonic, mode);
		}
		
		System.out.println("Selected scale: " + scale);
		
		Grid grid = new Grid();
		grid.add(Degree.I);
		grid.add((int)(Math.random() * Degree.COUNT));
		grid.add((int)(Math.random() * Degree.COUNT));
		grid.add((Math.random() < 0.5) ? Degree.I : Degree.V);
		
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
		DiatonicScale scale = null;
		
		while(scale == null || scale.hasStrangeNote() || !scale.isValid())
		{
			Note tonic = new Note((int)(Math.random() * Note.Name.COUNT), (int)(Math.random() * Note.Accidental.COUNT));
			int mode = (Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN;
			scale = new DiatonicScale(tonic, mode);
		}
		
		System.out.println("Selected scale: " + scale);
		
		int degree = (int)(Math.random() * Degree.COUNT);
		
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Note fundamental = scale.get(degree);
		
		Note third = new Note();
		scale.getNoteAtUpperInterval(degree, Interval.Name.THIRD, third);
		
		Note fifth = new Note();
		scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, fifth);
		
		Note seventh = new Note();
		scale.getNoteAtUpperInterval(degree, Interval.Name.SEVENTH, seventh);
		
		Bar bar = new Bar();
		
		Chord chord = new Chord(Duration.SINGLE, false);
		
		chord.add(fundamental);
		chord.add(third);
		chord.add(fifth);
		chord.add(seventh);
		System.out.println(chord);
		
		bar.add(chord);
		
		for(int inversion = 1; inversion < 54; inversion++)
		{
			chord = new Chord(Duration.SINGLE, false);
			
			chord.add(fundamental);
			chord.add(third);
			chord.add(fifth);
			chord.add(seventh);
			
			chord.reverse(inversion);
			System.out.println(chord);
			
			bar.add(chord);
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter("chords.mid");
		midiWriter.addTrack(track);
		midiWriter.write();
	}
	
	public static void testChordTransposition()
	{
		DiatonicScale scale = null;
		
		while(scale == null || scale.hasStrangeNote() || !scale.isValid())
		{
			Note tonic = new Note((int)(Math.random() * Note.Name.COUNT), (int)(Math.random() * Note.Accidental.COUNT));
			int mode = (Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN;
			scale = new DiatonicScale(tonic, mode);
		}
		
		System.out.println("Selected scale: " + scale);
		
		int degree = (int)(Math.random() * Degree.COUNT);
		
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Note fundamental = scale.get(degree);
		
		Note third = new Note();
		scale.getNoteAtUpperInterval(degree, Interval.Name.THIRD, third);
		
		Note fifth = new Note();
		scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, fifth);
		
		Chord chord = new Chord(Duration.SINGLE, false);
		
		chord.add(fundamental);
		chord.add(third);
		chord.add(fifth);
		System.out.println(chord);
		
		int transp = (Math.random() < 0.5) ? 1 : -1;
		while(true)
		{
			if(!chord.transpose(transp))
			{
				break;
			}
			
			System.out.println("transp. " + chord);
		}
	}
	
	public static void testAutomaticChordInversionAndTransposition()
	{
		DiatonicScale scale = null;
		
		while(scale == null || scale.hasStrangeNote() || !scale.isValid())
		{
			Note tonic = new Note((int)(Math.random() * Note.Name.COUNT), (int)(Math.random() * Note.Accidental.COUNT));
			int mode = (Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN;
			scale = new DiatonicScale(tonic, mode);
		}
		
		System.out.println("Selected scale: " + scale);
		
		Grid grid = new Grid();
		
		for(int i = 0; i < 4; i++)
		{
			grid.add((int)(Math.random() * Degree.COUNT));
		}
		
		System.out.println("Selected grid: " + grid);
		
		Bar bar = new Bar();
		
		for(int degree : grid)
		{
			Note fundamental = scale.get(degree);
			
			Note third = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.THIRD, third);
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, fifth);
			
			Note seventh = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.SEVENTH, seventh);
			
			Chord chord = new Chord(Duration.SINGLE);
			chord.add(fundamental);
			chord.add(third);
			chord.add(fifth);
			chord.add(seventh);
			
			System.out.println("Chord: " + chord);
			
			bar.add(chord);
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter writer = new MidiWriter("automatic.mid");
		writer.addTrack(track);
		writer.write();
	}
}

