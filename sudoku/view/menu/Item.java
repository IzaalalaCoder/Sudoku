package sudoku.view.menu;

/**
 * Le type énuméré Item représente toutes les commandes du menu.
 * 
 * @author Khabouri Izana
 */
public enum Item {

	// CONSTANTES

	NEW_GAME(null),
	RESET_GAME("Effacer la grille"),
	CHANGE_DATA("Changer le type"),
	SAVE_GAME("Sauvegarder la partie"),
	LOAD_GAME("Charger une partie"),
	UNDO_GAME("Annuler"),
	REDO_GAME("Refaire"),
	PREFERENCE_GAME("Preferences"),
	HELP_GAME("Comment jouer ?"),
	QUIT_GAME("Quitter"),
	RESOLVE_GRID(null),
	TURBOTFISH("TurbotFish"),
	BURMA("Burma");

	// ATTRIBUTS

	private String label;

	// CONSTRUCTEUR

	private Item(String lbl) {
		this.label = lbl;
	}

	// REQUETES

	/**
	 * Retourne la chaîne de caractères relié à l'item.
	 */
	public String getLabel() {
		return this.label;
	}
}
