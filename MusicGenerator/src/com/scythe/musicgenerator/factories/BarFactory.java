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
		ArrayList<TimedNoteToCut> notesToCut = createBeginningNotes(rhythmSignature[0] * Duration.convertInTime(rhythmSignature[1]));
		
		boolean newCuts = true;
		while(newCuts)
		{
			newCuts = false;
			for(int i = 0; i < notesToCut.size(); i++)
			{
				if(notesToCut.get(i).cuttingChance() != 0)
				{
					ArrayList<TimedNoteToCut> cuts = notesToCut.get(i).randomCutting();
					
					notesToCut.remove(i);
					notesToCut.addAll(i, cuts);
					
					newCuts = true;
					break;
				}
			}
		}
		
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
			for(int duration = 0; duration < Duration.NB_DURATIONS; duration++)
			{
				if((Duration.convertInTime(duration) * 1.5) <= timeToAdd)
				{
					notes.add(new TimedNoteToCut(new TimedNote(new Note(Note.Name.A), duration, true), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration) * 1.5;
					break;
				}
				
				if(Duration.convertInTime(duration) <= timeToAdd)
				{
					notes.add(new TimedNoteToCut(new TimedNote(new Note(Note.Name.A), duration, false), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration);
					break;
				}
			}
		}
		
		for(int i = 0; i < notes.size(); i++)
		{
			int randomIndex = (int)(Math.random() * notes.size());
			
			TimedNoteToCut tmp = notes.get(i);
			notes.set(i, notes.get(randomIndex));
			notes.set(randomIndex, tmp);
		}
		
		return notes;
	}
	
	private static float cuttingChanceCoeff = 0.85f;
	
	private static class TimedNoteToCut
	{
		public TimedNoteToCut(TimedNote note, float cuttingChance)
		{
			mNote = note;
			mCuttingChance = cuttingChance;
		}
		
		public ArrayList<TimedNoteToCut> randomCutting()
		{
			ArrayList<TimedNoteToCut> cuts = new ArrayList<TimedNoteToCut>();
			TimedNote note1, note2;
			
			if(mNote.duration() != Duration.QUARTER)
			{
				if(Math.random() < mCuttingChance)
				{
					if(mNote.dotted())
					{
						note1 = new TimedNote(mNote.note(), mNote.duration(), false);
						note2 = new TimedNote(mNote.note(), mNote.duration() + 1, false);
					}
					else
					{
						if(mNote.duration() == Duration.HALF)
						{
							note1 = new TimedNote(mNote.note(), mNote.duration() + 1, false);
							note2 = note1;
						}
						else
						{
							if(Math.random() < 0.4)
							{	
								note1 = new TimedNote(mNote.note(), mNote.duration() + 1, false);
								note2 = note1;
							}
							else
							{
								note1 = new TimedNote(mNote.note(), mNote.duration() + 1, true);
								note2 = new TimedNote(mNote.note(), mNote.duration() + 2, false);
							}
						}
					}
					
					cuts.add(new TimedNoteToCut(note1, mCuttingChance * cuttingChanceCoeff));
					cuts.add(new TimedNoteToCut(note2, mCuttingChance * cuttingChanceCoeff));
				}
			}
			
			if(cuts.size() == 0)
			{
				note1 = new TimedNote(mNote);
				cuts.add(new TimedNoteToCut(note1, 0));
			}
			
			for(int i = 0; i < cuts.size(); i++)
			{
				int randomIndex = (int)(Math.random() * cuts.size());
				
				TimedNoteToCut tmp = cuts.get(i);
				cuts.set(i, cuts.get(randomIndex));
				cuts.set(randomIndex, tmp);
			}
			
			return cuts;
		}
		
		public TimedNote note()
		{
			return mNote;
		}
		
		public float cuttingChance()
		{
			return mCuttingChance;
		}
		
		private TimedNote mNote;
		private float mCuttingChance;
	}
}
