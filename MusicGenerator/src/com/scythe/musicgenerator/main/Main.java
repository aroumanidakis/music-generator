package com.scythe.musicgenerator.main;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Chord;
import com.scythe.musicgenerator.core.Degree;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.Interval;
import com.scythe.musicgenerator.core.DiatonicScale.Mode;
import com.scythe.musicgenerator.core.Grid;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimeSignature;
import com.scythe.musicgenerator.core.TimedElement;
import com.scythe.musicgenerator.core.TimedElement.Duration;
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
		testArpeggio();
		/*
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
				bar = Bar.generateSimple(signature, scale, degrees, division, Chord.FIFTH | Chord.OCTAVE, Chord.FIFTH, 0);
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
		*/
		
		DiatonicScale scale;
		Grid grid;
		
		ArrayList<Bar> track = new ArrayList<Bar>();
		for(int i = 0; i < 2; i++)
		{
			scale = new DiatonicScale(new Note(Note.Name.D), (i == 0) ? Mode.IONIAN : Mode.EOLIAN);
			
			grid = new Grid(); // Pachelbel canon
			grid.add(Degree.I);
			grid.add(Degree.V);
			grid.add(Degree.VI);
			grid.add(Degree.III);
			grid.add(Degree.IV);
			grid.add(Degree.I);
			grid.add(Degree.IV);
			grid.add(Degree.V);
			
			Bar fundamentalChords = new Bar();
			Bar reversedChords = new Bar();
			
			for(int degree : grid)
			{
				fundamentalChords.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH));
				reversedChords.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH));
			}
			
			compressChords(reversedChords);
			
			track.add(fundamentalChords);
			track.add(reversedChords);
		}
		
		MidiWriter midiWriter = new MidiWriter("pachelbel.mid");
		midiWriter.setTempo(50);
		midiWriter.addTrack(track);
		midiWriter.write();
		
		scale = DiatonicScale.getRandom();
		System.out.println(scale);
		
		grid = Grid.getRandom(4,  true);
		System.out.println(grid);
		
		track.clear();
		Bar globalBar = new Bar();
		for(int degree : grid)
		{
			Bar bar = Bar.generateSimple(new TimeSignature("4/4"), scale, new int[]{degree}, 1, Chord.THIRD | Chord.FIFTH | Chord.OCTAVE, Chord.THIRD | Chord.FIFTH, Chord.THIRD | Chord.FIFTH);
			if(bar != null)
			{
				globalBar.addAll(bar);
			}
		}
		
		compressChords(globalBar);
		track.add(globalBar);
		
		midiWriter = new MidiWriter("testReverse.mid");
		midiWriter.addTrack(track);
		midiWriter.write();
	}
	
	private static void testSimpleMelody()
	{
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.getRandom(4, true);
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
				track.add(Bar.generateSimple(signature, scale, degrees, 1, Chord.FIFTH | Chord.OCTAVE, Chord.FIFTH, 0));
			}
		}
		
		int tempo = (int)(Math.random() * 100) + 60;
		System.out.println("Selected tempo: " + tempo);
		
		MidiWriter midiWriter = new MidiWriter("SimpleMelody.mid");
		midiWriter.setTempo(tempo);
		midiWriter.setKeySignature(scale);
		
		midiWriter.addTrack(track);
		midiWriter.write();
	}
	
	public static void testChordInversion()
	{
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.getRandom();
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
				System.out.println("fundamental: " + chord.getFundamental());
				
				bar.add(chord);
			}
		}
		
		bar.toMidiFile("chords.mid");
	}
	
	public static void testChordTransposition()
	{
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		int degree = Degree.getRandom();
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
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.getRandom(4, false);
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
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		scale.toMidiFile("scale.mid");
		
		int degree = Degree.getRandom();
		System.out.println("Selected degree: " + Degree.toString(degree));
		
		Bar bar = new Bar();

		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.FIFTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.FIFTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.THIRD | Chord.SEVENTH));
		bar.add(Chord.generate(Duration.QUARTER, false, scale, degree, Chord.FIFTH | Chord.OCTAVE | Chord.THIRD));
		
		System.out.println("bar: " + bar);
		
		bar.toMidiFile("chordGeneration.mid");
	}
	
	public static void testArpeggio()
	{
		DiatonicScale scale = DiatonicScale.getRandom((Math.random() < 0.5) ? Mode.IONIAN : Mode.EOLIAN);
		System.out.println("Selected scale: " + scale);
		
		Grid grid = Grid.getRandom(4, true);
		System.out.println("Selected grid: " + grid);
		
		String[] signatures = {"2/1", "2/2", "2/4", "2/8", "4/1", "4/2", "4/4", "4/8", "6/2", "6/4", "6/8", "6/16"};
		TimeSignature signature = new TimeSignature(signatures[(int)(Math.random() * (signatures.length - 1))]);
		
		System.out.println("Selected signature: " + signature);
		
		ArrayList<Bar> accompaniment = new ArrayList<Bar>();
		ArrayList<Bar> arpeggio = new ArrayList<Bar>();
		
		Bar bar;
		for(int degree : grid)
		{
			int[] degrees = {degree};
			bar = Bar.generateSimple(signature, scale, degrees, 0, Chord.THIRD | Chord.FIFTH | Chord.SEVENTH | Chord.OCTAVE, Chord.THIRD | Chord.FIFTH | Chord.SEVENTH, Chord.THIRD | Chord.FIFTH);
			accompaniment.add(bar);
			
			bar = Bar.generateArpeggio(signature, scale, degree);
			arpeggio.add(bar);
		}
		
		for(int i = 0; i < 2; i++)
		{
			accompaniment.addAll(accompaniment);
			arpeggio.addAll(arpeggio);
		}
		
		MidiWriter midiWriter = new MidiWriter("arpeggio.mid");
		midiWriter.setTempo(180);
		midiWriter.addTrack(accompaniment, "accompaniment");
		midiWriter.addTrack(arpeggio, "arpeggio");
		midiWriter.write();
	}
	
	public static void compressChords(Bar chords)
	{
		Note[] selectedNotes = new Note[4];
		int[] selectedChords = new int[2];
		
		boolean reversedByBottom;
		int reversedChordIndex;
		
		while(true)
		{
			int previousAmplitude = getChordArrayAmplitude(chords);
			
			TimedElement chord = chords.get(0);
			
			selectedNotes[0] = chord.get(0);
			selectedNotes[1] = chord.get(1);
			selectedNotes[2] = chord.get(chord.size() - 2);
			selectedNotes[3] = chord.get(chord.size() - 1);
			
			selectedChords[0] = 0;
			selectedChords[1] = 0;
			
			for(int i = 1; i < chords.size(); i++)
			{
				chord = chords.get(i);
				
				if(chord.get(0).compare(selectedNotes[0]) == 1)
				{
					selectedNotes[0] = chord.get(0);
					selectedChords[0] = i;
				}
				
				if((chord.get(1).compare(selectedNotes[1]) == 1) && (!chord.get(1).equals(selectedNotes[0])))
				{
					selectedNotes[1] = chord.get(1);
				}
				
				if(chord.get(chord.size() - 1).compare(selectedNotes[3]) == -1)
				{
					selectedNotes[3] = chord.get(chord.size() - 1);
					selectedChords[1] = i;
				}
				
				if((chord.get(chord.size() - 2).compare(selectedNotes[2]) == -1) && (!chord.get(chord.size() - 2).equals(selectedNotes[2])))
				{
					selectedNotes[2] = chord.get(chord.size() - 2);
				}
			}
			
			int lowNotesDiff = Note.getHalfToneDifference(selectedNotes[0], selectedNotes[1], true);
			int highNotesDiff = Note.getHalfToneDifference(selectedNotes[2], selectedNotes[3], true);
			
			if(lowNotesDiff >= highNotesDiff)
			{
				reversedByBottom = true;
				reversedChordIndex = selectedChords[0];
				((Chord)(chords.get(reversedChordIndex))).reverseByBottom(1);
			}
			else
			{
				reversedByBottom = false;
				reversedChordIndex = selectedChords[1];
				((Chord)chords.get(reversedChordIndex)).reverseByTop(1);
			}
			
			int newAmplitude = getChordArrayAmplitude(chords);

			if(newAmplitude > previousAmplitude)
			{
				if(reversedByBottom)
				{
					((Chord)(chords.get(reversedChordIndex))).reverseByTop(1);
				}
				else
				{
					((Chord)(chords.get(reversedChordIndex))).reverseByBottom(1);
				}
				
				break;
			}
			
			System.out.println("Chord " + reversedChordIndex + " reversed by " + ((reversedByBottom) ? "bottom" : "top"));
		}
	}
	
	public static int getChordArrayAmplitude(Bar chords)
	{
		TimedElement chord = chords.get(0);
		Note lowerNote = chord.get(0);
		Note higherNote = chord.get(chord.size() - 1);
		
		for(int i = 1; i < chords.size(); i++)
		{
			chord = chords.get(i);
			
			if(chord.get(0).compare(lowerNote) == 1)
			{
				lowerNote = chord.get(0);
			}
			
			if(chord.get(chord.size() - 1).compare(higherNote) == -1)
			{
				higherNote = chord.get(chord.size() - 1);
			}
		}
		
		return Note.getHalfToneDifference(lowerNote, higherNote, true);
	}
}

