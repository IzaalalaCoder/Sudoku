package sudoku.model.info;

import sudoku.model.Action;

/**
 * Le type énuméré des commandes possibles a effectuer sur la
 * grille du sudoku.
 * 
 * @author Khabouri Izana
 */
public enum Command {

	// CONSTANTES

	REM_CANDIDATES("Suppression d'un candidat") {
		@Override
		public Command getOpposite() {
			return ADD_CANDIDATES;
		}
	},
	ADD_CANDIDATES("Ajout d'un candidat") {
		@Override
		public Command getOpposite() {
			return REM_CANDIDATES;
		}
	},
	KEEP_CANDIDATE("Garder un candidat") {
		@Override
		public Command getOpposite() {
			return null;
		}
	},
	SET_VALUE("Ecriture d'un nombre") {
		@Override
		public Command getOpposite() {
			return UNSET_VALUE;
		}
	},
	UNSET_VALUE("Effacement d'un nombre") {
		@Override
		public Command getOpposite() {
			return SET_VALUE;
		}
	},
	UNDO_CMD("Annuler commande") {
		@Override
		public Command getOpposite() {
			return null;
		}
	},
	REDO_CMD("Refaire commande") {
		@Override
		public Command getOpposite() {
			return null;
		}
	},
	NOTHING_CMD("Aucune commande") {
		@Override
		public Command getOpposite() {
			return null;
		}
	};

	// ATTRIBUTS

	private String explication;

	// CONSTRUCTEUR

	private Command(String explication) {
		this.explication = explication;
	}

	// REQUETES

	/**
	 * Retourne la commande sous forme de chaîne de caractères.
	 */
	public String getExplication() {
		return this.explication;
	}

	/**
	 * Retourne l'action réalisé.
	 */
	public Action getAction(int x, int y, int value) {
		return new Action(x, y, value, this, false);
	}

	/**
	 * Retourne la commande opposé.
	 */
	public abstract Command getOpposite();
}
