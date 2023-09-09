package sudoku.model.heuristic;

import sudoku.model.Action;
import sudoku.model.ICase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.model.regions.IRegion;

import sudoku.util.Point;
import sudoku.util.Tools;

/**
 * La classe ForcedChainCandidate implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Candidat forcé en chaîne.
 * 
 * Pseudo-code 
 * 		Variables :
 * 				La grille du modèle - g
 * 				Liste de case qui n'ont pas de valeurs fixé dans g - lst 
 * 				Tableau de grilles - grids
 * 		
 * 		# Première étape --> Lecture et tentative de remplissage
 * 				Pour chaque case de lst
 * 					Créer le tableau de grilles grids qui compte autant de grilles que de candidats
 * 					Pour chaque candidat de case
 * 							Créer une grille en placant le candidat dans la case et l'ajouter dans le tableau
 * 							Remplir au maximum la grille
 * 
 * 					# Seconde étape --> Vérification des résultat après parcours de chaque candidats de la case
 * 					coordCase <- coordonnées de la case courante
 * 					pour chaque parcours (parcours de la Ligne, Colonne, Région ou se trouve la case courante)
 * 							Premier parcours --- servant à savoir si une même case contenue dans chaque grille de grids 
 * 							(parmi la ligne, la colonne et la région) est identique si oui et 
 * 							qu'elle est fixé et qu'elle ne l'était pas au départ alors trouvé
 * 
 * 							Second parcours --- servant à savoir si une même case contenue dans chaque grille de grids 
 * 							(parmi la ligne, la colonne et la région) ont perdu le même candidat alors qu'il est présent 
 * 							dans la grille de départ sur la même case alors trouvé également
 * 
 * 							Si rien a été trouvé alors continuer avec la case suivante
 * 
 * 				si on est à la fin du parcours de lst et rien n'a été trouvé alors renvoyer null
 * 
 * @author Khabouri Izana
 */ 
public class ForcedChainCandidate implements IHeuristic {
	
	// ATTRIBUTS STATIQUES 
	
	private final int EMPTY = 0;
	private final int ONE_CANDIDATE = 1;
	
	// ATTRIBUTS 
	
	private final IGrid grid;
	private List<Point> allCoordinate;
	private Point coordinate;
	private int value;
	private Point coordToIlluminate;
	private final int size;
	private boolean isSet;
	
	// CONSTRUCTEUR
	
	public ForcedChainCandidate(IGrid grid) {
		assert grid != null;
		this.grid = grid;
		this.allCoordinate = new ArrayList<Point>();
		this.value = EMPTY;
		this.coordinate = null;
		this.coordToIlluminate = null;
		this.size = grid.getSize().getSize();
	}
	
	// REQUETES
	
	@Override 
	public Answer compute() {
		boolean found = false;
		
		for (int n = 2; n <= size; n++) {
			searchCandidates(n);
			for (Point p : this.allCoordinate) {
				if (this.resolve(p)) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		
		String msg = (this.coordinate == null) ? null : toAnswer();
		
		Answer answer = found ? new Answer(msg) : null;
		
		if (found) {
			Command cmd = isSet ? Command.SET_VALUE : Command.REM_CANDIDATES;
			answer.addCasesOfProofs(this.coordToIlluminate.getX(), this.coordToIlluminate.getY());
			answer.addRecommendedActions(new Action(coordinate.getX(), coordinate.getY(), 
				this.value, cmd, false));
		}
		
		return answer;
	}
	
	// OUTILS

	/**
	 * Recherche les coordonnées ou se présente les case comportant 
	 * exactement n candidats.
	 */
	private void searchCandidates(int n) {
		this.allCoordinate.clear();
		final int size = this.grid.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = this.grid.getGrid()[i][j];
				if (!c.getIsFixed()) {
					if (c.getCandidates().size() == n) {
						this.allCoordinate.add(new Point(i, j));
					}
				}
			}
		}
	}

	/**
	 * Réalise le travail dans la grille g dans la ligne, dans la colonne 
	 * ainsi que la région contenant le point p.
	 */
	private void work(IGrid g, Point p) {
		boolean workInLines = true;
		boolean workInColumn = true;
		boolean workInRegion = true;
		
		while (workInLines || workInColumn || workInRegion) {
			if (workInLines) {
				actInLine(g, p.getX());
				workInLines = this.canWorkInLines(g, p.getX());
			} 
			
			if (workInColumn) {
				actInColumn(g, p.getY());
				workInColumn = this.canWorkInColumns(g, p.getY());
			}
			
			if (workInRegion) {
				actInRegion(g, p);
				workInRegion = this.canWorkInRegion(g, p);
			}
		}
	}
	
