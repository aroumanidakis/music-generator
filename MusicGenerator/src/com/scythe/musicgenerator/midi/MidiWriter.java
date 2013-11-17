package com.scythe.musicgenerator.midi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;
import com.scythe.musicgenerator.core.DiatonicScale;
import com.scythe.musicgenerator.core.DiatonicScale.Mode;
import com.scythe.musicgenerator.core.Note;
import com.scythe.musicgenerator.core.TimeSignature;
import com.scythe.musicgenerator.core.TimedElement;

public class MidiWriter
{
	public MidiWriter(String fileName)
	{
		mFileName = fileName;
		mTracks = new ArrayList<ArrayList<Bar>>();
		mTrackNames = new ArrayList<String>();
		mTempo = 120;
		mScale = null;
	}
	
	public void addTrack(ArrayList<Bar> track, String trackName)
	{
		mTracks.add(track);
		mTrackNames.add(trackName);
	}
	
	public void tempo(int tempo)
	{
		mTempo = tempo;
	}
	
	public void scale(DiatonicScale scale)
	{
		mScale = scale;
	}
	
	public void write()
	{
		if(mTracks.size() < 1)
		{
			System.out.println("There is not track to write, file " + mFileName + " won't be written...");
			return;
		}
		
		try
		{
			mOutputStream = new FileOutputStream(new File(mFileName));
			
			writeMidiHeader();
			writeMidiTracks();
		
			mOutputStream.close();
		}
		catch(IOException e)
		{
			System.out.println("File writing error: " + e.getMessage());
		}
	}
	
	private void writeMidiHeader() throws IOException
	{
		byte[] chunkID = {0x4D, 0x54, 0x68, 0x64};
		byte[] chunkSize = {0x00, 0x00, 0x00, 0x06};
		byte[] formatType = {0x00, (mTracks.size() == 1) ? (byte)0x00 : (byte)0x01};
		byte[] nbTracks = new byte[2];
		nbTracks[0] = (byte)((mTracks.size() & 0x0000FF00) >> 8);
		nbTracks[1] = (byte)(mTracks.size() & 0x000000FF);
		byte[] timeDivision = new byte[2];
		timeDivision[0] = (byte)((mTicksPerBeat & 0x0000FF00) >> 8);
		timeDivision[1] = (byte)(mTicksPerBeat & 0x000000FF);
		
		mOutputStream.write(chunkID);
		mOutputStream.write(chunkSize);
		mOutputStream.write(formatType);
		mOutputStream.write(nbTracks);
		mOutputStream.write(timeDivision);
	}
	
	private void writeMidiTracks() throws IOException
	{
		for(int trackIndex = 0; trackIndex < mTracks.size(); trackIndex++)
		{
			ArrayList<byte[]> events = new ArrayList<byte[]>();
			
			events.add(generateCopyrightEvent());
			events.add(generateTextInformationEvent());
			events.add(generateTrackNameEvent(trackIndex));
			events.add(generateTempoEvent());
			events.add(generateKeySignatureEvent());
			
			TimeSignature currentSignature = null;
			
			for(Bar bar : mTracks.get(trackIndex))
			{
				if(currentSignature == null || !currentSignature.equals(bar.signature()))
				{
					currentSignature = bar.signature();
					events.add(generateTimeSignatureEvent(currentSignature));
				}
				
				for(TimedElement element : bar)
				{
					for(Note note : element)
					{
						byte[] noteOnEvent = new byte[4];
						noteOnEvent[0] = 0;
						noteOnEvent[1] = (byte)0x90;
						noteOnEvent[2] = note.toMidiNoteNumber();
						noteOnEvent[3] = (byte)note.velocity();
						
						events.add(noteOnEvent);
					}
					
					for(int noteIndex = 0; noteIndex < element.size(); noteIndex++)
					{
						if(noteIndex == 0)
						{
							byte[] deltaTime = intToDeltaTime((int)(element.durationInTime() * mTicksPerBeat));
							
							byte[] noteOffEvent = new byte[deltaTime.length + 3];
							for(int i = 0; i < deltaTime.length; i++)
							{
								noteOffEvent[i] = deltaTime[i];
							}
							
							noteOffEvent[noteOffEvent.length - 3] = (byte)0x80;
							noteOffEvent[noteOffEvent.length - 2] = element.get(noteIndex).toMidiNoteNumber();
							noteOffEvent[noteOffEvent.length - 1] = (byte)element.get(noteIndex).velocity();
							
							events.add(noteOffEvent);
						}
						else
						{
							byte[] noteOffEvent = new byte[4];
							
							noteOffEvent[0] = 0;
							noteOffEvent[1] = (byte)0x80;
							noteOffEvent[2] = element.get(noteIndex).toMidiNoteNumber();
							noteOffEvent[3] = (byte)element.get(noteIndex).velocity();
							
							events.add(noteOffEvent);
						}
					}
				}
			}
			
			byte[] endOfTrack = {(byte)0x00, (byte)0xFF, (byte)0x2F, (byte)0x00};
			events.add(endOfTrack);
			
			byte[] chunkID = {0x4D, 0x54, 0x72, 0x6B};
			byte[] chunkSize = new byte[4];
			
			int size = 0;
			for(byte[] event : events)
			{
				size += event.length;
			}
			
			chunkSize[0] = (byte)((size & 0xFF000000) >> 24);
			chunkSize[1] = (byte)((size & 0x00FF0000) >> 16);
			chunkSize[2] = (byte)((size & 0x0000FF00) >> 8);
			chunkSize[3] = (byte)(size & 0x000000FF);
			
			mOutputStream.write(chunkID);
			mOutputStream.write(chunkSize);
			
			for(byte[] event : events)
			{
				mOutputStream.write(event);
			}
		}
	}
	
