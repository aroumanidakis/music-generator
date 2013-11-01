package com.scythe.musicgenerator.main;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.BarSignature;
import com.scythe.musicgenerator.core.Grid;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.Scale;
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
		validBarTest();
		scaleInstanciationTest();
		perfectFifthTest();
		barFactoryTest();
		midiWriterTest();
		gridTest();
	}
	
	public static void validBarTest()
	{
		System.out.println("VALID BAR TEST\n");
		
		ArrayList<TimedElement> timedElements = new ArrayList<TimedElement>();
		timedElements.add(new TimedElement(Duration.SINGLE, false));
		timedElements.add(new TimedElement(Duration.SINGLE, false));
		timedElements.add(new TimedElement(Duration.SINGLE, false));
		timedElements.add(new TimedElement(Duration.SINGLE, false));
		
		Bar bar = new Bar(new BarSignature("4/4"), timedElements);
		
		System.out.println("Bar is fully valid: " + (bar.isValid() ? "YES" : "NO"));
		System.out.println();
	}
	
	public static void scaleInstanciationTest()
	{
		for(int name = 0; name < Note.Name.COUNT; name++)
		{
			for(int accidental = 0; accidental < Note.Accidental.COUNT; accidental++)
			{
				Note tonic = new Note(name, accidental);
				for(int mode = 0; mode < Mode.COUNT; mode++)
				{
					Scale scale = new Scale(tonic, mode);
					System.out.println(scale);
				}
			}
		}
	}
	
	public static void perfectFifthTest()
	{
		System.out.println("PERFECT FIFTH TEST\n");
		
		Note tonic = new Note(Note.Name.C, Note.Accidental.NONE);
		
		for(int mode = 0; mode < Mode.COUNT; mode++)
		{
			Scale scale = new Scale(tonic, mode);
			System.out.println("Selected scale: " + scale);
			
			for(int noteIndex = 0; noteIndex < scale.noteCnt(); noteIndex++)
			{
				Note baseNote = scale.note(noteIndex);
				Note perfectFifth = baseNote.getNoteAtUpperInterval(Interval.PERFECT_FIFTH, scale.accidental());
				
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
			Bar bar = null;
			
			if(Math.random() < 0.5f)
			{
				bar = BarFactory.generateRhythm(new BarSignature("4/4"));
			}
			else
			{
				bar = BarFactory.generateRhythm(new BarSignature("6/8"));
			}
			
			String barStr = bar.toString();
			System.out.println(barStr + (bar.isValid() ? " (valid)" : " (not valid)"));
			
			barStr += "\n";
			
			file.write(barStr.getBytes());
		}
		
		file.close();
		System.out.println();
	}
	
	public static void midiWriterTest()
	{
		System.out.println("MIDI WRITER TEST\n");
		
		MidiWriter midiWriter = new MidiWriter("output.mid");
		midiWriter.addTrack(new ArrayList<Bar>());
		
		if(Math.random() < 0.5)
		{
			midiWriter.addTrack(new ArrayList<Bar>());
		}
		
		midiWriter.write();
		
		System.out.println("Midi file writen.");
		System.out.println();
	}
	
	public static void gridTest()
	{
		System.out.println("GRID TEST\n");
		
		ArrayList<Integer> degrees = new ArrayList<Integer>();
		
		degrees.add(Degree.I);
		for(int i = 0; i < 3; i++)
		{
			int degree = (int)(Math.random() * Degree.COUNT);
			degrees.add(degree);
		}
		
		Grid grid = new Grid(degrees);
		
		System.out.println("Selected grid: " + grid);
		
		Scale scale = new Scale(new Note(Note.Name.C), Mode.IONIAN);
		
		System.out.println("Selected scale: " + scale);
		
		ArrayList<TimedElement> timedElements = new ArrayList<TimedElement>();
		
		for(int i = 0; i < 4; i++)
		{
			Note fundamental = scale.note(grid.degree(i));
			Note perfectFifth = fundamental.getNoteAtUpperInterval(Interval.PERFECT_FIFTH, scale.accidental());
			
			if(!scale.isIn(perfectFifth, true))
			{
				System.out.println("Warning: powerchord is not in the scale");
			}
			
			Note octave = fundamental.getNoteAtUpperInterval(12, scale.accidental());
			
			TimedElement timedElement = new TimedElement(Duration.SINGLE, false);
			timedElement.addNote(fundamental);
			timedElement.addNote(perfectFifth);
			timedElement.addNote(octave);
			
			timedElements.add(timedElement);
		}
		
		Bar bar = new Bar(new BarSignature("4/4"), timedElements);
		
		System.out.println("Resulting bar: " + bar);
		System.out.println();
	}
}

