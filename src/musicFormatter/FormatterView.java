package musicFormatter;

import guidoengine.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import musicFormatter.FormatterModel.musicElement;

public class FormatterView extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2836526729813682976L;
	
	static {
        try {
			System.loadLibrary("jniGUIDOEngine");
			guido.Init("Guido2", "Times");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
		}
	}
	
	protected FormatterModel model;
	Rectangle rect;
	BufferedImage scoreImage;
	guidoscore fScore;
	public musicElement drawables[] = new musicElement[10];
	public int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
	private Dimension preferredSize;
	public Dimension getPreferredSize() { return preferredSize; }
	public void setPreferredSize(Dimension ps) { preferredSize = ps; }
	
	public FormatterView(FormatterModel m){
		model = m;
		model.addObserver(this);
		
		rect = new Rectangle();
		setPreferredSize(new Dimension(1000,2000));
        setSize(getPreferredSize());
	}
	
	public	void background_paint(Graphics g) {
		int border=10;
		int w = (getSize().width-(2*border));
		int h = (getSize().height-(2*border));
		int x = border, y = border;
		g.setColor(Color.GRAY);
		g.fillRect (0, 0, getSize().width, getSize().height);
		g.setColor(Color.WHITE);
		g.fillRect (x, y, w, h);
	}
	
    public void paintComponent(Graphics g) {
    	g.drawImage(scoreImage, 0, 0, null);
    	g.setColor(new Color (255,0,0,80));
		if(!rect.isEmpty())
			g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
    }

    public void drawScore(Graphics g) {
		int pageCount = fScore.GetPageCount();
		int buffer = 0;
		int index=0;
		for(int i=1; i<=pageCount; i++) {
			guidopageformat pf = new guidopageformat();
			fScore.GetPageFormat(i, pf);
			int w = (int)(guido.Unit2Inches(pf.fWidth)*(float)resolution);
			int h = (int)(guido.Unit2Inches(pf.fHeight)*(float)resolution);
			Graphics pageG = g.create(0, buffer, w+20, h+20);
			background_paint (pageG);
			guidodrawdesc desc = new guidodrawdesc(w, h);
			desc.fPage = i;
			Graphics guidoG = pageG.create(10, 10, w, h);
			int ret = fScore.Draw (guidoG, w, h, desc, new guidopaint());
			if (ret != guido.guidoNoErr)
				System.err.println("error drawing score: " + guido.GetErrorString(ret));
			
			
			for(musicElement e: drawables){
				if(e != null && e.page == i){
					guidoG.setColor(new Color((float)1,(float)0,(float)0,(float)1));
					guidoG.drawString(e.index + " " + (int)e.getRect().getX() + "," + (int)e.getRect().getY(), (int)e.getRect().getX(), (int)e.getRect().getY());
					guidoG.setColor(new Color((float)1,(float)0,(float)0,(float)0.5));
					guidoG.fillRect((int)e.getRect().getX(), (int)e.getRect().getY(), (int)e.getRect().getWidth(), (int)e.getRect().getHeight());
					index++;
				}
			}
			
			buffer += h+20;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof guidoscore){
			fScore = (guidoscore)arg1;
		
			guidopageformat format = new guidopageformat();
			fScore.GetPageFormat(1, format);
			int numPages = fScore.GetPageCount();
			//System.out.println(numPages);
			int scoreWidth = (int)(guido.Unit2Inches(format.fWidth)*(float)resolution);
			int scoreHeight = (int)(guido.Unit2Inches(format.fHeight)*(float)resolution)*numPages;
			setPreferredSize(new Dimension(scoreWidth,scoreHeight));
			setSize(preferredSize);
			scoreImage = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
			drawScore(scoreImage.getGraphics());
		}else if(arg1 instanceof musicElement[]){
			drawables=(musicElement[])arg1;
			drawScore(scoreImage.getGraphics());
		}else if(arg1 instanceof Rectangle)
			rect = (Rectangle)arg1;

		repaint();
	}

}
