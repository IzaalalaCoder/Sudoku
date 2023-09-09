package sudoku.view.menu;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.model.info.Type;

import sudoku.util.Tools;

/**
 * La classe MenuNewGame génère un menu pour la création d'une nouvelle partie.
 * 
 * @author Izana Khabouri
 */
public class MenuNewGame {

	// CONSTANTES

	public static final Item it = Item.NEW_GAME;
	public static final String NEW_GAME = "Nouvelle partie";

	// ATTRIBUT

	private static Map<String, JMenuItem> items = new HashMap<String, JMenuItem>();
	private static JMenu menu = createMenu();

	// REQUETES

	/**
	 * Retourne le menu de la création d'une nouvelle parties.
	 */
	public static JMenu getJMenuOfNewGame() {
		return menu;
	}

	/**
	 * Retourne pour chaque label, un item cliquable.
	 */
	public static Map<String, JMenuItem> getItems() {
		return items;
	}

	// OUTILS

	/**
	 * Crée le menu.
	 */
	private static JMenu createMenu() {
		JMenu menu = new JMenu(NEW_GAME);
		for (Size size : Size.values()) {
			JMenu subMenu = new JMenu(size.getNomButton());
			for (Level level : Tools.getCorrectLevel()) {
				JMenu lvl = new JMenu(level.getLevel());
				for (Type type : size.getCanValue()) {
					JMenuItem item = new JMenuItem(type.getLabel());
					String label = size.getNomButton() + " "
							+ " " + level.getLevel() + " " + type.getLabel();
					items.put(label, item);
					lvl.add(item);
				}
				subMenu.add(lvl);
			}
			menu.add(subMenu);
		}
		return menu;
	}
}
