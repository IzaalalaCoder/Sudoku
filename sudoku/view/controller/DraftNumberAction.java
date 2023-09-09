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
import sudoku.model.info.SpecialSymbols;
import sudoku.model.info.Type;

import sudoku.view.MessageUser;

/**
 * La classe DraftNumberAction implémente l'interface MouseListener
 * gère les évènements qui sont donc reliés aux candidats.
 * 
 * @author Khabouri Izana
 */
public class DraftNumberAction implements MouseListener {

	// CONSTANTES

	private final int COUNT_CLICK_ADD_CANDIDATE = 1;
	private final int COUNT_CLICK_REM_CANDIDATE = 2;
	private final int COUNT_CLICK_SET_VALUE = 1;
	private final int BUTTON_FOR_CANDIDATES = MouseEvent.BUTTON1;
	private final int BUTTON_FOR_SET_VALUE = MouseEvent.BUTTON3;
	private final boolean USE_HISTORIC = true;

	public final int SIZE_DRAFT_EXITED = 15;
	public final int SIZE_DRAFT_ENTERED = 17;

	public final int REM = 3;

	// ATTRIBUT

	private IGrid model;
	private int positionX;
	private int positionY;
	private boolean isColor;
	private boolean isDark;

	// CONSTRUCTEUR

	public DraftNumberAction(IGrid grid, int i, int j, boolean d) {
		assert grid != null;
		this.model = grid;
		this.positionX = i;
		this.positionY = j;
		this.isColor = this.model.getType() == Type.COLOR;
		this.isDark = d;
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
			int number = this.getNumber((JLabel) e.getSource());
			if (e.getButton() == BUTTON_FOR_CANDIDATES) {
				if (e.getClickCount() == COUNT_CLICK_REM_CANDIDATE) {
					try {
						model.remCandidate(positionX, positionY, number, USE_HISTORIC);
					} catch (PropertyVetoException e1) {
					}
				} else if (e.getClickCount() == COUNT_CLICK_ADD_CANDIDATE) {
					try {
						model.addCandidate(positionX, positionY, number, USE_HISTORIC);
					} catch (PropertyVetoException e1) {
					}
				}
			} else if (e.getButton() == BUTTON_FOR_SET_VALUE) {
				if (e.getClickCount() == COUNT_CLICK_SET_VALUE) {
					try {
						model.setValue(positionX, positionY, number, USE_HISTORIC);
						if (this.model.getCanAutoComplete()) {
							this.model.removingCandidates(number, positionX, positionY);
						}
					} catch (PropertyVetoException e1) {
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel lbl = (JLabel) e.getSource();
		if (!isColor) {
			int sizeFont = lbl.getFont().getSize() + REM;
			lbl.setFont(new Font("Serif", Font.BOLD, sizeFont));
		} else {
			JPanel p = (JPanel) lbl.getParent();
			int number = this.getNumber(lbl);
			ColorSudoku cs = ColorSudoku.values()[number - 1];
			Color c = cs.getColor().brighter();
			p.setBackground(c);
			lbl.setForeground(c);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JLabel lbl = (JLabel) e.getSource();
		if (!isColor) {
			int sizeFont = lbl.getFont().getSize() - REM;
			lbl.setFont(new Font("Serif", Font.PLAIN, sizeFont));
		} else {
			int number = this.getNumber(lbl);
			JPanel p = (JPanel) lbl.getParent();
			ColorSudoku cs = ColorSudoku.values()[number - 1];
			ICase c = this.model.getGrid()[positionX][positionY];
			boolean contains = c.getCandidates().contains(number);
			p.setBackground(contains ? cs.getColor() : cs.getColorNotChecked(isDark));
			lbl.setForeground(contains ? cs.getColor() : cs.getColorNotChecked(isDark));
		}
	}

	// OUTILS

	/**
	 * A partir du contenu du label, la méthode retourne le numéro associé.
	 */
	private int getNumber(JLabel lbl) {
		int number = 0;
		switch (model.getType()) {
			case COLOR:
				for (ColorSudoku color : ColorSudoku.values()) {
					if (color.getLabel().equals(lbl.getText())) {
						number = color.getNumber();
						break;
					}
				}
				break;
			case INTEGER:
				number = Integer.parseInt(lbl.getText());
				break;
			case LETTER:
				number = lbl.getText().charAt(0) - 64;
				break;
			case SYMBOL:
				for (SpecialSymbols sbl : SpecialSymbols.values()) {
					if (sbl.getSymbol().equals(lbl.getText())) {
						number = sbl.getNumber();
						break;
					}
				}
				break;
			default:
				break;

		}
		return number;
	}

}
