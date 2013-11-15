package com.scythe.musicgenerator.main;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.BarSignature;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.Grid;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimedElement;
import com.scythe.musicgenerator.defines.Degree;
import com.scythe.musicgenerator.defines.Duration;
import com.scythe.musicgenerator.defines.Interval;
import com.scythe.musicgenerator.defines.Mode;
import com.scythe.musicgenerator.factories.BarFactory;
import com.scythe.musicgenerator.midi.MidiWriter;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		//generate();
		testBarFactory();
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
			while(bar == null || bar.elements().size() < 8)
			{
				bar = BarFactory.Melody.generateRhythm(new BarSignature("4/4"));
			}
			
			ArrayList<TimedElement> elements = bar.elements();
			for(int i = 0; i < elements.size(); i++)
			{
				TimedElement element = elements.get(i);
				
				if(i == 0)
				{
					Note firstNote = rhythmTrack.get(barCnt).elements().get(0).notes().get(0);
					
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
					
					element.addNote(firstNote);
				}
				else
				{
					Note previousNote = elements.get(i - 1).notes().get(0);
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
					
					element.addNote(scale.note(noteIndex));
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
		ArrayList<Integer> degrees = new ArrayList<Integer>();
		
		degrees.add(Degree.I);
		for(int i = 0; i < 3; i++)
		{
			int degree = (int)(Math.random() * Degree.COUNT);
			degrees.add(degree);
		}
		
		return new Grid(degrees);
	}
	
	public static boolean validateGridForScale(Grid grid, DiatonicScale scale)
	{
		for(int degreeIndex = 0; degreeIndex < grid.degreesCnt(); degreeIndex++)
		{
			Note fifth = new Note();
			int qualification = scale.getNoteAtUpperInterval(grid.degree(degreeIndex), Interval.Name.FIFTH, fifth);
			
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
		
		for(int degreeIndex = 0; degreeIndex < grid.degreesCnt(); degreeIndex++)
		{
			Note fundamental = scale.note(grid.degree(degreeIndex));
			
			Note fifth = new Note();
			scale.getNoteAtUpperInterval(grid.degree(degreeIndex), Interval.Name.FIFTH, fifth);
			
			Note octave = new Note(fundamental);
			octave.octave(octave.octave() + 1);
			
			System.out.println("Power chord from degree " + Degree.toString(grid.degree(degreeIndex)) + ": " + fundamental + " " + fifth + " " + octave);
			
			TimedElement timedElement = new TimedElement(Duration.DOUBLE, false);
			timedElement.addNote(fundamental);
			timedElement.addNote(fifth);
			timedElement.addNote(octave);
			
			ArrayList<TimedElement> timedElements = new ArrayList<TimedElement>();
			timedElements.add(timedElement);
			timedElements.add(timedElement);
			
			track.add(new Bar(new BarSignature("4/4"), timedElements));
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
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		
		for(int noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
		{
			int[] degrees = new int[1];
			degrees[0] = noteIndex;
			track.add(BarFactory.Accompaniment.generateSimple(new BarSignature("6/8"), scale, degrees));
		}
		
		MidiWriter midiWriter = new MidiWriter("accompaniment.mid");
		midiWriter.tempo(100);
		midiWriter.scale(scale);
		midiWriter.addTrack(track, "accompaniment");
		midiWriter.write();
		
		System.out.println("accompaniment.mid written.");
	}
}

