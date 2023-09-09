package sudoku.view.menu;

import java.awt.event.KeyEvent;

/**
 * Le type énuméré Menu représente tous les onglets du menu ainsi
 * que leurs informations.
 * 
 * @author Khabouri Izana
 */
public enum Menu {

	// CONSTANTES

	GAME("Jeu", new Item[] {
			Item.NEW_GAME,
			Item.CHANGE_DATA,
			Item.RESET_GAME,
			null,
			Item.SAVE_GAME,
			Item.LOAD_GAME,
			Item.RESOLVE_GRID
	}, KeyEvent.VK_J),
	NAVIGATE("Navigation", new Item[] {
			Item.REDO_GAME,
			Item.UNDO_GAME
	}, KeyEvent.VK_N),
	OTHER("Autre", new Item[] {
			Item.PREFERENCE_GAME,
			null,
			Item.HELP_GAME,
			null,
			Item.QUIT_GAME
	}, KeyEvent.VK_A),
	HEURISTIQUES("Heuristiques", new Item[] {
			Item.TURBOTFISH,
			Item.BURMA
	}, KeyEvent.VK_H);

	// ATTRIBUTS

	private String menu;
	private Item[] items;
	private int mnemonic;

	// CONSTRUCTEURS

	private Menu(String label, Item[] menus, int mnemonic) {
		this.menu = label;
		this.items = menus;
		this.mnemonic = mnemonic;
	}

	// REQUETES

	/**
	 * Retourne le nom du menu principal.
	 */
	public String getMenu() {
		return this.menu;
	}

	/**
	 * Retourne la liste des items correspondant au thèmes du menu.
	 */
	public Item[] getItems() {
		return this.items;
	}

	/**
	 * Retourne la commande clavier correspondant au menu.
	 */
	public int getMnemonic() {
		return this.mnemonic;
	}
}
