package sudoku.view.controller;

import java.awt.Font;
import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.ColorSudoku;
import sudoku.model.info.Type;
import sudoku.view.MessageUser;

/**
 * La classe NumberAction implémente l'interface MouseListener
 * gère les évènements qui sont donc numéros qui sont fixés par
 * le joueur.
 * 
 * @author Khabouri Izana
 */
public class NumberAction implements MouseListener {

	// CONSTANTE

	private final int COUNT_CLICK = 1;
	private final boolean USE_HISTORIC = true;

	// ATTRIBUT

	private IGrid model;
	private int positionX;
	private int positionY;

	// CONSTRUCTEUR

	public NumberAction(IGrid grid, int i, int j) {
		assert grid != null;
		this.model = grid;
		this.positionX = i;
		this.positionY = j;
	}

	// COMMANDES

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (model.getFinished()) {
			MessageUser.messageInformation(null, "La partie est terminé ! \n"
					+ "Veuillez choisir une nouvelle partie.");
		} else {
			if (e.getClickCount() == COUNT_CLICK && e.getButton() == MouseEvent.BUTTON3) {
				ICase c = this.model.getGrid()[positionX][positionY];
				int number = c.getValue();
				try {
					model.unsetValue(positionX, positionY, number, USE_HISTORIC);
				} catch (PropertyVetoException e1) {
				}
				model.addingCandidates(number, positionX, positionY);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (this.model.getType() != Type.COLOR) {
			JLabel lbl = (JLabel) e.getSource();
			lbl.setFont(new Font("Serif", Font.BOLD, 27));
		} else {
			JPanel p = (JPanel) e.getSource();
			JLabel lbl = (JLabel) p.getComponents()[0];
			Color c = Color.white;
			p.setBackground(c);
			lbl.setForeground(c);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (this.model.getType() != Type.COLOR) {
			JLabel lbl = (JLabel) e.getSource();
			lbl.setFont(new Font("Serif", Font.PLAIN, 25));
		} else {
			JPanel p = (JPanel) e.getSource();
			JLabel lbl = (JLabel) p.getComponents()[0];
			int number = this.model.getGrid()[positionX][positionY].getValue();
			ColorSudoku cs = ColorSudoku.values()[number - 1];
			p.setBackground(cs.getColor());
			lbl.setForeground(cs.getColor());
		}
	}
}
