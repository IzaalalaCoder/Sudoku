package sudoku.util;

import org.jdom2.Element;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.text.DecimalFormat;
import javax.swing.*;

public class Chronometer extends JMenu {

	private static final long serialVersionUID = 1L;
	private final JMenuItem playItem;
	private int heures = 0;
	private int minutes = 0;
	private int secondes = 0;
	private long startTime;
	private final Timer timer;

	public Chronometer() {

		setFont(new Font("Consolas", Font.PLAIN, 13));
		playItem = new JMenuItem("Démarrer");

		timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				long diff = System.currentTimeMillis() - startTime;

				secondes = (int) ((diff / 1000) % 60);
				minutes = (int) ((diff / 1000) / 60 % 60);
				heures = (int) ((diff / 1000) / 60 / 60);

				setText(String.format("%02d:%02d.%02d", heures, minutes, secondes));

			}
		});
		playItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (playItem.getText()) {
					case "Arrêter":
						timer.stop();
						playItem.setText("Continuer");
						break;
					case "Continuer", "Démarrer":
						calibrage();
						timer.start();
						playItem.setText("Arrêter");
						break;
					default:
						break;
				}
			}
		});

		add(playItem);

		setText(String.format("%02d:%02d.%02d", heures, minutes, secondes));
	}

	public void resetTime() {
		timer.stop();
		setEnabled(false);
		heures = 0;
		minutes = 0;
		secondes = 0;
		setText(String.format("%02d:%02d.%02d", heures, minutes, secondes));
		playItem.setText("Démarrer");
		setEnabled(true);
	}

	public void newTime(Element chronoElt) {
		timer.stop();
		setEnabled(false);

		heures = Integer.parseInt(chronoElt.getAttribute("heures").getValue());
		minutes = Integer.parseInt(chronoElt.getAttribute("minutes").getValue());
		secondes = Integer.parseInt(chronoElt.getAttribute("secondes").getValue());
		setText(String.format("%02d:%02d.%02d", heures, minutes, secondes));
		if (heures + minutes + secondes > 0) {
			playItem.setText("Continuer");

		} else {
			playItem.setText("Démarrer");
		}

		setEnabled(true);
	}

	private void calibrage() {
		startTime = System.currentTimeMillis() - (heures * 3600000L) - (minutes * 60000L) - (secondes * 1000L);
	}

	public Element saveChronometer() {
		Element chronoElt = new Element("Chronometer");
		chronoElt.setAttribute("heures", Integer.toString(heures));
		chronoElt.setAttribute("minutes", Integer.toString(minutes));
		chronoElt.setAttribute("secondes", Integer.toString(secondes));

		return chronoElt;
	}

}
