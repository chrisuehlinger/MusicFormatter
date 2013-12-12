package musicFormatter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.Timer;

public class FormatterControl extends KeyAdapter implements MouseListener, ActionListener {
	
	protected FormatterModel model;
	
	public final static String FILE_NAME = System.getProperty("user.dir") + "/musicXMLsamples/BrahWiMeSample.xml";
	FormatterControl(FormatterModel m){
		model = m;
		Timer t = new Timer(1000 / 20,
			    new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
				    timerTick();
				}
		});
	t.start();
	//model.openScore(FILE_NAME);
	}

	/**
     * Handles timer events.
     */
	
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		System.out.println(action);
		if (action.compareTo("Open") == 0)
		{
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				model.openScore(chooser.getSelectedFile().toString());
		}else if (action.compareTo("Save") == 0)
				model.saveScore();
		else if (action.compareTo("Save As") == 0)
		{
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				model.saveAsScore(chooser.getSelectedFile().toString());
		}else if (action.compareTo("Print") == 0)
			model.printScore();
		else if (action.compareTo("Invert stem") == 0)
			model.setState(FormatterModel.INVERT_STEM);
		else if (action.compareTo("Decrease width of stem") == 0)
			model.setState(FormatterModel.DECR_WID);
		else if (action.compareTo("Increase width of stem") == 0)
			model.setState(FormatterModel.INCR_WID);
		else if (action.compareTo("Staccato") == 0)
			model.setState(FormatterModel.STACCATO);
		else if (action.compareTo("Accent") == 0)
			model.setState(FormatterModel.ACCENT);
		else if (action.compareTo("Tenuto") == 0)
			model.setState(FormatterModel.TENUTO);
		else if (action.compareTo("Fermata") == 0)
			model.setState(FormatterModel.FERMATA);
		else if (action.compareTo("Natural Harmonic") == 0)
			model.setState(FormatterModel.HARMONIC);
		else if(action.compareTo("Export to MIDI") == 0){
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			model.exportMIDI(chooser.getSelectedFile().getPath());
		}else
			model.setState(FormatterModel.NO_STATE);
	}
	
    public synchronized void timerTick()
    {
	//model.update();
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		model.update(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
