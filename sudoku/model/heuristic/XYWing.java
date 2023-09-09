package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Point;
import sudoku.util.Tools;

/**
 * La classe XYWing implémente l'interface IHeuristic.
 * Elle représente l'heuristique : XY-Wing.
 * @author Valentin Gangloff
 */ 
public class XYWing implements IHeuristic {

	// ATTRIBUTS

	private final IGrid model;
	private final ICase[][] grid;
	private final int gridSize;
	private Point pivot;
	private Point hPincer;
	private Point vPincer;
	private Point target = null;
	private int banned;
	private Integer[] pivotCands;
	private List<Point> vertical;
	private List<Point> horizontal;
		
	// CONSTRUCTEUR

	public XYWing(IGrid m) {
		if (m == null) {
			throw new AssertionError("XY-Wing constr precond");
		}

		model = m;
		grid = model.getGrid();
		gridSize = model.getSize().getSize();
	}

	// REQUETES

	@Override
	public Answer compute() {
		checkCandidates();
		if (target == null) {
			return null;
		}

		String mes = message();
		Answer answer = new Answer(mes);

		answer.addCasesOfProofs(pivot.getY(), pivot.getX());
		answer.addCasesOfProofs(hPincer.getY(), hPincer.getX());
		answer.addCasesOfProofs(vPincer.getY(), vPincer.getX());
		
		answer.addRecommendedActions(new Action(target.getY(), target.getX(), 
				banned, Command.REM_CANDIDATES, false));
		return answer;
	}

	// OUTILS

	private void checkCandidates() {
		for (int i = 0; i < gridSize; ++i) {
			for (int j = 0; j < gridSize; ++j) {
				if (grid[i][j].getCandidates().size() == 2) {
					pivot = new Point(j, i);
					pivotCands = grid[i][j].getCandidates().toArray(new Integer[2]);
					vertical = checkVertical();
					horizontal = checkHorizontal();

					if (vertical != null && horizontal != null) {
						for (Point k : horizontal) {
							for (Point k2 : vertical) {
								if ( (grid[k.getY()][k.getX()].getCandidates().contains(pivotCands[0]) 
									&& grid[k2.getY()][k2.getX()].getCandidates().contains(pivotCands[1]))
								|| (grid[k.getY()][k.getX()].getCandidates().contains(pivotCands[1]) 
									&& grid[k2.getY()][k2.getX()].getCandidates().contains(pivotCands[0])) ){
									Set<Integer> targetSet = grid[k2.getY()][k.getX()].getCandidates();
									if(targetSet.size() == 0) {
										continue;
									}
									Integer[] targetCands = targetSet.toArray(new Integer[targetSet.size()]);

									for (int l : targetCands) {
										if (grid[k.getY()][k.getX()].getCandidates().contains(l)
										&& grid[k2.getY()][k2.getX()].getCandidates().contains(l)
										&& !grid[pivot.getY()][pivot.getX()].getCandidates().contains(l)) {
											hPincer = k;
											vPincer = k2;
											target = new Point(k.getX(), k2.getY());
											banned = l;
											return;
										}
									}

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
				if (i != pivot.getY() 
				&& ((potentPincer.getCandidates().contains(pivotCands[0]) && !potentPincer.getCandidates().contains(pivotCands[1]))
				||(!potentPincer.getCandidates().contains(pivotCands[0]) && potentPincer.getCandidates().contains(pivotCands[1])))) {
					pincers.add(new Point(pivot.getX(), i));
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
				if ( j != pivot.getX()
				&& (potentPincer.getCandidates().contains(pivotCands[0]) && !potentPincer.getCandidates().contains(pivotCands[1]))
				||(!potentPincer.getCandidates().contains(pivotCands[0]) && potentPincer.getCandidates().contains(pivotCands[1]))) {
					pincers.add(new Point(j, pivot.getY()));
				}
			}
		}
		if (pincers.size() == 0) {
			return null;
		}
		return pincers;
	}
	
	private String message() {
		String msg = "Avec les deux valeurs " + Tools.getData(pivotCands[0], model.getType()) + " et " + Tools.getData(pivotCands[1], model.getType()) + " de la case (" + pivot.getX() + "," + pivot.getY() +")";

		msg = msg + ", communes aux cases (" + hPincer.getX() + "," + hPincer.getY() + ") et (" + vPincer.getX() + "," + vPincer.getY() + ")";

		msg = msg + ", nous avons donc un XY-Wing. On doit donc retirer le candidat commun " + Tools.getData(banned, model.getType()) + " de la case (" + target.getX() + "," + target.getY() + ")";
		
		return msg;
	}
}
