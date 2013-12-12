package proxymusic.guido;

import java.util.Arrays;

import proxymusic.*;
import proxymusic.ScorePartwise.Part;
import proxymusic.ScorePartwise.Part.Measure;

public class PartSummary {

	Part part;
	int staffCount;
	int voiceCount;
	int staves[];
	int voices[];
	int staffVoices[][];
	
	public PartSummary(Part p){
		part = p;
		staffCount=0;
		voiceCount=0;
		staves = new int[1];
		voices = new int[1];
		staffVoices = new int[1][1];
		
		for(Measure m: part.getMeasure())
			for(Object item: m.getNoteOrBackupOrForward())
				if(item instanceof Note){
					Note n = (Note)item;
					int v = Integer.parseInt(n.getVoice());
					int s = n.getStaff().intValue();
					
					if(v > voiceCount || s > staffCount)
					{
						
						if(v > voiceCount){
							voiceCount=v;
							voices = Arrays.copyOf(voices, voiceCount+1);
						}
					
						if(s > staffCount){
							staffCount=s;
							staves = Arrays.copyOf(staves, staffCount+1);
						}
						
						staffVoices = Arrays.copyOf(staffVoices, staffCount+1);
						for(int i=0; i<=staffCount; i++ )
							if(staffVoices[i] != null)
								staffVoices[i] = Arrays.copyOf(staffVoices[i], voiceCount+1);
							else
								staffVoices[i]= new int[voiceCount+1];
					}
					
					staves[s]++;
					voices[v]++;
					//System.out.println(s+" "+v +" "+staffVoices.length+" "+staffVoices[s].length);
					staffVoices[s][v]++;
					
				}
		}
	
	public int countStaves (){
		return staffCount;
	}
	
	public int countVoices (){
		return voiceCount;
	}
	
	public int countVoices (int staff){
		if(staff > staffCount)
			return 0;
		
		int v=0;
		for(int i=1; i<=voiceCount; i++)
			if(staffVoices[staff][i]>0)
				v++;
		
		return v;
	}
	
	public int getStaffNotes (int id){
		if(id > staffCount)
			return 0;
		
		return staves[id];
	}
	
	public int getVoiceNotes (int voiceid){
		if(voiceid > voiceCount)
			return 0;
		
		return voices[voiceid];
	}
	
	public int getVoiceNotes (int staffid, int voiceid){
		return staffVoices[staffid][voiceid];
	}
	
	public int getMainStaff (int voiceid){
		int maxnotes=0;
		int mainStaff=0;
		for(int i=1; i<=staffCount; i++)
			if(staffVoices[i][voiceid] > maxnotes){
				maxnotes = staffVoices[i][voiceid];
				mainStaff=i;
			}
		
		return mainStaff;
	}
	
	public int[] getStaves(){
		int staffIDs[] = new int[staffCount];
		
		for(int i=0; i<staffCount; i++)
			staffIDs[i]=i+1;
		
		return staffIDs;
	}
	
	public int[] getVoices (){
		int voiceIDs[] = new int[voiceCount];
		
		for(int i=0; i<voiceCount; i++)
			voiceIDs[i]=i+1;
		
		return voiceIDs;
	}
	
	public int[] getStaves (int voice){
		int numStaves=0;
		int[] staffIDs = new int[1];
		for(int i=1; i<=staffCount; i++)
			if(staffVoices[i][voice]>0){
				staffIDs=Arrays.copyOf(staffIDs, ++numStaves);
				staffIDs[numStaves-1]=i;
			}
		
		return staffIDs;
	}
	
	public int[] getVoices (int staff){
		int numVoices=0;
		int[] voiceIDs = new int[1];
		for(int i=1; i<=voiceCount; i++)
			if(staffVoices[staff][i]>0){
				voiceIDs=Arrays.copyOf(voiceIDs, ++numVoices);
				voiceIDs[numVoices-1]=i;
			}
		
		return voiceIDs;
	}
}
