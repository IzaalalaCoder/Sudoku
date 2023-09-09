package sudoku.model;

import sudoku.model.info.Command;

/**
 * @author Izana Khabouri
 *         La classe Action représente la commande qui a été réalisé à
 *         une position connue qui change d'état de notre valeur.
 */
public class Action {

	// ATTTRIBUTS CONSTANTES

	private final int NOTHING = -1;

	// ATTRIBUTS

	private int positionX;
	private int positionY;
	private int value;
	private Command command;
	private boolean forced;

	// CONSTRUCTEUR

	public Action(int x, int y, int val, Command cmd, boolean f) {
		if (val < 1 || cmd == null) {
			throw new AssertionError();
		}

		this.positionX = x;
		this.positionY = y;
		this.value = val;
		this.command = cmd;
		this.forced = f;
	}

	public Action(Command cmd) {
		assert cmd != null;

		this.positionX = NOTHING;
		this.positionY = NOTHING;
		this.value = NOTHING;
		this.command = cmd;
	}

	// REQUETES

	/**
	 * Retourne la position en abscisse.
	 */
	public int getPositionX() {
		return this.positionX;
	}

	/**
	 * Retourne la position en ordonnée.
	 */
	public int getPositionY() {
		return this.positionY;
	}

	/**
	 * Retourne la valeur qui a donc subi l'action de la commande.
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Retourne la commande effectuée.
	 */
	public Command getCommand() {
		return this.command;
	}

	/**
	 * Retourne si l'utilisation des candidats est forcé.
	 */
	public boolean getForcedUseCandidate() {
		return this.forced;
	}

	// OUTILS

	@Override
	/**
	 * Explication de l'action.
	 */
	public String toString() {
		return this.command.getExplication() + this.value + " à la position ["
				+ this.positionX + ", " + this.positionY + "] \n";
	}
}
