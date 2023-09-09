package sudoku.model.heuristic;

import java.util.Set;
import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.model.regions.IRegion;
import sudoku.util.Tools;

//Pseudo-code:
//Pour chaque ligne et colonne,
//Pour chaque valeur possible de la grille
//       Si cette valeur n'est pas fixé dans cette ligne/colonne 
//                Si cette  valeur apparait sur une seule région au sein de la ligne/colonne
//                               Supprimer ces valeurs dans les candidats candidats dans cette région où elle apparait sauf sur la ligne/colonne

public class InteractionRegion implements IHeuristic {
	private final IGrid grille;

	public InteractionRegion(IGrid grid) {

		grille = grid;
	}

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */

	public Answer compute() {
		String msg = null;
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			int[] t = interactionRegionLine(grille, i);

			if (t != null) {
				msg = " Tu peux retirer tout les " + Tools.getData(t[0], grille.getType()) + " de la " + t[1]
						+ "ème région de la ligne " + (i + 1) + " sauf ceux de cette ligne."
						+ "Cela est dû au fait que cette valeur n'est pas candidate dans  cette ligne dans les deux autres régions, alors "
						+ " elle se trouve forcément dans cette même ligne dans cette dernière région " + "\n";
				return createAnswer(0, t[0], t[1], i, msg);
			}
		}
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			int[] t = interactionRegionCol(grille, i);

