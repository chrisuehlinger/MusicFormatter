package musicFormatter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.zip.*;


import proxymusic.Note;
import proxymusic.ScorePartwise;
import proxymusic.Stem;
import proxymusic.StemValue;
import proxymusic.util.Marshalling;

import guidoengine.*;

public class FormatterModel extends Observable implements mapcollector, Printable {
	
	private int currentState;
	public final static int NO_FILE_OPEN = -1;
	public final static int NO_STATE = 0;
	public final static int INVERT_STEM = 1;
	public final static int INCR_WID = 2;
	public final static int DECR_WID = 3;
	public final static int STACCATO = 4;
	public final static int ACCENT = 5;
	public final static int FERMATA = 6;
	public final static int TENUTO = 7;
	public final static int HARMONIC = 8;
	public final static int MARCATO = 9;
	
	ScorePartwise scr=null;
	guidoscore fScore;
	public static ArrayList<musicElement> elements = new ArrayList<musicElement>();
	public musicElement drawables[] = new musicElement[10];
	public int selector;
	public String fileName;
	
	public FormatterModel(){
		currentState = NO_FILE_OPEN;			
	}
	
	public void openScore(java.lang.String path){
		fileName=path;
		currentState = NO_STATE;
		File xmlFile = new File(fileName);
		String extension = fileName.substring(fileName.length()-4);
		
		InputStream is = null;
		try {
			if(extension.compareTo(".mxl") == 0)
				is = new InflaterInputStream(new FileInputStream(xmlFile), new Inflater());
			else if(extension.compareTo(".xml") == 0)
				is = new FileInputStream(xmlFile);
			else{
				System.err.println("Not a valid type.");
				return;
			}
			
			scr = Marshalling.unmarshal(is);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

        System.out.println("Score imported from " + xmlFile);
        System.out.println("Score title: " + scr.getMovementTitle());
        
        refreshGuidoScore();
	}
	
	public void saveScore(){
		saveAsScore(fileName);
	}
	
	public void saveAsScore(String fileName){
		File xmlFile = new File(fileName);
		String extension = fileName.substring(fileName.length()-4);
		
		OutputStream os = null;
		try {
			if(extension.compareTo(".mxl") == 0)
				os = new DeflaterOutputStream(new FileOutputStream(xmlFile), new Deflater());
			else if(extension.compareTo(".xml") == 0)
				os = new FileOutputStream(xmlFile);
			else{
				System.err.println("Not a valid type.");
				return;
			}
			
			Marshalling.marshal(scr, os);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

        System.out.println("Score saved as " + xmlFile);
	}
	
	public void exportMIDI(String exportPath) {
		guido2midiparams p = new guido2midiparams();
		int err = fScore.AR2MIDIFile (exportPath, p);
		if (err != 0) {
			System.err.println("midi export failed: " + guido.GetErrorString(err));
		}
	}
	
	public void printScore(){
		PrinterJob job = PrinterJob.getPrinterJob();
		 job.setPrintable(this);
		 boolean ok = job.printDialog();
		 if (ok) {
			 try {
				  job.print();
			 } catch (PrinterException ex) {
				System.err.println("Printing Exception: " + ex);
				ex.printStackTrace();
			 }
		 }
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page >= fScore.GetPageCount())
			return NO_SUCH_PAGE;
		if (fScore.fGRHandler != 0) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			int w = (int)pf.getImageableWidth();
			int h = (int)pf.getImageableHeight();
			guidodrawdesc desc = new guidodrawdesc(w, h);
			desc.fPage = page+1;
			int ret = fScore.Draw (g, w, h, desc, new guidopaint());
			if (ret != guido.guidoNoErr)
				System.err.println("error printing score: " + guido.GetErrorString(ret));
		}		
		return PAGE_EXISTS;
	}
	
	public int getState()
	{
		return currentState;
	}
	
	public void setState(int newState)
	{
		currentState = newState;
		System.out.println("State is now: " + currentState);
	}
	
