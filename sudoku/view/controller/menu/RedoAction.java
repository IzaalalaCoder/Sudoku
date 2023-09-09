package sudoku.view.controller.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import sudoku.model.Action;
import sudoku.model.IGrid;

import sudoku.view.MessageUser;

/**
 * La classe RedoAction implémente l'interface ActionListener.
 * Elle permet de refaire une action passée.
 * 
 * @author Khabouri Izana
 */
public class RedoAction implements ActionListener {

	// CONSTANTES

	private final boolean USE_HISTORIC = false;

	// ATTRIBUTS

	private IGrid model;

	// CONSTRUCTEUR

	public RedoAction(IGrid m) {
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
		this.model.getAllAction().redo();
		Action act = this.model.getAllAction().getElementCurrent();

		if (act == null) {
			return;
		}

		int x = act.getPositionX();
		int y = act.getPositionY();
		int v = act.getValue();

		switch (act.getCommand()) {
			case REM_CANDIDATES:
				try {
					this.model.remCandidate(x, y, v, USE_HISTORIC);
				} catch (PropertyVetoException exception) {
				}
				break;
			case ADD_CANDIDATES:
				try {
					this.model.addCandidate(x, y, v, USE_HISTORIC);
				} catch (PropertyVetoException exception) {
				}
				break;
			case SET_VALUE:
				try {
					this.model.setValue(x, y, v, false);
				} catch (PropertyVetoException exception) {
				}
				if (this.model.getCanAutoComplete()) {
					this.model.removingCandidates(act.getValue(), act.getPositionX(), act.getPositionY());
				}
				break;
			case UNSET_VALUE:
				try {
					this.model.unsetValue(x, y, v, USE_HISTORIC);
				} catch (PropertyVetoException exception) {
				}
				if (this.model.getCanAutoComplete()) {
					this.model.addingCandidates(v, x, y);
				}
				break;
			default:
				break;
		}
	}
}
