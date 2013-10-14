package com.scythe.musicgenerator.midi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.scythe.musicgenerator.core.Bar;

public class MidiWriter
{
	public MidiWriter(String fileName)
	{
		mFileName = fileName;
		mTracks = new ArrayList<ArrayList<Bar>>();
	}
	
	public void addTrack(ArrayList<Bar> track)
	{
		mTracks.add(track);
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
		byte[] timeDivision = {0x00, 0x00};
		
		mOutputStream.write(chunkID);
		mOutputStream.write(chunkSize);
		mOutputStream.write(formatType);
		mOutputStream.write(nbTracks);
		mOutputStream.write(timeDivision);
	}
	
	private ArrayList<ArrayList<Bar>> mTracks;
	private String mFileName;
	
	private FileOutputStream mOutputStream;
}
