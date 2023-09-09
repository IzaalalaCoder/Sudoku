package sudoku.model.info;

import java.awt.Color;

/**
 * Le type énuméré des couleurs qui représente les couleurs
 * des cases de notre sudoku.
 * 
 * @author Khabouri Izana.
 */
public enum ColorSudoku {

	// CONSTANTES

	RED(1, 223, 59, 87, "rouge"),
	GREEN(2, 124, 234, 156, "vert"),
	BLUE(3, 89, 195, 195, "bleu"),
	PINK(4, 230, 170, 206, "rose"),
	YELLOW(5, 255, 200, 87, "jaune"),
	VIOLET(6, 155, 93, 229, "violet"),
	ORANGE(7, 238, 99, 82, "orange"),
	BROWN(8, 125, 92, 101, "marron"),
	GREY(9, 132, 153, 177, "gris"),
	;

	// ATTRIBUTS

	private int red;
	private int green;
	private int blue;
	private int number;
	private String label;

	// CONSTRUCTEUR

	private ColorSudoku(int n, int r, int g, int b, String l) {
		this.number = n;
		this.red = r;
		this.green = g;
		this.blue = b;
		this.label = l;
	}

	// REQUETES

	/**
	 * Retourne la couleur.
	 */
	public Color getColor() {
		return new Color(this.red, this.green, this.blue);
	}

	/**
	 * Retourne la couleur non validé selon le mode d'affichage.
	 */
	public Color getColorNotChecked(boolean isDark) {
		return isDark ? Color.black : Color.white;
	}

	// COLOR REM en fct de dark
	// COLOR KEEP en fct de dark
	// COLOR SET en fct de dark

	/**
	 * Retourne la valeur entre 0 et 255 qui représente le rouge.
	 */
	public int getRed() {
		return this.red;
	}

	/**
	 * Retourne la valeur entre 0 et 255 qui représente le vert.
	 */
	public int getGreen() {
		return this.green;
	}

	/**
	 * Retourne la valeur entre 0 et 255 qui représente le bleu.
	 */
	public int getBlue() {
		return this.blue;
	}

	/**
	 * Retourne le numéro associé à la couleur.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Retourne le nom de la couleur.
	 */
	public String getLabel() {
		return this.label;
	}
}
