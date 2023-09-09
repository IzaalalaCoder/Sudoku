package sudoku.model.info;

import java.awt.Dimension;

import sudoku.model.regions.IRegion;
import sudoku.model.regions.RegionNine;
import sudoku.model.regions.RegionEight;
import sudoku.model.regions.RegionSix;
import sudoku.model.regions.RegionSixTeen;
import sudoku.model.regions.RegionTen;
import sudoku.model.regions.RegionTwelve;
import sudoku.model.regions.RegionFour;

/**
 * Le type énuméré Size représente les différentes tailles possibles
 * pour le jeu du sudoku.
 * 
 * @author Khabouri Izana.
 */
public enum Size {

	// CONSTANTES

	SIXTEEN(16, 4, 4, 6, new Type[] { Type.LETTER, Type.SYMBOL }, "16x16", "Seize", true) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionSixTeen.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(160, 160);
		}
	},
	TWELVE(12, 3, 4, 7, new Type[] { Type.LETTER, Type.SYMBOL }, "12x12", "Douze", false) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionTwelve.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(200, 130);
		}
	},
	TEN(10, 2, 5, 7, new Type[] { Type.LETTER, Type.SYMBOL }, "10x10", "Dix", false) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionTen.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(254, 104);
		}
	},
	NINE(9, 3, 3, 13, Type.values(), "9x9", "Neuf", true) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionNine.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(175, 175);
		}
	},
	EIGHT(8, 2, 4, 13, Type.values(), "8x8", "Huit", false) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionEight.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(280, 130);
		}
	},
	SIX(6, 2, 3, 15, Type.values(), "6x6", "Six", false) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionSix.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(205, 140);
		}
	},
	FOUR(4, 2, 2, 20, Type.values(), "4x4", "Quatre", true) {
		@Override
		public IRegion[] getTypeRegion() {
			return RegionFour.values();
		}

		@Override
		public Dimension getDimension() {
			return new Dimension(150, 150);
		}
	};

	// ATTRIBUTS

	private int size;
	private int numberRegionCol;
	private int numberRegionLin;
	private String nomButton;
	private String folder;
	private Type[] canValue;
	private boolean perfectSquare;
	private int sizeFont;

	// CONSTRUCTEUR

	private Size(int size, int lin, int col, int font, Type[] t, String nomButton, String folder, boolean ps) {
		this.size = size;
		this.numberRegionLin = lin;
		this.numberRegionCol = col;
		this.canValue = t;
		this.nomButton = nomButton;
		this.folder = folder;
		this.perfectSquare = ps;
		this.sizeFont = font;
	}

	// REQUETES

	/**
	 * Retourne le nombre de région par lignes.
	 */
	public int getNumberRegionLines() {
		return this.numberRegionLin;
	}

	/**
	 * Retourne le nombre de région par colonnes.
	 */
	public int getNumberRegionCols() {
		return this.numberRegionCol;
	}

	/**
	 * Retourne la taille.
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Retourne la taille de la police.
	 */
	public int getSizeDraftFont() {
		return this.sizeFont;
	}

	/**
	 * Retourne les types de données qu'elles peut prendre.
	 */
	public Type[] getCanValue() {
		return this.canValue;
	}

	/**
	 * Retourne le dossier associé à la taille du sudoku.
	 */
	public String getRepertory() {
		return this.folder;
	}

	public String getNomButton() {
		return this.nomButton;
	}

	/**
	 * Retourne si la taille du sudoku est un carré parfait.
	 */
	public boolean getPerfectQuare() {
		return this.perfectSquare;
	}

	/**
	 * Retourne les différentes régions existantes.
	 */
	public abstract IRegion[] getTypeRegion();

	/**
	 * Retourne la dimension appropriée pour l'affichage de la grille.
	 */
	public abstract Dimension getDimension();
}
