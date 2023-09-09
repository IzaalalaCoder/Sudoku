package sudoku.util;

import java.io.File;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

import java.util.Random;

import sudoku.model.info.ColorSudoku;
import sudoku.model.info.Level;
import sudoku.model.info.SpecialSymbols;
import sudoku.model.info.Type;

public class Tools {

	// STATIQUES

	/**
	 * Retourne un nombre aléatoire entre 0 et max - 1;
	 */
	public static int getAlea(int max) {
		Random random = new Random();
		return random.nextInt(max);
	}

	/**
	 * Retourne la valeur en fonction du type du model.
	 */
	public static String getData(int x, Type t) {
		String str = null;
		switch (t) {
			case LETTER:
				str = Character.toString((char) (65 + x - 1));
				break;
			case SYMBOL:
				str = SpecialSymbols.values()[x - 1].getSymbol();
				break;
			case COLOR:
				str = ColorSudoku.values()[x - 1].getLabel();
				break;
			case INTEGER:
				str = Integer.toString(x);
				break;
			default:
				break;
		}

		return str;
	}

	/**
	 * Retourne tous les niveau sauf celui INCONNU.
	 */
	public static Level[] getCorrectLevel() {
		Level[] lvlForGrid = new Level[Level.values().length - 1];
		int index = 0;
		while (index < Level.values().length - 1) {
			lvlForGrid[index] = Level.values()[index];
			index += 1;
		}
		return lvlForGrid;
	}

	// OUTILS

	/**
	 * Retourne le chemin absolu par rapport au chemin relatif.
	 */
	public String getAbsolute(String relatif) {
		URL url = getClass().getResource(relatif);
		URI uri = null;
		if (url == null) {
			return null;
		}
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
		}
		File f = new File(uri);
		return f.getAbsolutePath();
	}

}
