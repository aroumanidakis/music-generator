package com.scythe.musicgenerator.main;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.DiatonicScale.Mode;
import com.scythe.musicgenerator.core.Degree;
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
		testBarFactory();
		//testFourChords();
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
					for(noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
					{
						if(scale.note(noteIndex).equals(firstNote))
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
					for(noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
					{
						if(scale.note(noteIndex).equals(previousNote))
						{
							break;
						}
					}
					
					int indexVar = ((int)(Math.random() * 5)) - 2;
					
					noteIndex += indexVar;
					
					if(noteIndex < 0)
					{
						noteIndex += scale.noteCnt();
					}
					
					noteIndex %= scale.noteCnt();
					
					element.add(scale.note(noteIndex));
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
			Note fundamental = scale.note(grid.get(degreeIndex));
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(grid.get(degreeIndex), Interval.Name.FIFTH, fifth);
			
			Note octave = new Note(fundamental);
			octave.octave(octave.octave() + 1);
			
			System.out.println("Power chord from degree " + Degree.toString(grid.get(degreeIndex)) + ": " + fundamental + " " + fifth + " " + octave);
			
			TimedElement timedElement = new TimedElement(Duration.DOUBLE, false);
			timedElement.add(fundamental);
			timedElement.add(fifth);
			timedElement.add(octave);
			
			Bar bar = new Bar(new TimeSignature("4/4"));
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
				
				Bar bar2 = new Bar(new TimeSignature("4/4"));
				
				TimedElement te = bar.get(0);
				Note note = new Note(te.get(0));
				note.octave(0);
				note.velocity(Note.Dynamics.FORTISSISSIMO);
				
				te = new TimedElement(Duration.SINGLE, false);
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
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(degree, Interval.Name.FIFTH, fifth);
			
			TimedElement te = new TimedElement(Duration.SINGLE, false);
			te.add(fundamental);
			te.add(fifth);
			
			bar.add(te);
		}
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		track.add(bar);
		
		MidiWriter midiWriter = new MidiWriter("fourChords.mid");
		midiWriter.tempo(60);
		midiWriter.addTrack(track, "intru");
		midiWriter.write();
	}
}

