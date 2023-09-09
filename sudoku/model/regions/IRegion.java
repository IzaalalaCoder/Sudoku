package sudoku.model.regions;

/**
 * L'interface IRegion est une interface qui gère toutes
 * les différentes tailles en terme de sudoku.
 * 
 * @author Khabouri Izana
 */
public interface IRegion {

	/**
	 * Retourne le point de départ en abscisse.
	 */
	public int getStartX();

	/**
	 * Retourne le point d'arrivé en abscisse.
	 */
	public int getEndX();

	/**
	 * Retourne le point de départ en ordonné.
	 */
	public int getStartY();

	/**
	 * Retourne le point d'arrivé en ordonné.
	 */
	public int getEndY();

}
