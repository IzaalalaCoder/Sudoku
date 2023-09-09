package sudoku.model.heuristic;

import sudoku.model.Action;
import sudoku.model.Grid;
import sudoku.model.ICase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.model.info.Type;

import sudoku.model.regions.IRegion;


public class FCCV2 implements IHeuristic {
	
	// ATTRIBUTS STATIQUES 
	
	private final int EMPTY = 0;
	
	// ATTRIBUTS 
	
	private IGrid grid;
	private List<Point> allCoordinate;
	private Point coordinate;
	private int value;
	private final int size;
	
	// CONSTRUCTEUR
	
	public FCCV2(IGrid grid) {
		assert grid != null;
		this.grid = grid;
		this.allCoordinate = new ArrayList<Point>();
		this.value = EMPTY;
		this.coordinate = null;
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
		return (this.coordinate == null) ? null : toAnswer();
	}
	
	// OUTILS
	
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
	
	private boolean resolve(Point p) {
		Set<Integer> draft = grid.getGrid()[p.getX()][p.getY()].getCandidates();
		Object[] values = (Object[]) draft.toArray();
		
		IGrid g1 = copyGrid((int) values[0], p);
		this.work(g1, p);
		IGrid g2 = copyGrid((int) values[1], p);
		this.work(g2, p);

		return this.askFoundResult(g1, g2, p);
	}

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
	
	private void actInLine(IGrid g, int i) {
		final int size = g.getSize().getSize();
		for (int j = 0; j < size; j++) {
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == 1) {
					int v = (int) g.getGrid()[i][j].getCandidates().toArray()[0];
					this.act(g, v, new Point(i, j));
				}
			}
		}
	}
	
	private void actInColumn(IGrid g, int j) {
		final int size = g.getSize().getSize();
			for (int i = 0; i < size; i++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == 1) {
					int v = (int) g.getGrid()[i][j].getCandidates().toArray()[0];
					this.act(g, v, new Point(i, j));
				}
			}
		}
	}
  
	private void actInRegion(IGrid g, Point p) {
		IRegion r = g.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
					if (g.getGrid()[i][j].getCandidates().size() == 1) {
						int v = (int) g.getGrid()[i][j].getCandidates().toArray()[0];
						this.act(g, v, new Point(i, j));
					}
				}
			}
		}
	}
	
	private boolean canWorkInLines(IGrid g, int i) {
		final int size = g.getSize().getSize();
		for (int j = 0; j < size; j++) { 
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == 1) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean canWorkInColumns(IGrid g, int j) {
		final int size = g.getSize().getSize();
		for (int i = 0; i < size; i++) { 
			if (!g.getGrid()[i][j].getIsFixed()) {
				if (g.getGrid()[i][j].getCandidates().size() == 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean canWorkInRegion(IGrid g, Point p) {
		IRegion r = g.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				if (!g.getGrid()[i][j].getIsFixed()) {
					if (g.getGrid()[i][j].getCandidates().size() == 1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private IGrid copyGrid(Integer i, Point p) {
		boolean ac = grid.getCanAutoComplete();
		Size s = grid.getSize();
		Level lvl = grid.getLevel();
		Type t = grid.getType();
		int[][] tab = caseToInt(grid.getGrid());
		IGrid g = new Grid(tab, ac, false, s, lvl, t);
		this.act(g, i, p);
		return g;
	}
	
	private void act(IGrid g, Integer i, Point p) {
		g.getGrid()[p.getX()][p.getY()].setValue(i);
		g.removingCandidates(i, p.getX(), p.getY());
	}
	
	private int[][] caseToInt(ICase[][] cases) {
		int size = grid.getSize().getSize();
		int[][] t = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = cases[i][j];
				t[i][j] = c.getIsFixed() ? cases[i][j].getValue() : EMPTY; 
			}
		}	
		return t;
	}
	
	private boolean askFoundResult(IGrid g1, IGrid g2, Point p) {
		// Lecture sur la ligne
		
		final int size = grid.getSize().getSize();
		for (int j = 0; j < size; j++) {
			ICase c1 = g1.getGrid()[p.getX()][j];
			ICase c2 = g2.getGrid()[p.getX()][j];
			ICase c = grid.getGrid()[p.getX()][j];
			if (!c.getIsFixed()) {
				if (c1.isEquals(c2) && !c1.equals(c)) {
					if (c1.getIsFixedByUser()) {
						this.coordinate = new Point(p.getX(), j);
						this.value = c1.getValue();
						return true;
					}
				}
			}
		}
		
		// Lecture sur la colonne
		
		for (int i = 0; i < size; i++) {
			ICase c1 = g1.getGrid()[i][p.getY()];
			ICase c2 = g2.getGrid()[i][p.getY()];
			ICase c = grid.getGrid()[i][p.getY()];
			if (!c.getIsFixed()) {
				if (c1.isEquals(c2) && !c1.equals(c)) {
					if (c1.getIsFixedByUser()) {
						this.coordinate = new Point(i, p.getY());
						this.value = c1.getValue();
						return true;
					}
				}
			}
		}
		
		// Lecture sur la région
		
		IRegion r = grid.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getEndY(); j <= r.getEndY(); j++) {
				ICase c1 = g1.getGrid()[i][j];
				ICase c2 = g2.getGrid()[i][j];
				ICase c = grid.getGrid()[i][j];
				if (!c.getIsFixed()) {
					if (c1.isEquals(c2) && !c1.equals(c)) {
						if (c1.getIsFixedByUser()) {
							this.coordinate = new Point(i, j);
							this.value = c1.getValue();
							return true;
						}
					}
				}
			}
		}
		
		// Fin d'analyse et rien n'a été vain	
		return false;
	}
	
	private Answer toAnswer() {
		int x =  coordinate.getX();
		int y =  coordinate.getY();
		String msg =  "A la position (" + x + "," + y 
				+ ") avec la valeur " + this.value + "."; 
		Answer a = new Answer(msg);
		Action act = new Action(x, y, this.value, Command.SET_VALUE, false);
		a.addRecommendedActions(act);
		return a;
	}
	
	// TYPE IMBRIQUEE
	
	private class Point {
		
		// ATTRIBUTS
		
		private int x;
		private int y;
		
		// CONSTRUCTEURS
		
		public Point(int a, int b) {
			this.x = a;
			this.y = b;
		}
		
		// REQUETES 
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
	}
}
