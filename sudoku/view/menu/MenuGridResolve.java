package sudoku.view.menu;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import sudoku.model.info.Size;

/**
 * La classe MenuNewGame génère un menu pour la création d'une nouvelle partie.
 * 
 * @author Izana Khabouri
 */
public class MenuGridResolve {

	// CONSTANTES

	public static final Item it = Item.RESOLVE_GRID;
	private static final String GRID_RESOLVE = "Résoudre un sudoku";

	// ATTRIBUT

	private static Map<Size, JMenuItem> items = new HashMap<Size, JMenuItem>();
	private static JMenu menu = createMenu();

	// REQUETES

	/**
	 * Retourne le menu de la création d'une grille de résolution vierge.
	 */
	public static JMenu getJMenuOfGridResolve() {
		return menu;
	}

	/**
	 * Retourne pour chaque label, un item cliquable.
	 */
	public static Map<Size, JMenuItem> getItems() {
		return items;
	}

	// OUTILS

	/**
	 * Crée le menu.
	 */
	private static JMenu createMenu() {
		JMenu menu = new JMenu(GRID_RESOLVE);
		for (Size size : Size.values()) {
			JMenuItem it = new JMenuItem(size.getRepertory());
			items.put(size, it);
			menu.add(it);
		}
		return menu;
	}
}
