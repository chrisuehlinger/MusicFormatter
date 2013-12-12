package musicFormatter;
/*
   Copyright (C) 2010 Sony CSL Paris
   All rights reserved.
   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are met:

       * Redistributions of source code must retain the above copyright
         notice, this list of conditions and the following disclaimer.
       * Redistributions in binary form must reproduce the above copyright
         notice, this list of conditions and the following disclaimer in the
         documentation and/or other materials provided with the distribution.
       * Neither the name of Sony CSL Paris nor the names of its contributors 
         may be used to endorse or promote products derived from this software 
         without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
   EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import guidoengine.*;

public class guidoviewer {  
   
    public static void main(String[] args) {
        JFrame win = new guidoviewerGUI();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setVisible(true);
    }
}
   
class scorePanel extends Canvas implements Printable {

 	static {
        try {
			System.loadLibrary("jniGUIDOEngine");
			guido.Init("Guido2", "Times");
			if (guido.xml2gmn())
				System.out.println("libMusicXML v." + guido.musicxmlversion() + 
					" with GMN converter v." + guido.musicxml2guidoversion());
			else
				System.out.println("libMusicXML not available.");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
		}
	}

	public static final int kNoMap = 0;
	public static final int kMapVoice = 1;
	public static final int kMapStaff = 2;
	public static final int kMapSys = 3;

	guidoscore	m_gmnscore;
	int			m_showMap;
	
	public scorePanel()						{ m_gmnscore = new guidoscore(); m_showMap = kNoMap; }
	public guidoscore score()				{ return m_gmnscore; }
	public boolean opened()					{ return m_gmnscore.fARHandler != 0; }
	public Dimension getPreferredSize()		{ return new Dimension(500,600); }

	public void showMap (int map)			{ if (m_showMap != map) { m_showMap = map; repaint(); } }

	public void setGMN(String str, boolean gmncode) {
		if (opened()) m_gmnscore.close();
		int err = gmncode ? m_gmnscore.ParseString(str) : m_gmnscore.ParseFile(str);
		if (err != guido.guidoNoErr) {
			String msg = gmncode ? 
				  new String("Error reading string:\n") + guido.GetErrorString (err)
				: new String("Error opening ") + str + ":\n" + guido.GetErrorString (err);
			if (err == guido.guidoErrParse) {
				if (guido.xml2gmn() && !gmncode) {
					String gmn = guido.xml2gmn(str);
					setGMN (gmn, true);
					return;
				}	
				msg += " line " + guido.GetParseErrorLine();
			}
			JOptionPane.showMessageDialog(this, msg);
		}
		else {
			err = m_gmnscore.AR2GR();
			if (err != guido.guidoNoErr) {
				JOptionPane.showMessageDialog(this, "Error converting AR to GR " + str + ":\n" + guido.GetErrorString (err));
				m_gmnscore = null;
			}
			else {
				m_gmnscore.ResizePageToMusic();
				repaint();
			}
		}
	}

	public void drawMap(Graphics g, guidoscoremap map) {
		int n = map.size();
		Color a = new Color (0,0,200,100);
		Color b = new Color (200,0,0,100);
		guidosegment time = new guidosegment();
		Rectangle r = new Rectangle();
		for (int i=0; i < n; i++) {
			if (map.get(i, time, r)) {
				g.setColor ( (i%2 == 1) ? b : a);
				g.fillRect(r.x, r.y, r.width, r.height);
			}
			else {
				System.err.println("unexpected map.get failure");
				break;
			}
		}
	}

	public void drawMap(Graphics g, int what) {
		guidoscoremap map = new guidoscoremap();
		int ret = guido.guidoNoErr;
		int voices = m_gmnscore.CountVoices();
		switch (what) {
			case kMapVoice:
				for (int i=1; i<=voices; i++) {
					ret = m_gmnscore.GetVoiceMap (1, getSize().width, getSize().height, i, map);
					if (ret != guido.guidoNoErr) break;
					drawMap (g, map);
					System.out.println("Voice " + i + " of " + voices);
				}
				break;
			case kMapStaff:
				for (int i=1; i<=voices; i++) {
					ret = m_gmnscore.GetStaffMap (1, getSize().width, getSize().height, i, map);
					if (ret != guido.guidoNoErr) break;
					drawMap (g, map);
				}
				break;
			case kMapSys:
				ret = m_gmnscore.GetSystemMap (1, getSize().width, getSize().height, map);
				if (ret == guido.guidoNoErr) drawMap (g, map);
				break;
		}		
		if (ret != guido.guidoNoErr)
			System.err.println("error getting score map: " + guido.GetErrorString(ret));
	}

	public void paint(Graphics g) {
		if (m_gmnscore.fGRHandler != 0) {
			guidodrawdesc desc = new guidodrawdesc(getSize().width, getSize().height);		
			int ret = m_gmnscore.Draw (g, getSize().width, getSize().height, desc, new guidopaint());
			if (ret != guido.guidoNoErr)
				System.err.println("error drawing score: " + guido.GetErrorString(ret));
			else if (m_showMap > 0) drawMap (g, m_showMap);
		}
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page >= m_gmnscore.GetPageCount())
			return NO_SUCH_PAGE;
		if (m_gmnscore.fGRHandler != 0) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			int w = (int)pf.getImageableWidth();
			int h = (int)pf.getImageableHeight();
			guidodrawdesc desc = new guidodrawdesc(w, h);
			desc.fPage = page+1;
			int ret = m_gmnscore.Draw (g, w, h, desc, new guidopaint());
			if (ret != guido.guidoNoErr)
				System.err.println("error printing score: " + guido.GetErrorString(ret));
		}		
		return PAGE_EXISTS;
	}
}


///// guidoviewerGUI
class guidoviewerGUI extends JFrame {

    String	 m_Title = "Guido Viewer Java";
	Menu     m_fileMenu = new Menu("File");		// declare and create new menu
    MenuItem m_openItem = new MenuItem("Open");	// create new menu item
    MenuItem m_midiexportItem = new MenuItem("Export to MIDI file");	// create new menu item
    MenuItem m_svgexportItem = new MenuItem("Export to SVG");	// create new menu item
    MenuItem m_printItem = new MenuItem("Print");	// create new menu item
    MenuItem m_quitItem = new MenuItem("Quit");	// another menu item

	Menu     m_mapMenu = new Menu("Map");		// declare and create new menu
    CheckboxMenuItem m_mapVoiceItem = new CheckboxMenuItem("Voice");	// create new menu item
    CheckboxMenuItem m_mapStaffItem = new CheckboxMenuItem("Staff");	// create new menu item
    CheckboxMenuItem m_mapSysItem	= new CheckboxMenuItem("System");	// create new menu item

	scorePanel m_score = new scorePanel();

    
    public guidoviewerGUI() {
        //... Add listeners to file menu items
        m_openItem.addActionListener(new OpenAction(this));
        m_openItem.setShortcut(new MenuShortcut('O'));
        m_midiexportItem.addActionListener(new MidiExportAction(this));
        m_svgexportItem.addActionListener(new SVGExportAction(this));
        m_printItem.addActionListener(new PrintAction(m_score));
        m_quitItem.addActionListener(new QuitAction());
 		//... Add listeners to map menu items
        m_mapVoiceItem.addItemListener(new MapAction(this, scorePanel.kMapVoice));
        m_mapStaffItem.addItemListener(new MapAction(this, scorePanel.kMapStaff));
        m_mapSysItem.addItemListener(new MapAction(this, scorePanel.kMapSys));

        //... set shortcuts
        m_openItem.setShortcut(new MenuShortcut('O'));
        m_printItem.setShortcut(new MenuShortcut('P'));
		m_mapVoiceItem.setShortcut(new MenuShortcut('R'));
        m_mapStaffItem.setShortcut(new MenuShortcut('T'));
        m_mapSysItem.setShortcut(new MenuShortcut('Y'));
	  
        //... disable some items
		m_midiexportItem.setEnabled (false);
        m_svgexportItem.setEnabled (false);
        m_printItem.setEnabled (false);
        m_mapMenu.setEnabled (false);
 
        //... Menubar, menus, menu items.  Use indentation to show structure.
        MenuBar menubar = new MenuBar();  // declare and create new menu bar
		menubar.add(m_fileMenu);			// add the menu to the menubar
		m_fileMenu.add(m_openItem);			// add the menu item to the menu
		m_fileMenu.add(m_midiexportItem);
		m_fileMenu.add(m_svgexportItem);
		m_fileMenu.add(m_printItem);
		m_fileMenu.addSeparator();			// add separator line to menu
		m_fileMenu.add(m_quitItem);

		menubar.add(m_mapMenu);			// add the menu to the menubar
  		m_mapMenu.add(m_mapVoiceItem);
  		m_mapMenu.add(m_mapStaffItem);
  		m_mapMenu.add(m_mapSysItem);
    
        //... Content pane: create, layout, add components
        JPanel content = new JPanel();
        content.setBackground(Color.white);
        content.setLayout(new BorderLayout());
        content.add(m_score, BorderLayout.CENTER);

        //... Set JFrame's menubar, content pane, and title.
        this.setContentPane(content);       // Set windows content pane..
        this.setMenuBar(menubar);          // Set windows menubar.
        this.setTitle(m_Title);
        this.pack();
    }
    
    protected void mapChange(int map, int change) {
		if (change == ItemEvent.DESELECTED) m_score.showMap (scorePanel.kNoMap);
		else {
			m_score.showMap (map);
			switch (map) {
				case scorePanel.kMapVoice:
					m_mapStaffItem.setState ( false );
					m_mapSysItem.setState	( false );
					break;
				case scorePanel.kMapStaff:
					m_mapVoiceItem.setState ( false );
					m_mapSysItem.setState	( false );
					break;
				case scorePanel.kMapSys:
					m_mapStaffItem.setState ( false );
					m_mapVoiceItem.setState ( false );
					break;
			}
		}
	}
    
    protected boolean getMapState(int map) {
		switch (map) {
			case scorePanel.kMapVoice:
				return m_mapVoiceItem.getState();
			case scorePanel.kMapStaff:
				return m_mapStaffItem.getState();
			case scorePanel.kMapSys:
				return m_mapSysItem.getState();
		}
		return false;
	}
   
    public void setGMNFile(File file) {
        this.setTitle(m_Title + " - " + file.getName());
 		m_score.setGMN(file.getPath(), false); 
		m_midiexportItem.setEnabled (true);
        m_svgexportItem.setEnabled (true);
        m_printItem.setEnabled (true);
		m_mapMenu.setEnabled (true);
	}

	public guidoscore score()				{ return m_score.score(); }

	//// MapAction
	class MapAction implements ItemListener {
		guidoviewerGUI m_viewer;
		int m_map; 
        public MapAction(guidoviewerGUI viewer, int map)	{ m_viewer = viewer; m_map = map; }
		public void itemStateChanged(ItemEvent e) {
			m_viewer.mapChange (m_map, e.getStateChange());
    	}
    }

	//// OpenAction
	class OpenAction implements ActionListener {
		guidoviewerGUI m_viewer;
        public OpenAction(guidoviewerGUI viewer)	{ m_viewer = viewer; }
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
    		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				m_viewer.setGMNFile(chooser.getSelectedFile());
    	}
    }

	//// MIDI export
	class MidiExportAction implements ActionListener {
		guidoviewerGUI m_viewer;
        public MidiExportAction (guidoviewerGUI viewer)	{ m_viewer = viewer; }
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
    		if(m_viewer.m_score.opened() && (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)) {
				guido2midiparams p = new guido2midiparams();
				int err = m_viewer.score().AR2MIDIFile (chooser.getSelectedFile().getPath(), p);
				if (err != 0) {
					System.err.println("midi export failed: " + guido.GetErrorString(err));
				}
			}
    	}
    }

	//// SVG export
	class SVGExportAction implements ActionListener  {
		guidoviewerGUI m_viewer;
        public SVGExportAction (guidoviewerGUI viewer)	{ m_viewer = viewer; }
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
    		if(m_viewer.m_score.opened() && (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)) {
				try {
					FileWriter w = new FileWriter (chooser.getSelectedFile());
					String svg = m_viewer.score().SVGExport (1, "");
					w.write ( svg);
					w.flush ();
				}
				catch (IOException x) {
					System.err.println("writing to file " + chooser.getSelectedFile().getPath() + " failed");
				}
			}
    	}
    }

	//// PrintAction
	class PrintAction implements ActionListener {
         scorePanel m_score;
         public PrintAction(scorePanel score)	{ m_score = score; }
		 public void actionPerformed(ActionEvent e) {
			 PrinterJob job = PrinterJob.getPrinterJob();
			 job.setPrintable(m_score);
			 boolean ok = job.printDialog();
			 if (ok) {
				 try {
					  job.print();
				 } catch (PrinterException ex) {
					System.err.println("printing exception: " + ex);
				 }
			 }
		}
    }
    
	/// QuitAction
    class QuitAction implements ActionListener {
        public void actionPerformed(ActionEvent e) { System.exit(0); }
    }
}
