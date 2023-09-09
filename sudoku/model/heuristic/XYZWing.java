package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.List;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Point;
import sudoku.util.Tools;

/**
 * La classe XYZWing implémente l'interface IHeuristic.
 * Elle représente l'heuristique : XYZ-Wing.
 * @author Valentin Gangloff
 */ 
public class XYZWing implements IHeuristic {

	// ATTRIBUTS

	private final IGrid model;
	private final ICase[][] grid;
	private final int gridSize;
	private Point pivot;
	private Point hPincer;
	private Point vPincer;
	private boolean targeted = false;
	private int banned;
	private Integer[] pivotCands;
	private List<Point> vertical;
	private List<Point> horizontal;
	private List<Point> remove;
		
	// CONSTRUCTEUR

	public XYZWing(IGrid m) {
		if (m == null) {
			throw new AssertionError("XYZ-Wing constr precond");
		}

		model = m;
		grid = model.getGrid();
		gridSize = model.getSize().getSize();
	}

	// REQUETES

	@Override
	public Answer compute() {
		checkCandidates();
		if (!targeted) {
			return null;
		}

		purify();

		String mes = message();
		Answer answer = new Answer(mes);

		answer.addCasesOfProofs(pivot.getY(), pivot.getX());
		answer.addCasesOfProofs(hPincer.getY(), hPincer.getX());
		answer.addCasesOfProofs(vPincer.getY(), vPincer.getX());
		

		for (Point k : remove) {
			answer.addRecommendedActions(new Action(k.getX(), k.getY(), 
					banned, Command.REM_CANDIDATES, false));
		}
		
		return answer;
	}

	// OUTILS

	private void checkCandidates() {
		for (int i = 0; i < gridSize; ++i) {
			for (int j = 0; j < gridSize; ++j) {
				if (grid[i][j].getCandidates().size() == 3) {
					pivot = new Point(j, i);
					pivotCands = grid[i][j].getCandidates().toArray(new Integer[3]);
					vertical = checkVertical();
					horizontal = checkHorizontal();

					if (vertical != null && horizontal != null) {
						for (Point k : horizontal) {
							for (Point k2 : vertical) {
								int matchs = 0;
								for (int n : pivotCands) {
									if (grid[k.getY()][k.getX()].getCandidates().contains(n) && grid[k2.getY()][k2.getX()].getCandidates().contains(n)) {
										++matchs;
										banned = n;
									}
									
								}
								if (matchs == 1) {
									hPincer = k;
									vPincer = k2;
									targeted = true;
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private List<Point> checkVertical() {
		List<Point> pincers = new ArrayList<Point>();
		for (int i = 0; i < gridSize; ++i) {
			ICase potentPincer = grid[i][pivot.getX()];
			if (potentPincer.getCandidates().size() == 2) {
				if (i != pivot.getY()) {
					int matchs = 0;
					for (int n : pivotCands) {
						if (potentPincer.getCandidates().contains(n)) {
							++matchs;
						}
					}
					if (matchs == 2) {
						pincers.add(new Point(pivot.getX(), i));
					}
					
				}
			}
		}
		if (pincers.size() == 0) {
			return null;
		}
		return pincers;
	}

	private List<Point> checkHorizontal() {
		List<Point> pincers = new ArrayList<Point>();
		for (int j = 0; j < gridSize; ++j) {
			ICase potentPincer = grid[pivot.getY()][j];
			if (potentPincer.getCandidates().size() == 2) {
				if (j != pivot.getX()) {
					int matchs = 0;
					for (int n : pivotCands) {
						if (potentPincer.getCandidates().contains(n)) {
							++matchs;
						}
					}
					if (matchs == 2) {
						pincers.add(new Point(j, pivot.getY()));
					}
				}
			}
		}
		if (pincers.size() == 0) {
			return null;
		}
		return pincers;
	}
	
	private String message() {
		String msg = "Avec les valeurs " + Tools.getData(pivotCands[0], model.getType()) + "," + Tools.getData(pivotCands[1], model.getType()) + " et " + Tools.getData(pivotCands[2], model.getType()) + " de la case (" + pivot.getX() + "," + pivot.getY() +")";

		msg = msg + ", communes aux cases (" + hPincer.getX() + "," + hPincer.getY() + ") et (" + vPincer.getX() + "," + vPincer.getY() + "), dont " + Tools.getData(banned, model.getType()) + " commune aux trois.";

		msg = msg + ", nous avons donc un XYZ-Wing. On doit donc retirer le candidat commun " + Tools.getData(banned, model.getType()) + " des cases séparant le pivot de ses pinces.";
		
		return msg;
	}

	private void purify() {
		remove = new ArrayList<Point>();
		int start;
		int end;
		if (pivot.getX() < hPincer.getX()) {
			start = hPincer.getX();
			end =pivot.getX();
		} else {
			start = pivot.getX();
			end = hPincer.getX();
		}

		for (int j = start + 1; j < end; ++j) {
			if (grid[pivot.getY()][j].getCandidates().contains(banned)) {
				remove.add(new Point(j, pivot.getY()));
			}
		}

		if (pivot.getY() < vPincer.getY()) {
			start = hPincer.getY();
			end =pivot.getY();
		} else {
			start = pivot.getY();
			end = hPincer.getY();
		}

		for (int i = start + 1; i < end; ++i) {
			if (grid[i][pivot.getX()].getCandidates().contains(banned)) {
				remove.add(new Point(pivot.getX(), i));
			}
		}
	}
}