	public class musicElement
    {
    	Rectangle rect;
    	guidosegment time;
    	guidoelementinfo infos;
    	public int sel;
    	public int page;
    	public int index;
    	
    	public musicElement(guidorect b, guidosegment t, guidoelementinfo i, int p){
    		rect= new Rectangle(b.left, b.top, b.width(), b.height());
    		time=t;
    		infos=i;
    		sel=selector;
    		page = p;
    	}
    	
    	public Rectangle getRect()
    	{
    		return rect;
    	}
    }
	
	int voiceConstant=0;
	int pageConstant=0;
	int index=0;
	public void update(MouseEvent e){
		if(currentState != NO_FILE_OPEN){
		Dimension box = ((FormatterView)e.getSource()).getSize();
		int voices = fScore.CountVoices();
		int pages = fScore.GetPageCount();
		
		
		int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
		int pagenum=0;
		int totalHeight=10;
		int y=0;
		for(pagenum=1; pagenum<=pages; pagenum++) {
			guidopageformat pf = new guidopageformat();
			fScore.GetPageFormat(pagenum, pf);
			int h = (int)(guido.Unit2Inches(pf.fHeight)*(float)resolution);
			if(e.getY()-totalHeight < h) {
				y=e.getY()-totalHeight;
				System.out.println(pagenum);
				break;
			}else
				System.out.println(e.getY() + " - " + totalHeight + " != " + h);
			totalHeight+=h+20;
		}
		refreshElements(voices,pages,box);
		
		for(musicElement el: elements){
			if(el.page == pagenum && el.getRect().contains(e.getX()-10, y)){
				Note n = scr.findNote(el.index, voices);
				changeNote(n);
				System.out.println("Index of clicked note: " + el.index);
			}
		}
		}
		//refreshElements(voices,pages,box);
    	//setChanged();
    	//notifyObservers(elements.toArray(new musicElement[1]));
	}
	
	private void changeNote(Note n){
		if(currentState != NO_STATE) {
			try {
				switch(currentState){
					case INVERT_STEM: n.flipStem(); break;
					case FERMATA: n.addOrRemoveFermata(); break;
					case STACCATO: n.addOrRemoveArticulation("staccato"); break;
					case ACCENT: n.addOrRemoveArticulation("accent"); break;
					case TENUTO: n.addOrRemoveArticulation("tenuto"); break;
					case HARMONIC: n.addOrRemoveTechnical("harmonic"); break;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
				
			refreshGuidoScore();
		}
	}
	
	private void refreshElements(int voices, int pages, Dimension box) {
		index=1;
		elements.removeAll(elements);
		for(int i=1; i<=voices; i++){
			voiceConstant=i;
			for(int j=1; j<=pages; j++){
				pageConstant=j;
				fScore.GetMap(j, (float)box.getWidth(), (float)box.getHeight(), 5, this);
			}
		}
	}
	
	private void refreshGuidoScore() {
		fScore = new guidoscore();
		fScore.ParseString(scr.convertToGuido());
		int err = fScore.AR2GR();
		if (err != guido.guidoNoErr){
			System.out.println("gScore.AR2GR error  : " + guido.GetErrorString (err));
			System.exit(0);
		}
		fScore.ResizePageToMusic();
		
		setChanged();
		guidopageformat format = new guidopageformat();
		fScore.GetPageFormat(1, format);
		notifyObservers(new Double(format.fWidth));
		setChanged();
        notifyObservers(fScore);
	}
    
    public void Graph2TimeMap( guidorect box, guidosegment time, guidoelementinfo infos )
    {
    	if((infos.type==guidoelementinfo.kNote || infos.type==guidoelementinfo.kRest) && infos.voiceNum==voiceConstant){
    		musicElement e = new musicElement(box,time,infos,pageConstant);
    		e.index=index;
    		elements.add(e);
    		index++;
    	}
    }

}