	private byte[] generateTextInformationEvent()
	{
		String msg = "Generated by music-generator (Java SE project)";
		int msgSize = msg.length();
		
		byte[] event = new byte[4 + msgSize];
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x01;
		event[3] = (byte)msgSize;
		
		for(int i = 4; i < event.length; i++)
		{
			event[i] = msg.getBytes()[i - 4];
		}
		
		return event;
	}
	
	private byte[] generateCopyrightEvent()
	{
		String copyright = "Author : Scythe, all right reserved";
		int msgSize = copyright.length();
		
		byte[] event = new byte[4 + msgSize];
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x02;
		event[3] = (byte)msgSize;
		
		for(int i = 4; i < event.length; i++)
		{
			event[i] = copyright.getBytes()[i - 4];
		}
		
		return event;
	}
	
	private byte[] generateTrackNameEvent(int trackIndex)
	{
		String trackName = mTrackNames.get(trackIndex);
		int trackNameSize = trackName.length();
		
		byte[] event = new byte[4 + trackNameSize];
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x03;
		event[3] = (byte)(trackNameSize & 0x000000FF);
		
		for(int i = 4; i <event.length; i++)
		{
			event[i] = trackName.getBytes()[i - 4];
		}
		
		return event;
	}
	
	private byte[] generateTempoEvent()
	{
		int microsecPerQuarterNote = 60000000 / mTempo;
		
		byte[] event = new byte[7];
		
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x51;
		event[3] = 0x03;
		event[4] = (byte)((microsecPerQuarterNote & 0x00FF0000) >> 16);
		event[5] = (byte)((microsecPerQuarterNote & 0x0000FF00) >> 8);
		event[6] = (byte)(microsecPerQuarterNote & 0x000000FF);
		
		return event;
	}
	
	private byte[] generateTimeSignatureEvent(TimeSignature signature)
	{
		byte[] event = new byte[8];
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x58;
		event[3] = 0x04;
		event[4] = (byte)signature.numerator();
		event[5] = (byte)signature.denominator();
		event[6] = 0x24;
		event[7] = 0x8;
		
		return event;
	}
	
	private byte[] generateKeySignatureEvent()
	{
		byte[] event = new byte[6];
		
		event[0] = 0x00;
		event[1] = (byte)0xFF;
		event[2] = 0x59;
		event[3] = 0x02;
		
		if(mScale == null)
		{
			event[4] = 0x00;
			event[5] = 0x01;
		}
		else
		{
			int numberOfAccidental = mScale.getNumberOfAccidental();
			if(mScale.accidental() == Note.Accidental.FLAT)
			{
				numberOfAccidental *= -1;
			}
			
			event[4] = (byte)numberOfAccidental;
			event[5] = (mScale.mode() == Mode.IONIAN) ? (byte)0x00 : (byte)0x01;
		}
		
		return event;
	}
	
	private byte[] intToDeltaTime(int time)
	{
		int deltaTimeSize = (time > 127) ? 2 : 1;
		
		byte[] deltaTime = new byte[deltaTimeSize];
		
		if(deltaTimeSize == 1)
		{
			deltaTime[0] = (byte)(time & 0x0000007F);
		}
		else
		{
			deltaTime[0] = (byte)(((time & 0x00007F80) >> 7) | 0x80);
			deltaTime[1] = (byte)(time & 0x0000007F);
		}
		
		return deltaTime;
	}
	
	private static final int mTicksPerBeat = 480;
	
	private ArrayList<ArrayList<Bar>> mTracks;
	private ArrayList<String> mTrackNames;
	private String mFileName;
	private int mTempo;
	private DiatonicScale mScale;
	
	private FileOutputStream mOutputStream;
}
