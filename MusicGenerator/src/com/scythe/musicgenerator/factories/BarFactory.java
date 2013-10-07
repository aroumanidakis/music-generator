package com.scythe.musicgenerator.factories;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimedNote;
import com.scythe.musicgenerator.defines.Duration;

public class BarFactory
{
	public static Bar generateNonMelodic(int[] rhythmSignature)
	{
		ArrayList<TimedNoteToCut> notesToCut = createBeginningNotes(rhythmSignature[0] * Duration.convert(rhythmSignature[1]));
		
		TimedNote[] notes = new TimedNote[notesToCut.size()];
		for(int i = 0; i < notes.length; i++)
		{
			notes[i] = notesToCut.get(i).note();
		}
		
		return new Bar(rhythmSignature, notes);
	}
	
	private static ArrayList<TimedNoteToCut> createBeginningNotes(float totalTime)
	{
		ArrayList<TimedNoteToCut> notes = new ArrayList<TimedNoteToCut>();
		
		float timeToAdd = totalTime;
		while(timeToAdd > 0)
		{
			for(int duration = Duration.QUADRUPLE; duration <= Duration.NB_DURATIONS; duration++)
			{
				if((Duration.convert(duration) * 1.5) <= timeToAdd)
				{
					notes.add(new TimedNoteToCut(new TimedNote(new Note(Note.Name.A), duration, true), true));
					timeToAdd -= Duration.convert(duration) * 1.5;
					break;
				}
				
				if(Duration.convert(duration) <= timeToAdd)
				{		
					notes.add(new TimedNoteToCut(new TimedNote(new Note(Note.Name.A), duration, false), true));
					timeToAdd -= Duration.convert(duration);
					break;
				}
			}
		}
		
		return notes;
	}
	
	private static class TimedNoteToCut
	{
		public TimedNoteToCut(TimedNote note, boolean cut)
		{
			mNote = note;
			mCut = cut;
		}
		
		public TimedNote note()
		{
			return mNote;
		}
		
		public void note(TimedNote note)
		{
			mNote = note;
		}
		
		public boolean cut()
		{
			return mCut;
		}
		
		public void cut(boolean cut)
		{
			mCut = cut;
		}
		
		private TimedNote mNote;
		private boolean mCut;
	}
}
