package sudoku.view;

import java.awt.Color;

/**
 * L'énumération gère les couleurs de fond pour les cases qui seront en
 * surbrillance
 * lors d'une demande d'aide.
 * 
 * @author Izana Khabouri
 */
public enum DisplayColorAnswer {

	// CONSTANTES

	REM(new Color[] {
			// rouge to orange and yellow
			new Color(106, 4, 15),
			new Color(208, 0, 0),
			new Color(232, 93, 4),
			new Color(250, 163, 7)
	}),
	KEEP(new Color[] {
			// blue
			new Color(3, 4, 94),
			new Color(2, 62, 138),
			new Color(0, 180, 216),
			new Color(144, 224, 239)
	}),
	SET(new Color[] {
			// green
			new Color(0, 75, 35),
			new Color(0, 127, 95),
			new Color(56, 176, 0),
			new Color(85, 166, 48)
	});

	// ATTRIBUTS

	private Color[] colorChart;

	// CONSTRUCTEUR

	private DisplayColorAnswer(Color[] colors) {
		this.colorChart = colors;
	}

	// REQUETES

	/**
	 * Retourne les différentes couleurs associé à la réponse
	 * pour pouvoir afficher les différentes possibilités.
	 */
	public Color[] getColorChart() {
		return this.colorChart;
	}

}
