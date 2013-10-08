package com.scythe.musicgenerator.factories;

import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.defines.Duration;

public class BarFactory
{
	public static Bar generateNonMelodic(int[] rhythmSignature)
	{
		ArrayList<NoteToCut> notesToCut = createBeginningNotes(rhythmSignature[0] * Duration.convertInTime(rhythmSignature[1]));
		
		boolean newCuts = true;
		while(newCuts)
		{
			newCuts = false;
			for(int i = 0; i < notesToCut.size(); i++)
			{
				if(notesToCut.get(i).cuttingChance() != 0)
				{
					ArrayList<NoteToCut> cuts = notesToCut.get(i).randomCutting();
					
					notesToCut.remove(i);
					notesToCut.addAll(i, cuts);
					
					newCuts = true;
					break;
				}
			}
		}
		
		Note[] notes = new Note[notesToCut.size()];
		for(int i = 0; i < notes.length; i++)
		{
			notes[i] = notesToCut.get(i).note();
		}
		
		return new Bar(rhythmSignature, notes);
	}
	
	private static ArrayList<NoteToCut> createBeginningNotes(float totalTime)
	{
		ArrayList<NoteToCut> notes = new ArrayList<NoteToCut>();
		
		float timeToAdd = totalTime;
		while(timeToAdd > 0)
		{
			for(int duration = 0; duration < Duration.NB_DURATIONS; duration++)
			{
				if((Duration.convertInTime(duration) * 1.5) <= timeToAdd)
				{
					notes.add(new NoteToCut(new Note(Note.Name.A, Note.Accidental.NONE, duration, true), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration) * 1.5;
					break;
				}
				
				if(Duration.convertInTime(duration) <= timeToAdd)
				{
					notes.add(new NoteToCut(new Note(Note.Name.A, Note.Accidental.NONE, duration, false), cuttingChanceCoeff));
					timeToAdd -= Duration.convertInTime(duration);
					break;
				}
			}
		}
		
		for(int i = 0; i < notes.size(); i++)
		{
			int randomIndex = (int)(Math.random() * notes.size());
			
			NoteToCut tmp = notes.get(i);
			notes.set(i, notes.get(randomIndex));
			notes.set(randomIndex, tmp);
		}
		
		return notes;
	}
	
	private static float cuttingChanceCoeff = 0.85f;
	
	private static class NoteToCut
	{
		public NoteToCut(Note note, float cuttingChance)
		{
			mNote = note;
			mCuttingChance = cuttingChance;
		}
		
		public ArrayList<NoteToCut> randomCutting()
		{
			ArrayList<NoteToCut> cuts = new ArrayList<NoteToCut>();
			Note note1, note2;
			
			if(mNote.duration() != Duration.QUARTER)
			{
				if(Math.random() < mCuttingChance)
				{
					if(mNote.dotted())
					{
						note1 = new Note(mNote.name(), mNote.accidental(), mNote.duration(), false);
						note2 = new Note(mNote.name(), mNote.accidental(), mNote.duration() + 1, false);
					}
					else
					{
						if(mNote.duration() == Duration.HALF)
						{
							note1 = new Note(mNote.name(), mNote.accidental(), mNote.duration() + 1, false);
							note2 = note1;
						}
						else
						{
							if(Math.random() < 0.4)
							{	
								note1 = new Note(mNote.name(), mNote.accidental(), mNote.duration() + 1, false);
								note2 = note1;
							}
							else
							{
								note1 = new Note(mNote.name(), mNote.accidental(), mNote.duration() + 1, true);
								note2 = new Note(mNote.name(), mNote.accidental(), mNote.duration() + 2, false);
							}
						}
					}
					
					cuts.add(new NoteToCut(note1, mCuttingChance * cuttingChanceCoeff));
					cuts.add(new NoteToCut(note2, mCuttingChance * cuttingChanceCoeff));
				}
			}
			
			if(cuts.size() == 0)
			{
				note1 = new Note(mNote);
				cuts.add(new NoteToCut(note1, 0));
			}
			
			for(int i = 0; i < cuts.size(); i++)
			{
				int randomIndex = (int)(Math.random() * cuts.size());
				
				NoteToCut tmp = cuts.get(i);
				cuts.set(i, cuts.get(randomIndex));
				cuts.set(randomIndex, tmp);
			}
			
			return cuts;
		}
		
		public Note note()
		{
			return mNote;
		}
		
		public float cuttingChance()
		{
			return mCuttingChance;
		}
		
		private Note mNote;
		private float mCuttingChance;
	}
}
