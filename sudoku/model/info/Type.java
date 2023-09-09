package sudoku.model.info;

/**
 * La classe enum Type représente les différents type de données ainsi que
 * l'accès
 * dans les futurs templates.
 * 
 * @author Izana Khabouri
 */
public enum Type {

	// CONSTANTE

	INTEGER("Nombre", "number/"),
	COLOR("Couleur", "color/"),
	LETTER("Lettre", "letter/"),
	SYMBOL("Symbole", "symbols/");

	// ATTRIBUTS

	private String label;
	private String path;

	// CONSTRUCTEUR

	private Type(String lbl, String path) {
		this.label = lbl;
		this.path = path;
	}

	// REQUETES

	/**
	 * Retourne le type de données sous forme de chaîne de caractères.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Retourne le chemin vers le dossier correspondant au type de données.
	 */
	public String getPath() {
		return this.path;
	}
}
