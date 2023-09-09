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

/**
 * La classe IdenticalCandidates implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Candidats identiques.
 * 
 * @author Yacine Ben Ahmed
 */

// Pseudo-code:
// Pour chaque ligne, colonne, région
// Créer un Set Integer  pour chaque valeur i
// Si une case de le grille contient cette valeur en candidat
//              Ajouter l'indice de cette case dans le Set Integer correspondant
// Créer un Set Integer union qui fait l'union de tous les couples possibles des Set Integer
// Créer un Set Integer mixedGroups
// Pour chaque Set Integer inclue dans l'union
//               Ajouter la valeur i dans mixedGroups
// Si le nombre de valeur de mixedGroups == taille de l'union
//             Supprimer les candidats autres que mixedGroups des cases qui contiennent des candidats inclus dans mixedGroups

public class MixedGroups implements IHeuristic {
	private final IGrid grille;

	public MixedGroups(IGrid grid) {

		grille = grid;
	}

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */

	public Answer compute() {
		String msg = null;
		// recherche de groupes mélangés dans les lignes
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			int[] t = mixedGroupsLine(grille, i);
			if (t != null) {
				StringBuilder tableau = new StringBuilder("["); // création d'un StringBuilder avec une ouverture de
																// crochet
				for (int q = 0; q < t.length; q++) {
					tableau.append(t[q]); // ajout de la valeur du tableau d'entiers dans le StringBuilder
					if (q < t.length - 1) {
						tableau.append(", "); // ajout d'une virgule et d'un espace entre les valeurs, sauf pour la
												// dernière valeur
					}
				}
				tableau.append("]");
				msg = " Sur la ligne " + (i + 1)
						+ ", si des cases contiennent des  candidats dont la valeur numérique est contenu dans"
						+ tableau + " ainsi que des " +
						" candidats supplémentaires alors tu peux supprimer ces candidats supplémentaires car ils n'appartiennent pas au groupe mélangé"
						+ "\n";
				return createAnswer(0, i, t, msg);
			}
		}
		// recherche de groupes mélangés dans les colonnes
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			int[] t = mixedGroupsCol(grille, i);
			if (t != null) {
				StringBuilder tableau = new StringBuilder("["); // création d'un StringBuilder avec une ouverture de
																// crochet
				for (int q = 0; q < t.length; q++) {
					tableau.append(t[q]); // ajout de la valeur du tableau d'entiers dans le StringBuilder
					if (q < t.length - 1) {
						tableau.append(", "); // ajout d'une virgule et d'un espace entre les valeurs, sauf pour la
												// dernière valeur
					}
				}
				tableau.append("]");
				msg = " Sur la colonne " + (i + 1)
						+ ", si des cases contiennent des candidats dont la valeur numérique est contenu dans" + tableau
						+ " avec des " +
						" candidats supplémentaires alors tu peux supprimer ces candidats supplémentaires" + "\n";
				return createAnswer(1, i, t, msg);
			}
		}
		// recherche de groupes mélangés dans les régions
		for (IRegion r : grille.getSize().getTypeRegion()) {
			int[] t = mixedGroupsRegion(grille, r);
			if (t != null) {
				StringBuilder tableau = new StringBuilder("["); // création d'un StringBuilder avec une ouverture de
																// crochet
				for (int q = 0; q < t.length; q++) {
					tableau.append(t[q]); // ajout de la valeur du tableau d'entiers dans le StringBuilder
					if (q < t.length - 1) {
						tableau.append(", "); // ajout d'une virgule et d'un espace entre les valeurs, sauf pour la
												// dernière valeur
					}
				}
				tableau.append("]");

				msg = " Sur la région de la case (" + r.getStartX() + 1 + "," + r.getStartY() + 1
						+ "), si des cases contiennent des candidats dont la valeur numérique est contenu dans"
						+ tableau + " ainsi que des " +
						" candidats supplémentaires alors tu peux supprimer ces candidats supplémentaires car ils n'appartiennent pas au groupe mélangé"
						+ "\n";
				return createAnswer2(r.getStartX(), r.getStartY(), t, msg);
			}
		}
		return null;
	}

	private Answer createAnswer(int type, int x, int[] tab, String msg) {
		Answer a = new Answer(msg);
		final int size = this.grille.getSize().getSize();
		Action act = null;
		// conversion du tableau en set integer
		Set<Integer> setEntiers = new HashSet<Integer>();
		for (int i = 0; i < tab.length; i++) {
			setEntiers.add(tab[i]);
		}
		if (type == 0) {
			for (int j = 0; j < size; j++) {
				ICase ci = grille.getCase(x, j);
				if (!ci.getIsFixed()) {
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(setEntiers);
					if (!intersection.isEmpty()) {
						Set<Integer> si = ci.getCandidates();
						for (int xi : si) {
							if (!setEntiers.contains(xi)) {
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
				ICase ci = grille.getCase(j, x);
				if (!ci.getIsFixed()) {
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(setEntiers);
					if (!intersection.isEmpty()) {
						Set<Integer> si = ci.getCandidates();
						for (int xi : si) {
							if (!setEntiers.contains(xi)) {
								act = new Action(j, x, xi, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}
			}
		}
		return a;

	}

	private Answer createAnswer2(int x, int y, int[] tab, String msg) {
		Answer a = new Answer(msg);
		IRegion region = grille.searchRegion(x, y);
		Action act = null;
		// conversion du tableau en set integer
		Set<Integer> setEntiers = new HashSet<Integer>();
		for (int i = 0; i < tab.length; i++) {
			setEntiers.add(tab[i]);
		}

		for (int k = region.getStartX(); k <= region.getEndX(); k++) {
			for (int j = region.getStartY(); j <= region.getEndY(); j++) {
				ICase ci = grille.getCase(k, j);
				if (!ci.getIsFixed()) {
					Set<Integer> intersection = new HashSet<Integer>(ci.getCandidates());
					intersection.retainAll(setEntiers);
					if (!intersection.isEmpty()) {
						Set<Integer> si = ci.getCandidates();
						for (int xi : si) {
							if (!setEntiers.contains(xi)) {
								act = new Action(k, j, xi, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}

			}

		}
		return a;
	}

	private int[] mixedGroupsLine(IGrid grid, int x) {
		// création d'une liste de set integer
		List<Set<Integer>> liste = new ArrayList<Set<Integer>>();

		for (int i = 0; i <= grid.getSize().getSize(); i++) {
			liste.add(new HashSet<Integer>());
		}
		// chaque set integer correspond à une valeur de la grille qu'on remplit avec
		// les indices des cases où cette valeur se trouve
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			ICase ci = grille.getCase(x, i);
			if (!ci.getIsFixed()) {
				Set<Integer> si = ci.getCandidates();
				for (int xi : si) {
					if (xi >= 0 && xi < liste.size() && liste.get(xi) != null) {
						liste.get(xi).add(i + 1);
					}
				}
			}
		}
		for (int i = 1; i <= grille.getSize().getSize(); i++) {
			for (int o = 1; o <= grille.getSize().getSize(); o++) {
				// on fait une union de deux éléments de la liste
				Set<Integer> union = new HashSet<Integer>();
				if (liste.get(i) != null && liste.get(o) != null) {
					union.addAll(liste.get(i));
					union.addAll(liste.get(o));
				}
				// compteur initialisé à 0 qui va compter le nombre de valeurs qui ont une
				// inclusion avec l'union
				int count = 0;
				// si une valeur du groupe mélangée se trouve en dehors, un changement sera
				// nécessaire
				boolean changeNeeded = false;
				// set qui va regrouper les valeurs du groupe mélangée potentiel
				Set<Integer> mixedgroup = new HashSet<Integer>();
				for (int j = 1; j <= grille.getSize().getSize(); j++) {
					if (j >= 0 && j < liste.size() && !liste.get(j).isEmpty()) {
						boolean isIncluded = union.containsAll(liste.get(j));
						if (isIncluded == true) {
							count = count + 1;
							mixedgroup.add(j);
						}
					}
				}
				// s'il y a autant de candidats que de valeurs, on a un groupe mélangés
				if (union.size() == count) {
					// s'il y a un groupé mélangé, on cherche s'il y a dans leurs cases des
					// candidats supplémentaires à changer
					for (int u = 0; u < grille.getSize().getSize(); u++) {
						ICase ci2 = grille.getCase(x, u);
						if (!ci2.getIsFixed()) {
							Set<Integer> si2 = ci2.getCandidates();
							Set<Integer> intersection = new HashSet<Integer>(si2);
							Set<Integer> difference = new HashSet<Integer>(si2);
							intersection.retainAll(mixedgroup);
							difference.removeAll(mixedgroup);
							// si une case contient des valeurs du groupe mélangé et des valeurs
							// supplémentaire, un changement est nécessaire)
							if (!intersection.isEmpty() && !difference.isEmpty()) {
								changeNeeded = true;
							}
						}
						if (changeNeeded == true) {
							int k = 0;
							int[] result = new int[count];
							for (int a : mixedgroup) {
								if (a != 0) {
									result[k] = a;
									k++;
								}
							}
							return result;
						}

					}
				}
			}

		}
		return null;
	}

	private int[] mixedGroupsCol(IGrid grid, int y) {
		// création d'une liste de set integer
		List<Set<Integer>> liste = new ArrayList<Set<Integer>>();

		for (int i = 0; i <= grid.getSize().getSize(); i++) {
			liste.add(new HashSet<Integer>());
		}
		// chaque set integer correspond à une valeur de la grille qu'on remplit avec
		// les indices des cases où cette valeur se trouve
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			ICase ci = grille.getCase(i, y);
			if (!ci.getIsFixed()) {
				Set<Integer> si = ci.getCandidates();
				for (int xi : si) {
					liste.get(xi).add(i + 1);
				}
			}
		}
		for (int i = 1; i <= grille.getSize().getSize(); i++) {
			for (int o = 1; o <= grille.getSize().getSize(); o++) {
				// on fait une union de deux éléments de la liste
				Set<Integer> union = new HashSet<Integer>();
				if (liste.get(i) != null && liste.get(o) != null) {
					union.addAll(liste.get(i));
					union.addAll(liste.get(o));
				}
				// compteur initialisé à 0 qui va compter le nombre de valeurs qui ont une
				// inclusion avec l'union
				int count = 0;
				// si une valeur du groupe mélangée se trouve en dehors, un changement sera
				// nécessaire
				boolean changeNeeded = false;
				// set qui va regrouper les valeurs du groupe mélangée potentiel
				Set<Integer> mixedgroup = new HashSet<Integer>();
				for (int j = 1; j <= grille.getSize().getSize(); j++) {
					if (j >= 0 && j < liste.size() && !liste.get(j).isEmpty()) {
						boolean isIncluded = union.containsAll(liste.get(j));
						if (isIncluded == true) {
							count = count + 1;
							mixedgroup.add(j);
						}
					}
				}
				// s'il y a autant de candidats que de valeurs, on a un groupe mélangés
				if (union.size() == count) {
					// s'il y a un groupé mélangé, on cherche s'il y a dans leurs cases des
					// candidats supplémentaires à changer
					for (int u = 0; u < grille.getSize().getSize(); u++) {
						ICase ci2 = grille.getCase(u, y);
						if (!ci2.getIsFixed()) {
							Set<Integer> si2 = ci2.getCandidates();
							Set<Integer> intersection = new HashSet<Integer>(si2);
							Set<Integer> difference = new HashSet<Integer>(si2);
							intersection.retainAll(mixedgroup);
							difference.removeAll(mixedgroup);
							// si une case contient des valeurs du groupe mélangé et des valeurs
							// supplémentaire, un changement est nécessaire)
							if (!intersection.isEmpty() && !difference.isEmpty()) {
								changeNeeded = true;
							}
						}
						if (changeNeeded == true) {
							int k = 0;
							int[] result = new int[count];
							for (int a : mixedgroup) {
								if (a != 0) {
									result[k] = a;
									k++;
								}
							}
							return result;
						}

					}
				}
			}

		}
		return null;
	}

	private int[] mixedGroupsRegion(IGrid grid, IRegion region) {
		// création d'une liste de set integer
		List<Set<Integer>> liste = new ArrayList<Set<Integer>>();

		for (int i = 0; i <= grid.getSize().getSize(); i++) {
			liste.add(new HashSet<Integer>());
		}
		// chaque set integer correspond à une valeur de la grille qu'on remplit avec
		// les indices des cases où cette valeur se trouve
		for (int i = region.getStartX(); i <= region.getEndX(); i++) {
			for (int j = region.getStartY(); j <= region.getEndY(); j++) {
				ICase ci = grille.getCase(i, j);
				if (!ci.getIsFixed()) {
					Set<Integer> si = ci.getCandidates();
					for (int xi : si) {
						liste.get(xi).add(i + 1);
					}
				}
			}
		}
		for (int i = 1; i <= grille.getSize().getSize(); i++) {
			for (int o = 1; o <= grille.getSize().getSize(); o++) {
				// on fait une union de deux éléments de la liste
				Set<Integer> union = new HashSet<Integer>();
				if (liste.get(i) != null && liste.get(o) != null) {
					union.addAll(liste.get(i));
					union.addAll(liste.get(o));
				}
				// compteur initialisé à 0 qui va compter le nombre de valeurs qui ont une
				// inclusion avec l'union
				int count = 0;
				// si une valeur du groupe mélangée se trouve en dehors, un changement sera
				// nécessaire
				boolean changeNeeded = false;
				// set qui va regrouper les valeurs du groupe mélangée potentiel
				Set<Integer> mixedgroup = new HashSet<Integer>();
				for (int j = 1; j <= grille.getSize().getSize(); j++) {
					if (j >= 0 && j < liste.size() && !liste.get(j).isEmpty()) {
						boolean isIncluded = union.containsAll(liste.get(j));
						if (isIncluded == true) {
							count = count + 1;
							mixedgroup.add(j);
						}
					}
				}
				// s'il y a autant de candidats que de valeurs, on a un groupe mélangé
				if (union.size() == count) {
					// s'il y a un groupé mélangé, on cherche s'il y a dans leurs cases des
					// candidats supplémentaires à changer
					for (int k = region.getStartX(); k <= region.getEndX(); k++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci2 = grille.getCase(k, j);
							if (!ci2.getIsFixed()) {
								Set<Integer> si2 = ci2.getCandidates();
								Set<Integer> intersection = new HashSet<Integer>(si2);
								Set<Integer> difference = new HashSet<Integer>(si2);
								intersection.retainAll(mixedgroup);
								difference.removeAll(mixedgroup);
								// si une case contient des valeurs du groupe mélangé et des valeurs
								// supplémentaire, un changement est nécessaire)
								if (!intersection.isEmpty() && !difference.isEmpty()) {
									changeNeeded = true;
								}
							}
							if (changeNeeded == true) {
								int ki = 0;
								int[] result = new int[count];
								for (int a : mixedgroup) {
									if (a != 0) {
										result[ki] = a;
										ki++;
									}
								}
								return result;
							}

						}
					}
				}
			}

		}
		return null;
	}
}
