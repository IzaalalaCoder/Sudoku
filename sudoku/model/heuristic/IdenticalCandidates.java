package sudoku.model.heuristic;

import java.util.HashSet;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.model.regions.IRegion;

/**
 * La classe IdenticalCandidates implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Candidats identiques.
 * 
 * @author Yacine Ben Ahmed
 */

//Pseudo-code:
//Pour chaque ligne,colonne,région
//   Pour chaque case C de la grille non résolue
//     NumberOfCandidates =  nombre de candidats de C
//     NombreOfCases =  nombre de cases de la ligne/col/région  courante qui ont exactement  les mêmes candidats
 //        Si NombredeCandidats = NomberOfCases 
 //            Retirer les candidats de C dans les cases de cette ligne/col/région  qui n'ont pas exactement les mêmes candidats que C

public class IdenticalCandidates implements IHeuristic {
	private final IGrid grille;

	public IdenticalCandidates(IGrid grid) {

		grille = grid;
	}

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */

	public Answer compute() {
		String msg = null;
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			for (int j = 0; j < grille.getSize().getSize(); j++) {

				int[] t1 = identicalCandidatesLine(grille, i, j);
				if (t1 != null) {
					msg = " Sur la ligne " + (i + 1)
							+ ", si des cases ont des candidats en communs avec la case de coordonnées (" + (t1[0] + 1)
							+ "," + (t1[1] + 1) + ") sans avoir exactement " +
							" les mêmes candidats alors tu peux supprimer les candidats communs car cette case ainsi que d'autres cases identiques dans la même ligne restreignent ces candidats "
							+ "\n";
					return createAnswer(0, t1[0], t1[1], i, msg);
				}
				int[] t2 = identicalCandidatesCol(grille, i, j);
				if (t2 != null) {

					msg = " Sur la colonne " + (j + 1)
							+ ", si des cases ont des candidats en communs avec la case de coordonnées (" + (t2[0] + 1)
							+ "," + (t2[1] + 1) + ") sans avoir exactement " +
							" les mêmes candidats alors tu peux supprimer les candidats communs car cette case ainsi que d'autres cases identiques dans la même colonne restreignent ces candidats"
							+ "\n";
					return createAnswer(1, t2[0], t2[1], i, msg);
				}

				int[] t3 = identicalCandidatesRegion(grille, i, j);
				if (t3 != null) {
					msg = " Sur la région de le case (" + (t3[0] + 1) + "," + (t3[1] + 1)
							+ "), si des cases ont des candidats en communs avec cette case sans avoir exactement " +
							" les mêmes candidats alors tu peux supprimer les candidats communs car cette case ainsi que d'autres cases identiques dans la même région restreignent ces candidats"
							+ "\n";
					return createAnswer(2, t3[0], t3[1], i, msg);
				}
			}
		}
		return null;

	}

	private Answer createAnswer(int type, int x, int y, int i, String msg) {
		Answer a = new Answer(msg);
		final int size = this.grille.getSize().getSize();
		Action act = null;
		ICase c = grille.getCase(x, y);
		Set<Integer> s = c.getCandidates();
		if (type == 0) {
			for (int j = 0; j < size; j++) {
				ICase ci = grille.getCase(x, j);
				if (!ci.getIsFixed()) {
					Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
					difference.removeAll(s);
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(s);
					if (!intersection.isEmpty() && !difference.isEmpty()) {
						Set<Integer> si = ci.getCandidates();
						for (int xi : si) {
							if (grille.getCase(x, y).getCandidates().contains(xi)) {
								act = new Action(x, j, xi, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}
			}
		}

		if (type == 1) {
			for (int j = 0; j < size; j++) {
				ICase ci = grille.getCase(j, y);
				if (!ci.getIsFixed()) {
					Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
					difference.removeAll(s);
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(s);
					if (!intersection.isEmpty() && !difference.isEmpty()) {
						Set<Integer> si = ci.getCandidates();
						for (int xi : si) {
							if (grille.getCase(x, y).getCandidates().contains(xi)) {
								act = new Action(j, y, xi, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}
			}
		}

		if (type == 2) {
			IRegion region = grille.searchRegion(x, y);
			for (int j = region.getStartX(); j <= region.getEndX(); j++) {
				for (int k = region.getStartY(); k <= region.getEndY(); k++) {
					ICase ci = grille.getCase(j, k);
					if (!ci.getIsFixed()) {
						Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
						difference.removeAll(s);
						Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
						intersection.retainAll(s);
						if (!intersection.isEmpty() && !difference.isEmpty()) {
							Set<Integer> si = ci.getCandidates();
							for (int xi : si) {
								if (grille.getCase(x, y).getCandidates().contains(xi)) {
									act = new Action(j, k, xi, Command.REM_CANDIDATES, false);
									a.addRecommendedActions(act);
								}
							}
						}
					}
				}
			}
		}

		return a;
	}

	private int[] identicalCandidatesLine(IGrid grid, int x, int y) {
		int count = 0;
		boolean changeNeeded = false;
		ICase c = grille.getCase(x, y);
		if (c.getIsFixed()) {
			return null;
		}
		Set<Integer> s = c.getCandidates();
		int size = s.size();
		// recherche de cases identiques
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			ICase ci = grille.getCase(x, i);
			if (!ci.getIsFixed()) {
				Set<Integer> si = ci.getCandidates();
				if (s.equals(si)) {
					++count;
				} else {
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(s);
					difference.removeAll(s);
					if (!intersection.isEmpty() && !difference.isEmpty()) {
						changeNeeded = true;
					}
				}
			}
		}
		if (count == size && changeNeeded == true) {
			int[] result = new int[2];
			result[0] = x;
			result[1] = y;
			return result;
		}
		return null;
	}

	private int[] identicalCandidatesCol(IGrid grid, int x, int y) {
		int count = 0;
		boolean changeNeeded = false;
		ICase c = grille.getCase(x, y);
		if (c.getIsFixed()) {
			return null;
		}
		Set<Integer> s = c.getCandidates();
		int size = s.size();
		// recherche de cases identiques
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			ICase ci = grille.getCase(i, y);
			if (!ci.getIsFixed()) {
				Set<Integer> si = ci.getCandidates();
				if (s.equals(si)) {
					++count;
				} else {
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(s);
					difference.removeAll(s);
					if (!intersection.isEmpty() && !difference.isEmpty()) {
						changeNeeded = true;
					}
				}
			}
		}
		if (count == size && changeNeeded == true) {
			int[] result = new int[2];
			result[0] = x;
			result[1] = y;
			return result;
		}
		return null;
	}

	private int[] identicalCandidatesRegion(IGrid grid, int x, int y) {
		int count = 0;
		boolean changeNeeded = false;
		ICase c = grille.getCase(x, y);
		if (c.getIsFixed()) {
			return null;
		}
		Set<Integer> s = c.getCandidates();
		int size = s.size();

		IRegion region = grid.searchRegion(x, y);

		for (int i = region.getStartX(); i <= region.getEndX(); i++) {
			for (int j = region.getStartY(); j <= region.getEndY(); j++) {
				ICase ci = grille.getCase(i, j);
				if (!ci.getIsFixed()) {
					Set<Integer> si = ci.getCandidates();
					if (s.equals(si)) {
						++count;
					} else {
						Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
						Set<Integer> difference = new HashSet<Integer>(ci.getCandidates());
						intersection.retainAll(s);
						difference.removeAll(s);
						if (!intersection.isEmpty() && !difference.isEmpty()) {
							changeNeeded = true;
						}
					}
				}
			}
		}
		if (count == size && changeNeeded == true) {
			int[] result = new int[2];
			result[0] = x;
			result[1] = y;
			return result;
		}
		return null;
	}
}