			if (t != null) {
				msg = " Tu peux retirer tout les " + Tools.getData(t[0], grille.getType()) + " de la " + t[1]
						+ "ème région de la colonne " + (i + 1) + " sauf ceux de cette colonne."
						+ "Cela est dû au fait que cette valeur n'est pas candidate dans  cette colonne  dans les deux autres régions, alors "
						+ " elle se trouve forcément dans cette même colonne dans cette dernière région " + "\n";
				return createAnswer(1, t[0], t[1], i, msg);

			}
		}

		return null;
	}

	private Answer createAnswer(int type, int val, int numberRegion, int lineorcol, String msg) {
		Answer a = new Answer(msg);
		Action act = null;
		if (type == 0) {
			IRegion region = grille.searchRegion(lineorcol, 0);
			if (numberRegion == 2) {
				region = grille.searchRegion(lineorcol, 3);
			}
			if (numberRegion == 3) {
				region = grille.searchRegion(lineorcol, 6);
			}
			for (int j = region.getStartX(); j <= region.getEndX(); j++) {
				for (int k = region.getStartY(); k <= region.getEndY(); k++) {
					ICase ci = grille.getCase(j, k);
					if (j != lineorcol) {
						if (!ci.getIsFixed()) {
							if (ci.getCandidates().contains(val)) {
								act = new Action(j, k, val, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}
			}
		}

		if (type == 1) {
			IRegion region = grille.searchRegion(0, lineorcol);
			if (numberRegion == 2) {
				region = grille.searchRegion(3, lineorcol);
			}
			if (numberRegion == 3) {
				region = grille.searchRegion(6, lineorcol);
			}
			for (int j = region.getStartX(); j <= region.getEndX(); j++) {
				for (int k = region.getStartY(); k <= region.getEndY(); k++) {
					ICase ci = grille.getCase(j, k);
					if (k != lineorcol) {
						if (!ci.getIsFixed()) {
							if (ci.getCandidates().contains(val)) {
								act = new Action(j, k, val, Command.REM_CANDIDATES, false);
								a.addRecommendedActions(act);
							}
						}
					}
				}
			}
		}

		return a;
	}

	private int[] interactionRegionLine(IGrid grid, int x) {
		// pour chaque nombre, on teste
		for (int number = 1; number <= grille.getSize().getSize(); number++) {
			// on suppose que les 3 régions ne contiennent pas ce nombre sur la ligne x
			boolean r1 = true;
			boolean r2 = true;
			boolean r3 = true;
			// on suppose que les 3 régions ne contiennent pas ce nombre en dehors cette
			// ligne
			boolean inr1 = true;
			boolean inr2 = true;
			boolean inr3 = true;

			// parcours des 3 cases de région 1 pour cette ligne
			for (int region1 = 0; region1 < Math.sqrt(grid.getSize().getSize()); region1++) {
				ICase c = grille.getCase(x, region1);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r1 = false;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r1 = false;
					}
				}
				if (r1 == false) {
					IRegion region = grid.searchRegion(x, region1);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (j != region1) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr1 = false;
									}
								}

							}
						}
					}
				}
			}

			// parcours des 3 cases de région 2
			for (int region2 = (int) Math.sqrt(grid.getSize().getSize()); region2 < Math.sqrt(grid.getSize().getSize())
					* 2; region2++) {
				ICase c = grille.getCase(x, region2);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r2 = false;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r2 = false;
					}
				}
				if (r2 == false) {
					IRegion region = grid.searchRegion(x, region2);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (j != region2) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr2 = false;
									}
								}

							}
						}
					}
				}
			}

			// parcours des 3 cases de région 3
			for (int region3 = (int) Math.sqrt(grid.getSize().getSize()) * 2; region3 < Math
					.sqrt(grid.getSize().getSize()) * 3; region3++) {
				ICase c = grille.getCase(x, region3);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r3 = false;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r3 = false;
					}
				}
				if (r3 == false) {
					IRegion region = grid.searchRegion(x, region3);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (j != region3) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr3 = false;
									}
								}

							}
						}
					}
				}
			}
			if ((r1 == true && r2 == true) && r3 == false && inr3 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 3;
				return result;
			}
			if ((r1 == true && r2 == false) && r3 == true && inr2 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 2;
				return result;
			}
			if ((r1 == false && r2 == true) && r3 == true && inr1 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 1;
				return result;
			}
		}
		return null;
	}

	private int[] interactionRegionCol(IGrid grid, int x) {
		// pour chaque nombre, on teste
		for (int number = 1; number <= grid.getSize().getSize(); number++) {
			// on suppose que les 3 régions ne contiennent pas ce nombre au départ
			boolean r1 = true;
			boolean r2 = true;
			boolean r3 = true;
			// on suppose que les 3 régions ne contiennent pas ce nombre en dehors cette
			// colonne
			boolean inr1 = true;
			boolean inr2 = true;
			boolean inr3 = true;

			// parcours des 3 cases de région 1 de la colonne
			for (int region1 = 0; region1 < Math.sqrt(grid.getSize().getSize()); region1++) {
				ICase c = grille.getCase(region1, x);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r1 = true;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r1 = false;
					}
				}
				if (r1 == false) {
					IRegion region = grid.searchRegion(region1, x);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (i != region1) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr1 = false;
									}
								}

							}
						}
					}
				}
			}

			// parcours des 3 cases de région 2
			for (int region2 = (int) Math.sqrt(grid.getSize().getSize()); region2 < Math.sqrt(grid.getSize().getSize())
					* 2; region2++) {
				ICase c = grille.getCase(region2, x);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r2 = true;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r2 = false;
					}
				}
				if (r2 == false) {
					IRegion region = grid.searchRegion(region2, x);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (i != region2) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr2 = false;
									}
								}

							}
						}
					}
				}
			}

			// parcours des 3 cases de région 3
			for (int region3 = (int) Math.sqrt(grid.getSize().getSize()) * 2; region3 < Math
					.sqrt(grid.getSize().getSize()) * 3; region3++) {
				ICase c = grille.getCase(region3, x);
				if (c.getIsFixed()) {
					if (c.getValue() == number) {
						r3 = true;
					}
				}
				if (!c.getIsFixed()) {
					Set<Integer> s = c.getCandidates();
					if (s.contains(number)) {
						r3 = false;
					}
				}
				if (r3 == false) {
					IRegion region = grid.searchRegion(region3, x);

					for (int i = region.getStartX(); i <= region.getEndX(); i++) {
						for (int j = region.getStartY(); j <= region.getEndY(); j++) {
							ICase ci = grille.getCase(i, j);
							if (i != region3) {
								if (!c.getIsFixed()) {
									Set<Integer> si = ci.getCandidates();
									if (si.contains(number)) {
										inr3 = false;
									}
								}

							}
						}
					}
				}
			}
			if ((r1 == true && r2 == true) && r3 == false && inr3 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 3;
				return result;
			}
			if ((r1 == true && r2 == false) && r3 == true && inr2 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 2;
				return result;
			}
			if ((r1 == false && r2 == true) && r3 == true && inr1 == false) {
				int[] result = new int[2];
				result[0] = number;
				result[1] = 1;
				return result;
			}
		}

		return null;
	}
}
