package sudoku.model.heuristic;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import sudoku.util.Point;
import sudoku.util.Tools;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

/**
 * La classe Squirmbag implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Squirmbag.
 * 
 * @author Valentin Gangloff
 */
public class Squirmbag implements IHeuristic {

	// ATTRIBUTS

	private final IGrid model;
	private int checkedCandidate;

	public Squirmbag(IGrid m) {
		if (m == null) {
			throw new AssertionError("Squirmbag constr precond");
		}

		model = m;
	}

	@Override
	public Answer compute() {
		ICase[][] cells = model.getGrid();
		List<Integer> candLines = checkCandidates(cells);

		if (candLines == null) {
			return null;
		}

		String mes = message(candLines);
		Answer answer = new Answer(mes);

		List<Point> keep = toKeep(candLines);
		List<Point> remove = toRemove(candLines);

		for (Point p : keep) {
			answer.addRecommendedActions(new Action(p.getX(), p.getY(), this.checkedCandidate,
					Command.KEEP_CANDIDATE, false));
		}

		if (remove.size() != 0) {
			for (Point p : remove) {
				answer.addRecommendedActions(new Action(p.getX(), p.getY(), this.checkedCandidate,
						Command.REM_CANDIDATES, false));
			}
		}

		for (Integer j : candLines) {
			for (int i = 0; i < model.getSize().getSize(); ++i) {
				answer.addCasesOfProofs(j, i);
			}
		}
		return answer;
	}

	// OUTILS

	private List<Integer> checkCandidates(ICase[][] cells) {
		List<Integer> candCols;
		int positCells = 0;
		List<Integer> checkedLines;

		for (int n = 1; n <= model.getSize().getSize(); ++n) {
			checkedCandidate = n;
			candCols = new ArrayList<Integer>();
			for (int j = 0; j < model.getSize().getSize(); ++j) {
				positCells = 0;
				for (int i = 0; i < model.getSize().getSize(); ++i) {
					if (cells[i][j].getCandidates().contains(n)) {
						++positCells;
					}
				}
				if (positCells <= 4) {
					candCols.add(j);
				}
			}
			if (candCols.size() == 4) {
				checkedLines = new ArrayList<Integer>();
				for (Integer j : candCols) {
					for (int i = 0; i < model.getSize().getSize(); ++i) {
						if (checkedLines.contains(i)) {
							continue;
						}
						if (cells[i][j].getCandidates().contains(n) ||
								((cells[i][j].getIsFixedByGrid() || cells[i][j].getIsFixedByUser())
										&& cells[i][j].getValue() == n)) {
							checkedLines.add(i);
						}
					}
				}
				if (checkedLines.size() == 4) {
					return candCols;
				}
			}
			continue;
		}
		return null;
	}

	private String message(List<Integer> candLines) {
		List<Point> removeCoords = toRemove(candLines);
		String msg = "Avec la valeur " + Tools.getData(checkedCandidate, model.getType());

		msg = msg + " aux colonnes";

		for (int i = 0; i < candLines.size(); ++i) {
			msg = msg + candLines.get(i);
			if (i != candLines.size() - 1) {
				msg = msg + ", ";
			} else {
				msg = msg + " ";
			}
		}

		msg = msg
				+ "nous avons donc un Squirmbag";

		if (removeCoords.size() == 0) {
			msg = msg + " cependant, aucune autre occurence se trouve sur les colonnes externes.";
		} else {
			msg = msg + ", ce qui veut dire que les "
					+ Tools.getData(checkedCandidate, model.getType()) + " en positions ";

			for (Point p : removeCoords) {
				msg = msg + "(" + p.getX() + "," + p.getY() + ") ";
			}

			msg = msg + "ne peuvent être candidats.";
		}
		return msg;
	}

	private List<Point> toKeep(List<Integer> candLines) {
		ICase[][] cells = model.getGrid();

		List<Point> toKeepCoords = new ArrayList<Point>();

		for (int j : candLines) {
			for (int i = 0; i < model.getSize().getSize(); ++i) {

				if (cells[i][j].getCandidates().contains(checkedCandidate)) {
					toKeepCoords.add(new Point(j, i));
				}

			}
		}
		return toKeepCoords;
	}

	private List<Point> toRemove(List<Integer> candLines) {
		ICase[][] cells = model.getGrid();

		List<Point> toRemoveCoords = new ArrayList<Point>();

		boolean[] checked = new boolean[model.getSize().getSize()];
		Arrays.fill(checked, false);

		for (int j : candLines) {
			for (int i = 0; i < model.getSize().getSize(); ++i) {
				if (checked[i]) {
					continue;
				}

				if (cells[i][j].getCandidates().contains(this.checkedCandidate)) {
					checked[i] = true;
					for (int k = 0; k < model.getSize().getSize(); ++k) {
						if (!candLines.contains(k)
								&& cells[i][k].getCandidates().contains(this.checkedCandidate)) {
							toRemoveCoords.add(new Point(k, i));
						}
					}
				}

			}
		}
		return toRemoveCoords;
	}
}