	/**
   * Réalise l'action de fixer une valeur qui se trouve
   * dans une case qui comporte seulement un candidat.
   * La recherche se passe dans la ligne i.
   * Ainsi que la suppression sur la ligne, colonne, et région des
   * cases comportant un seul candidat.
   */
	private void actInLine(IGrid g, int i) {
		final int size = g.getSize().getSize();
		for (int j = 0; j < size; j++) {
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
					int v = (Integer) g.getGrid()[i][j].getCandidates().toArray()[0];
					this.act(g, v, new Point(i, j));
				}
			}
		}
	}
	
	/**
   * Réalise l'action de fixer une valeur qui se trouve
   * dans une case qui comporte seulement un candidat.
   * La recherche se passe dans la colonne j.
   * Ainsi que la suppression sur la ligne, colonne, et région des
   * cases comportant un seul candidat.
   */
	private void actInColumn(IGrid g, int j) {
		final int size = g.getSize().getSize();
			for (int i = 0; i < size; i++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
					int v = (Integer) g.getGrid()[i][j].getCandidates().toArray()[0];
					this.act(g, v, new Point(i, j));
				}
				}
			}
	}
	/**
	 * Réalise l'action de fixer une valeur qui se trouve
	 * dans une case qui comporte seulement un candidat.
	 * La recherche se passe dans la région contenant le point p.
	 * Ainsi que la suppression sur la ligne, colonne, et région des
	 * cases comportant un seul candidat.
	 */
	private void actInRegion(IGrid g, Point p) {
		IRegion r = g.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
					if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
						int v = (Integer) g.getGrid()[i][j].getCandidates().toArray()[0];
						this.act(g, v, new Point(i, j));
					}
				}
			}
		}
	}

	/**
	 * Réalise l'action de fixer la valeur i une valeur sur la coordonnée p,
	 * ainsi que supprimer le candidat i dans toute la ligne, la colonne et également
	 * dans la région.
	 * Cette action se fait sur la grille g.
	 */
	private void act(IGrid g, Integer i, Point p) {
		g.getGrid()[p.getX()][p.getY()].setValue(i);
		g.removingCandidates(i, p.getX(), p.getY());
	}
	
	/**
	 * Regarde s'il existe des cases vides dens le sens qu'il n'a 
	 * aucun candidats aux alentours de la case à la position p.
	 */
	private boolean haveCaseNotEmpty(Point p) {
		// parcours ligne
		
		for (int j = 0; j < size; j++) {
			ICase c = this.grid.getGrid()[p.getX()][j];
			if (!c.getIsFixed()) {
				if (c.getCandidates().size() == 0) {
					return true;
				}
			}
		}
		
		// parcours colonne 
		
		for (int i = 0; i < size; i++) {
			ICase c = this.grid.getGrid()[i][p.getY()];
			if (!c.getIsFixed()) {
				if (c.getCandidates().size() == 0) {
					return true;
				}
			}
		}
		
		// parcours region
		
		IRegion r = grid.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getStartY(); j <= r.getEndY(); j++) {
				ICase c = this.grid.getGrid()[i][j];
				if (!c.getIsFixed()) {
					if (c.getCandidates().size() == 0) {
						return true;
					}
				}
			}
		}
		
		// retour final
		return false;
	}
	
	/**
	 * Résout les différentes grilles en fonction de chaque candidats.
	 */
	private boolean resolve(Point p) {
		if (haveCaseNotEmpty(p)) {
			return false; 
		}
		Set<Integer> draft = grid.getGrid()[p.getX()][p.getY()].getCandidates();
		Object[] values = (Object[]) draft.toArray();
		IGrid[] grids = new IGrid[values.length];
		for (int k = 0; k < values.length; k++) {
			grids[k] = grid.getCopy();
			this.act(grids[k], (Integer) values[k], p);
			this.work(grids[k], p);
		}
		return this.askFoundResult(grids, p);
	}
	
	/**
	 * Vérifie si l'on peut travailler dans la ligne i.
	 * La vérification retourne vrai dans le cas ou au moins une case comporte qu'un
	 * seul candidat.
	 */
	private boolean canWorkInLines(IGrid g, int i) {
		final int size = g.getSize().getSize();
		for (int j = 0; j < size; j++) { 
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Vérifie si l'on peut travailler dans la colonne j.
	 * La vérification retourne vrai dans le cas ou au moins une case comporte qu'un
	 * seul candidat.
	 */
	private boolean canWorkInColumns(IGrid g, int j) {
		final int size = g.getSize().getSize();
		for (int i = 0; i < size; i++) { 
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Vérifie si l'on peut travailler dans la région comportant le point p.
	 * La vérification retourne vrai dans le cas ou au moins une case comporte qu'un
	 * seul candidat.
	 */
	private boolean canWorkInRegion(IGrid g, Point p) {
		IRegion r = g.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
					if (g.getGrid()[i][j].getCandidates().size() == ONE_CANDIDATE) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Retourne si toutes les grilles contenues dans grids sont identique
	 * c'est-à-dire ont fixé le même candidat au point p. 
	 */
	private boolean allIsIdentical(IGrid[] grids, Point p) {
		ICase c = grid.getGrid()[p.getX()][p.getY()];
		ICase testCase = grids[0].getGrid()[p.getX()][p.getY()];
		if (c.getIsFixedByGrid() || c.isEquals(testCase)) {
			return false;
		}
		if (!c.getIsFixed()) {
			for (int k = 1; k < grids.length; k++) {
				ICase otherCase = grids[k].getGrid()[p.getX()][p.getY()];
				if (!testCase.isEquals(otherCase)) {
					return false;
				} 
			}
		}
		if (testCase.getIsFixedByUser()) {
			this.isSet = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Retourne si toutes les grilles contenues dans grids ont perdu le même
	 * candidats au point p.
	 */
	private boolean allLostSameCandidates(IGrid[] grids, Point p) {
		ICase c = grid.getGrid()[p.getX()][p.getY()];
		ICase testCase = grids[0].getGrid()[p.getX()][p.getY()];
		if (c.getIsFixedByGrid() || c.isEquals(testCase)) {
			return false;
		}
		Map<Integer, Integer> numbersLost = new HashMap<Integer, Integer>();
		for (int i = 1; i <= grid.getSize().getSize(); i++) {
			numbersLost.put(i, 0);
		}
		
		if (!c.getIsFixed()) {
			for (int k = 0; k < grids.length; k++) {
				testCase = grids[k].getGrid()[p.getX()][p.getY()];
				if (!testCase.getIsFixed()) {
					for (int i = 1; i <= grid.getSize().getSize(); i++) {
						if (c.getCandidates().contains(i)) {
							if (!testCase.getCandidates().contains(i)) {
								numbersLost.replace(i, numbersLost.get(i) + 1);
							}
						}
					}
				}
			}
		}
		
		if (!testCase.getIsFixedByUser()) {
			final int numbers = grids.length;
			for (Integer key : numbersLost.keySet()) {
				if (numbersLost.get(key) == numbers) {
					isSet = false;
					this.value = key;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Retourne un booléen si les grilles g1 et g2 ont une case identique qui
	 * n'existait pas sur la grille de départ. Cette case est dont le résultat 
	 * de l'heuristique.
	 */
	private boolean askFoundResult(IGrid[] grids, Point p) {
		this.coordToIlluminate = new Point(p.getX(), p.getY());
		
		// Lecture sur la ligne
		
		for (int j = 0; j < size; j++) {
			Point np = new Point(p.getX(), j);
			if (allIsIdentical(grids, np)) {
				this.coordinate = np;
				this.value = grids[0].getGrid()[np.getX()][np.getY()].getValue();
				return true;
			} else {
				if (allLostSameCandidates(grids, np)) {
					this.coordinate = np;
					return true;
				}
			}
		}
		
		// Lecture sur la colonne
		
		for (int i = 0; i < size; i++) {
			Point np = new Point(i, p.getY());
			if (allIsIdentical(grids, np)) {
				this.coordinate = np;
				this.value = grids[0].getGrid()[np.getX()][np.getY()].getValue();
				return true;
			} else {
				if (allLostSameCandidates(grids, np)) {
					this.coordinate = np;
					return true;
				}
			}
		}
		
		// Lecture sur la région
		
		IRegion r = grid.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				Point np = new Point(i, j);
				if (allIsIdentical(grids, np)) {
					this.coordinate = np;
					this.value = grids[0].getGrid()[np.getX()][np.getY()].getValue();
					return true;
				} else {
					if (allLostSameCandidates(grids, np)) {
						this.coordinate = np;
						return true;
					}
				}
			}
		}
		
		// Fin d'analyse et rien n'a été vain

		this.coordToIlluminate = null;
		return false;
	}
	
	/**
	 * Retourne la réponse de l'heuristique en cas de réponse trouvée sous
	 * forme de chaîne de caractères.
	 */
	private String toAnswer() {
		return "Quel que soit les placements que l'on peut faire "
				+ "avec la case en position"
				+ "(" + (coordToIlluminate.getX() + 1) 
				+ ", " + (coordToIlluminate.getY() + 1) + ") "
				+ ((isSet) ? "on placera " : "on supprimera ")
				+ " toujours la valeur suivante : " 
				+ Tools.getData(this.value, grid.getType())
				+ " à la position (" + (coordinate.getX() + 1)
				+ ", " + (coordinate.getY() + 1) + ")";
	}	
}
