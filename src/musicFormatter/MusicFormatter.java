package musicFormatter;

import guidoengine.*;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MusicFormatter extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	FormatterModel model;
	FormatterView view;
	FormatterControl control;
	
	JScrollPane pane;
	
	public MusicFormatter() {
		super("Music Formatter");
		setSize(700,700);
		
		model = new FormatterModel();
		view = new FormatterView(model);
		control = new FormatterControl(model);
		view.addKeyListener(control);
		view.addMouseListener(control);
		model.addObserver(this);
		
		JMenuBar menuBar;
		menuBar = createMenuBar(control);
		this.setJMenuBar(menuBar);
		
		pane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    setContentPane(pane);
	    pane.setDoubleBuffered(true);
	    pane.setViewportView(view);
	    
	    setSize(view.getWidth()+pane.getVerticalScrollBar().getWidth(), getHeight());
	    setVisible(true);
	    setResizable(false);
	}

	
	
	
	JMenuBar createMenuBar(FormatterControl ctrl)
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
			JMenuItem openButton = new JMenuItem("Open");
			JMenuItem saveButton = new JMenuItem("Save");
			JMenuItem saveAsButton = new JMenuItem("Save As");
			JMenuItem printButton = new JMenuItem("Print");
			JMenuItem exitButton = new JMenuItem("Exit");
				openButton.addActionListener(ctrl);
				saveButton.addActionListener(ctrl);
				saveAsButton.addActionListener(ctrl);
				printButton.addActionListener(ctrl);
				exitButton.addActionListener(ctrl);
				fileMenu.add(openButton);
				fileMenu.add(saveButton);
				fileMenu.add(saveAsButton);
				fileMenu.add(printButton);
				fileMenu.add(exitButton);
		JMenu midiMenu = new JMenu("Playback");
			JMenuItem exportMIDI = new JMenuItem("Export to MIDI");
			JMenuItem playButton = new JMenuItem("Play/Pause");
			JMenuItem forwardButton = new JMenuItem("Fast Forward");
			JMenuItem rewindButton = new JMenuItem("Rewind");
				exportMIDI.addActionListener(ctrl);
				playButton.addActionListener(ctrl);
				forwardButton.addActionListener(ctrl);
				rewindButton.addActionListener(ctrl);
				midiMenu.add(exportMIDI);
				midiMenu.add(playButton);
				midiMenu.add(forwardButton);
				midiMenu.add(rewindButton);
		JMenu noteFormatMenu = new JMenu("Notes");
			JMenuItem incrWidButton = new JMenuItem("Increase width of stem");
			JMenuItem decrWidButton = new JMenuItem("Decrease width of stem");
			JMenuItem invStemButton = new JMenuItem("Invert stem");
			JMenuItem addBeamButton = new JMenuItem("Add beam to note");
			JMenuItem destrBeamButton = new JMenuItem("Destroy beam");
			incrWidButton.addActionListener(ctrl);
			decrWidButton.addActionListener(ctrl);
			invStemButton.addActionListener(ctrl);
			addBeamButton.addActionListener(ctrl);
			destrBeamButton.addActionListener(ctrl);
			noteFormatMenu.add(incrWidButton);
			noteFormatMenu.add(decrWidButton);
			noteFormatMenu.add(invStemButton);
			noteFormatMenu.add(addBeamButton);
			noteFormatMenu.add(destrBeamButton);
		JMenu articulMenu = new JMenu("Articulation");
			JMenuItem stacButton = new JMenuItem("Staccato");
			JMenuItem accentButton = new JMenuItem("Accent");
			JMenuItem tenutoButton = new JMenuItem("Tenuto");
			JMenuItem fermataButton = new JMenuItem("Fermata");
			JMenuItem harmonicButton = new JMenuItem("Natural Harmonic");
				stacButton.addActionListener(ctrl);
				accentButton.addActionListener(ctrl);
				tenutoButton.addActionListener(ctrl);
				fermataButton.addActionListener(ctrl);
				harmonicButton.addActionListener(ctrl);
				articulMenu.add(stacButton);
				articulMenu.add(accentButton);
				articulMenu.add(tenutoButton);
				articulMenu.add(fermataButton);
				articulMenu.add(harmonicButton);
		JMenu timeMenu = new JMenu("Time");
			JMenuItem metronButton = new JMenuItem("Add Metronome Mark");
				metronButton.addActionListener(ctrl);
				timeMenu.add(metronButton);
		JMenu lyricsMenu = new JMenu("Lyrics");
			JMenuItem editLyrButton = new JMenuItem("Edit lyrics");
			JMenuItem addSpaceLyrButton = new JMenuItem("Add space");
			JMenuItem reducSpaceLyrButton = new JMenuItem("Reduce space");
			JMenuItem lockButton = new JMenuItem("Lock to note");
			JMenuItem unlockButton = new JMenuItem("Unlock from note");
			JMenuItem fontButton = new JMenuItem("Change font");
				editLyrButton.addActionListener(ctrl);
				addSpaceLyrButton.addActionListener(ctrl);
				reducSpaceLyrButton.addActionListener(ctrl);
				lockButton.addActionListener(ctrl);
				unlockButton.addActionListener(ctrl);
				fontButton.addActionListener(ctrl);
				lyricsMenu.add(editLyrButton);
				lyricsMenu.add(addSpaceLyrButton);
				lyricsMenu.add(reducSpaceLyrButton);
				lyricsMenu.add(lockButton);
				lyricsMenu.add(unlockButton);
				lyricsMenu.add(fontButton);
		JMenu spacingMenu = new JMenu("Spacing");
			JMenuItem addSpaceButton = new JMenuItem("Add space");
			JMenuItem reducSpaceButton = new JMenuItem("Reduce space");
				addSpaceButton.addActionListener(ctrl);
				reducSpaceButton.addActionListener(ctrl);
				spacingMenu.add(addSpaceButton);
				spacingMenu.add(reducSpaceButton);
		JMenu pageMenu = new JMenu("Format");
			JMenuItem shrButton = new JMenuItem("Shrink margins");
			JMenuItem expButton = new JMenuItem("Expand margins");
			JMenuItem incrBarButton = new JMenuItem("Increase number of bars per line");
			JMenuItem decrBarButton = new JMenuItem("Decrease number of bars per line");
			JMenuItem incrLineButton = new JMenuItem("Increase number of lines per page");
			JMenuItem decrLineButton = new JMenuItem("Decrease number of lines per page");
				shrButton.addActionListener(ctrl);
				expButton.addActionListener(ctrl);
				incrBarButton.addActionListener(ctrl);
				decrBarButton.addActionListener(ctrl);
				incrLineButton.addActionListener(ctrl);
				decrLineButton.addActionListener(ctrl);
				pageMenu.add(shrButton);
				pageMenu.add(expButton);
				pageMenu.add(incrBarButton);
				pageMenu.add(decrBarButton);
				pageMenu.add(incrLineButton);
				pageMenu.add(decrLineButton);
		JMenu commentsMenu = new JMenu("Comments");
			JMenuItem partNameButton = new JMenuItem("Change part name");
			JMenuItem createTextButton = new JMenuItem("Insert text comment");
			JMenuItem editTextButton = new JMenuItem("Edit text comment");
			JMenuItem deleteTextButton = new JMenuItem("Delete text comment");
				partNameButton.addActionListener(ctrl);
				createTextButton.addActionListener(ctrl);
				editTextButton.addActionListener(ctrl);
				deleteTextButton.addActionListener(ctrl);
				commentsMenu.add(partNameButton);
				commentsMenu.add(createTextButton);
				commentsMenu.add(editTextButton);
				commentsMenu.add(deleteTextButton);
		menuBar.add(fileMenu);
		menuBar.add(midiMenu);
		menuBar.add(midiMenu);
		menuBar.add(noteFormatMenu);
		menuBar.add(articulMenu);
		menuBar.add(timeMenu);
		menuBar.add(lyricsMenu);
		menuBar.add(spacingMenu);
		menuBar.add(pageMenu);
		menuBar.add(commentsMenu);
		
		return menuBar;
	}
	
	public static void main(String[] args)
	{
		JFrame win = new MusicFormatter();
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}




	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof Double){
			int scoreWidth = (int)(guido.Unit2Inches(((Double)arg1).floatValue())*(float)view.resolution);
			
			System.out.println("Size in inches: " + guido.Unit2Inches(((Double)arg1).floatValue()));
			setSize(scoreWidth+2*10, getSize().height);
		}
	}

}
