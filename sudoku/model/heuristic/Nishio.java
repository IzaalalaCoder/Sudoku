package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;
import sudoku.model.regions.IRegion;
import sudoku.util.Point;
import sudoku.util.Tools;

/**
 * La classe Nishio implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Nishio.
 * 
 * @author Khabouri Izana
 */
public class Nishio implements IHeuristic {

	// CONSTANTES

	private final int NBR_CANDIDATES = 2;

	// REQUETES

	private final IGrid model;
	private List<Point> coords;
	private Point coordToIlluminate;
	private Point coordinate;
	private int value;

	// CONSTRUCTEUR

	public Nishio(IGrid m) {
		assert m != null;
		this.model = m;
		this.coords = new ArrayList<Point>();
		this.coordinate = null;
		this.value = ICase.CASE_EMPTY;
	}

	// REQUETES

	@Override
	public Answer compute() {
		if (!checkReadyGridToRead()) {
			return null;
		}
		this.researchAllCoordinates();
		this.searchResolve();
		String msg = (this.coordinate == null) ? null : toAnswer();
		Answer answer = msg == null ? null : new Answer(msg);
		if (answer != null) {
			answer.addCasesOfProofs(coordToIlluminate.getX(),
					coordToIlluminate.getY());
			answer.addRecommendedActions(new Action(coordinate.getX(),
					coordinate.getY(),
					this.value, Command.REM_CANDIDATES, false));
		}
		return answer;
	}

	// OUTILS

	/**
	 * Cherche a résoudre jusqu'a que l'on rencontre une contradiction.
	 */
	private void searchResolve() {
		for (Point p : this.coords) {
			final int x = p.getX();
			final int y = p.getY();
			for (Integer i : model.getGrid()[x][y].getCandidates()) {
				IGrid g = this.model.getCopy();
				this.act(g, i, p);
				this.work(g);
				if (searchContradiction(g)) {
					this.value = i;
					this.coordinate = p;
					return;
				}
			}
		}
	}

	/**
	 * Rempli la grille tant qu'elle peut être rempli.
	 */
	private void work(IGrid grid) {
		boolean workInGrid = true;
		while (workInGrid) {
			actInGrid(grid);
			if (searchContradiction(grid)) {
				return;
			}
			workInGrid = this.canWorkInGrid(grid);
		}
	}

	/**
	 * Travaille dans la grille en remplissant cette dernière.
	 */
	private void actInGrid(IGrid g) {
		final int size = model.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = g.getCase(i, j);
				if (!c.getIsFixed()) {
					if (c.getCandidates().size() == 1) {
						Point p = new Point(i, j);
						int value = (Integer) c.getCandidates().toArray()[0];
						this.act(g, value, p);
					}
				}
			}
		}
	}

	/**
	 * Réalise l'action de fixer la valeur i une valeur sur la coordonnée p,
	 * ainsi que supprimer le candidat i dans toute la ligne, la colonne et
	 * également
	 * dans la région.
	 * Cette action se fait sur la grille g.
	 */
	private void act(IGrid g, Integer i, Point p) {
		g.getGrid()[p.getX()][p.getY()].setValue(i);
		g.removingCandidates(i, p.getX(), p.getY());
	}

	/**
	 * Recherche et récupère toutes les cases ou l'on constate un certain nombre de
	 * candidats.
	 */
	private void researchAllCoordinates() {
		ICase[][] cases = model.getGrid();
		final int size = model.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (!cases[i][j].getIsFixed()) {
					if (cases[i][j].getCandidates().size() == NBR_CANDIDATES) {
						this.coords.add(new Point(i, j));
					}
				}
			}
		}
	}

	/**
	 * Retourne vrai si une contradiction est présente dans la grille faux sinon.
	 */
	private boolean searchContradiction(IGrid grid) {
		final int size = model.getSize().getSize();
		if (grid.getCanAutoComplete()) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					ICase c = grid.getCase(i, j);
					if (!c.getIsFixed()) {
						if (c.getCandidates().size() == 0) {
							this.coordToIlluminate = new Point(i, j);
							return true;
						}
					}
				}
			}
		} else {
			// vérifier la présence de doublons dans le cas ou
			// l'utilisateur ne souhaite pas utiliser l'autocomplétion

			Set<Integer> number = new HashSet<Integer>();
			// PARCOURS LIGNE
			for (int i = 0; i < grid.getSize().getSize(); i++) {
				for (int j = 0; j < grid.getSize().getSize(); j++) {
					ICase c = grid.getCase(i, j);
					if (number.contains(c.getValue())) {
						return true;
					} else {
						number.add(c.getValue());
					}
				}
			}

			// PARCOURS COLONNE
			number.clear();
			for (int j = 0; j < grid.getSize().getSize(); j++) {
				for (int i = 0; i < grid.getSize().getSize(); i++) {
					ICase c = grid.getCase(i, j);
					if (number.contains(c.getValue())) {
						return true;
					} else {
						number.add(c.getValue());
					}
				}
			}

			// PARCOURS REGION
			number.clear();
			for (IRegion r : grid.getSize().getTypeRegion()) {
				for (int i = r.getStartX(); i <= r.getEndX(); i++) {
					for (int j = r.getStartY(); j <= r.getEndY(); j++) {
						ICase c = grid.getCase(i, j);
						if (number.contains(c.getValue())) {
							return true;
						} else {
							number.add(c.getValue());
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Retourne vrai si on peut continuer à placer des valeurs faux sinon.
	 */
	private boolean canWorkInGrid(IGrid g) {
		final int size = model.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = g.getCase(i, j);
				if (!c.getIsFixed()) {
					if (c.getCandidates().size() == 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Retourne vrai si la grille est prête à exécuter l'algorithme.
	 * En cas de case vide contenant zéro candidates présente dans la grille dès le
	 * départ alors
	 * l'algorithme peut s'avérer faux s'il travaille sur la grille.
	 */
	private boolean checkReadyGridToRead() {
		final int size = model.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = model.getCase(i, j);
				if (!c.getIsFixed()) {
					if (c.getCandidates().size() == 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Crée le message de retour.
	 */
	private String toAnswer() {
		return "Si l'on place "
				+ Tools.getData(this.value, model.getType())
				+ " à la position ("
				+ (this.coordinate.getX() + 1)
				+ ", " + (this.coordinate.getY() + 1) + ") cela "
				+ "provoque une contradiction à la position ("
				+ (this.coordToIlluminate.getX() + 1)
				+ ", " + (this.coordToIlluminate.getY() + 1) + ") "
				+ "donc on peut supprimer le candidat de"
				+ " la première case";
	}
}
