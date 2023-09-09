package sudoku.view.controller.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import sudoku.model.Action;
import sudoku.model.IGrid;

import sudoku.view.MessageUser;

/**
 * La classe UndoAction implémente l'interface ActionListener.
 * Elle permet d'annuler l'action courante.
 * 
 * @author Khabouri Izana
 */
public class UndoAction implements ActionListener {

	// ATTRIBUTS

	private IGrid model;

	// CONSTRUCTEUR

	public UndoAction(IGrid m) {
		assert m != null;
		this.model = m;
	}

	// COMMANDES

	@Override
	public void actionPerformed(ActionEvent e) {
		if (model.getFinished()) {
			MessageUser.messageInformation(null, "La partie est terminé ! \n"
					+ "Veuillez choisir une nouvelle partie.");
			return;
		}

		Action act = this.model.getAllAction().getElementCurrent();
		this.model.getAllAction().undo();

		if (act == null) {
			return;
		}

		int x = act.getPositionX();
		int y = act.getPositionY();
		int v = act.getValue();

		switch (act.getCommand().getOpposite()) {
			case REM_CANDIDATES:
				try {
					this.model.remCandidate(x, y, v, false);
				} catch (PropertyVetoException e1) {
				}
				break;
			case ADD_CANDIDATES:
				try {
					this.model.addCandidate(x, y, v, false);
				} catch (PropertyVetoException e1) {
				}
				break;
			case SET_VALUE:
				try {
					this.model.setValue(x, y, v, false);
				} catch (PropertyVetoException e1) {
				}
				if (this.model.getCanAutoComplete()) {
					model.removingCandidates(v, x, y);
				}
				break;
			case UNSET_VALUE:
				try {
					this.model.unsetValue(x, y, v, false);
				} catch (PropertyVetoException e1) {
				}
				if (this.model.getCanAutoComplete()) {
					model.addingCandidates(v, x, y);
				}
				break;
			default:
				break;
		}
	}
}
