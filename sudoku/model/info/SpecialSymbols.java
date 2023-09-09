package sudoku.model.info;

/**
 * La classe enum SpecialSymbols représente les caractères
 * spéciaux.
 * 
 * @author Izana Khabouri
 */
public enum SpecialSymbols {

	// CONSTANTES

	AROBASE(1, "@"),
	EXCLAMATION_MARK(2, "!"),
	HASHTAG(3, "#"),
	SUPERIOR(4, ">"),
	EQUAL(5, "="),
	QUESTION_MARK(6, "?"),
	SUM(7, "+"),
	DOLLAR(8, "$"),
	STAR(9, "*"),
	TIRET(10, "-"),
	PERCENTAGE(11, "%"),
	AMPERSAND(12, "&"),
	SLASH(13, "\\"),
	EURO(14, "€"),
	COPYRIGHT(15, "©"),
	BRACE(16, "{");

	// ATTRIBUTS

	private String symbol;
	private int number;

	// CONSTRUCTEURS

	private SpecialSymbols(int nbr, String sbl) {
		this.symbol = sbl;
		this.number = nbr;
	}

	// REQUETES

	/**
	 * Retourne le numéro associé au caractère spécial.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Retourne le caractère spécial sous forme de chaîne de
	 * caractères.
	 */
	public String getSymbol() {
		return this.symbol;
	}
}
