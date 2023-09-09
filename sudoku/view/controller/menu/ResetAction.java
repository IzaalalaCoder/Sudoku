package sudoku.view.controller.menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import sudoku.model.IGrid;

/**
 * La classe ResetAction implémente l'interface ActionListener.
 * Elle gère l'action d'effacement de la grille.
 * 
 * @author Khabouri Izana.
 */
public class ResetAction implements ActionListener {

	// ATTRIBUTS

	private IGrid model;

	// CONSTRUCTEUR

	public ResetAction(IGrid m) {
		assert m != null;

		model = m;
	}

	// COMMANDES

	@Override
	public void actionPerformed(ActionEvent e) {
		model.clear();
	}
}
