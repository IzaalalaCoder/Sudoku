package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 12x12.
 * 
 * @author Khabouri Izana
 */
public enum RegionTwelve implements IRegion {

	// CONSTANTES

	REGION1(0, 2, 0, 3),
	REGION2(0, 2, 4, 7),
	REGION3(0, 2, 8, 11),
	REGION4(3, 5, 0, 3),
	REGION5(3, 5, 4, 7),
	REGION6(3, 5, 8, 11),
	REGION7(6, 8, 0, 3),
	REGION8(6, 8, 4, 7),
	REGION9(6, 8, 8, 11),
	REGION10(9, 11, 0, 3),
	REGION11(9, 11, 4, 7),
	REGION12(9, 11, 8, 11);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionTwelve(int startX, int endX, int startY, int endY) {
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
	}

	// REQUETES

	@Override
	public int getStartX() {
		return this.startX;
	}

	@Override
	public int getEndX() {
		return this.endX;
	}

	@Override
	public int getStartY() {
		return this.startY;
	}

	@Override
	public int getEndY() {
		return this.endY;
	}

}
