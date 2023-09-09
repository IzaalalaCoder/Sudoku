package sudoku.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import sudoku.model.heuristic.Answer;
import sudoku.model.heuristic.BruteStrength;
import sudoku.model.heuristic.ForcedChainCandidate;
import sudoku.model.heuristic.IHeuristic;
import sudoku.model.heuristic.IdenticalCandidates;
import sudoku.model.heuristic.InteractionRegion;
import sudoku.model.heuristic.IsolatedGroups;
import sudoku.model.heuristic.Jellyfish;
import sudoku.model.heuristic.MixedGroups;
import sudoku.model.heuristic.Nishio;
import sudoku.model.heuristic.OnlyCandidate;
import sudoku.model.heuristic.SingleCandidate;
import sudoku.model.heuristic.Squirmbag;
import sudoku.model.heuristic.SwordFish;
import sudoku.model.heuristic.TwinsTriplets;
import sudoku.model.heuristic.Unicity;
import sudoku.model.heuristic.XWing;
import sudoku.model.heuristic.XYChain;
import sudoku.model.heuristic.XYColored;
import sudoku.model.heuristic.XYWing;
import sudoku.model.heuristic.XYZWing;

import sudoku.model.info.Command;
import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.model.info.Type;

import sudoku.model.regions.IRegion;

import sudoku.util.IStack;
import sudoku.util.Stack;
import sudoku.util.Tools;
import sudoku.view.MessageUser;
import sudoku.view.Sudoku;

/**
 * @author Izana Khabouri, Tanguy Cavelier et Yohann Ducroq
 */
public class Grid implements IGrid {

	// CONSTANTES

	private final int EMPTY = 0;

	// ATTRIBUTS

	private int[][] grid;

	private boolean autocomplete;
	private boolean finished;
	private boolean forcedUseCandidate;
	private ICase[][] game;
	private Type data;

	private IStack<Action> actions;
	private final Size size;
	private final Level level;
	private final PropertyChangeSupport pcs;
	private final VetoableChangeSupport vcs;

	// CONTRUCTEUR

	public Grid(int[][] t, boolean ac, boolean f,
			Size size, Level lvl, Type type) {
		this.vcs = new VetoableChangeSupport(this);
		this.pcs = new PropertyChangeSupport(this);
		this.finished = false;
		this.grid = t.clone();
		this.size = size;
		this.level = lvl;
		this.data = type;
		this.forcedUseCandidate = f;
		this.autocomplete = ac;
		this.game = intToCell(t);
		this.autoCompleteGrid();
		this.actions = new Stack<Action>();
	}

	public Grid(ICase[][] grille, boolean ac, boolean f,
			Type data, Size size, Level level) {
		this.vcs = new VetoableChangeSupport(this);
		this.pcs = new PropertyChangeSupport(this);
		this.finished = false;
		this.size = size;
		this.grid = caseToInt(grille);
		this.level = level;
		this.data = data;
		this.forcedUseCandidate = f;
		this.autocomplete = ac;
		this.game = grille;
		if (ac) {
			this.fullGrid();
			this.autoCompleteGrid();
		}
		this.actions = new Stack<Action>();
	}

	// REQUETES

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public boolean getFinished() {
		return this.finished;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public boolean checkGame() {

		// Ensemble des valeurs

		Set<Integer> numbers = new HashSet<Integer>();

		// parcours des REGIONS

		for (IRegion region : size.getTypeRegion()) {
			for (int x = region.getStartX(); x <= region.getEndX(); x++) {
				for (int y = region.getStartY(); y <= region.getEndY(); y++) {
					int value = this.game[x][y].getValue();
					if (numbers.contains(value)) {
						return false;
					}
					numbers.add(value);
				}
			}
			for (int i = 1; i <= size.getSize(); i++) {
				if (!numbers.contains(i)) {
					return false;
				}
			}
			numbers.clear();
		}

		// parcours des LIGNES

		for (int x = 0; x < size.getSize(); x++) {
			for (int y = 0; y < size.getSize(); y++) {
				int value = this.game[x][y].getValue();
				if (numbers.contains(value)) {
					return false;
				}
				numbers.add(value);
			}
			for (int i = 1; i <= size.getSize(); i++) {
				if (!numbers.contains(i)) {
					return false;
				}
			}
			numbers.clear();
		}

		numbers.clear();

		// parcours des COLONNES

		for (int x = 0; x < size.getSize(); x++) {
			for (int y = 0; y < size.getSize(); y++) {
				int value = this.game[y][x].getValue();
				if (numbers.contains(value)) {
					return false;
				}
				numbers.add(value);
			}
			for (int i = 1; i <= size.getSize(); i++) {
				if (!numbers.contains(i)) {
					return false;
				}
			}
			numbers.clear();
		}

		return true;
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public boolean getCanAutoComplete() {
		return this.autocomplete;
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public boolean getForcedUseCandidate() {
		return this.forcedUseCandidate;
	}

	@Override
	/**
	 * @author Cavelier Tanguy
	 */
	public ICase getCase(int x, int y) {
		return game[x][y];
	}

	@Override
	/**
	 * @author Cavelier Tanguy
	 */
	public ICase[][] getGrid() {
		return this.game.clone();
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public int[][] getGridOfInteger() {
		return this.caseToInt(game);
	}

	@Override
	/**
	 * @author Cavelier Tanguy
	 */
	public ICase[] getLine(int x) {
		ICase[] line = new ICase[size.getSize()];
		for (int k = 0; k < size.getSize(); k++) {
			line[k] = game[x][k];
		}
		return line;
	}

	@Override
	/**
	 * @author Cavelier Tanguy
	 */
	public ICase[] getColumn(int y) {
		ICase[] colonne = new ICase[size.getSize()];
		for (int k = 0; k < size.getSize(); k++) {
			colonne[k] = game[k][y];
		}
		return colonne;
	}

	    @Override
    /**
     * @author Cavelier Tanguy
     */
    public boolean isInSameRegion(int line1, int column1, int line2, int column2) {
    	int regionSizex = size.getNumberRegionLines();
    	int regionSizey = size.getNumberRegionCols();
    	int region1X = line1 / regionSizey;
        int region1Y = column1 / regionSizex;
        int region2X = line2 / regionSizey;
        int region2Y = column2 / regionSizex;
        return (region1X == region2X && region1Y == region2Y);
    }

    
    @Override
    /**
     * @author Cavelier Tanguy
     */
    public ICase[] getRegion(int x, int y) {
        int k = 0;
        ICase[] region = new ICase[size.getSize()];
        for(int i = 0; i < size.getSize();i++) {
        	for(int j = 0; j < size.getSize(); j++) {
        		if(isInSameRegion(x, y, i, j)) {
        			region[k] = game[i][j];
                    k++;
        		}
        	}
        }
        return region;
    }

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public IRegion searchRegion(int x, int y) {
		for (IRegion r : this.size.getTypeRegion()) {
			if (r.getStartX() <= x && x <= r.getEndX()) {
				if (r.getStartY() <= y && y <= r.getEndY()) {
					return r;
				}
			}
		}
		return null;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public IGrid getCopy() {
		Size s = this.getSize();
		Level lvl = this.getLevel();
		Type t = this.getType();
		int[][] tab = caseToInt(this.getGrid());
		IGrid g = new Grid(tab, true, true, s, lvl, t);
		return g;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public IStack<Action> getAllAction() {
		return this.actions;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public Size getSize() {
		return this.size;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public Level getLevel() {
		return this.level;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public Type getType() {
		return this.data;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public PropertyChangeListener[] getPropertyChangeListeners(String pName) {
		if (pName == null) {
			throw new AssertionError();
		}
		return pcs.getPropertyChangeListeners(pName);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public VetoableChangeListener[] getVetoableChangeListeners(String pName) {
		if (pName == null) {
			throw new AssertionError();
		}
		return this.vcs.getVetoableChangeListeners(pName);
	}

	// COMMANDES

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void help() {
		Answer response = null;
		for (Heuristics h : Heuristics.values()) {
			IHeuristic heuristic = h.getHeuristic(this);
			response = heuristic.compute();
			if (response != null) {
				response.setAuthor(h.name());
				break;
			}
		}
		this.pcs.firePropertyChange(PROP_HELP, null, response);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void setType(Type type) {
		assert type != null;
		Type oldType = this.data;
		this.data = type;
		this.pcs.firePropertyChange(PROP_DATA, oldType, this.data);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void setFinished() throws PropertyVetoException {
		this.vcs.fireVetoableChange(PROP_FINISHED, finished, !finished);
		this.finished = true;
		this.pcs.firePropertyChange(PROP_FINISHED, false, this.finished);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void setCanAutoComplete(boolean b) {
		boolean oldAutocomplete = this.autocomplete;
		this.autocomplete = b;
		this.pcs.firePropertyChange(PROP_AUTOCOMPLETE, oldAutocomplete, autocomplete);
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public void setForcedUseCandidate(boolean b) {
		this.forcedUseCandidate = b;
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public void setValue(int i, int j, int number, boolean historicUse) throws PropertyVetoException {
		assert i >= 0 && j >= 0 && i < this.size.getSize() && j < this.size.getSize();
		assert number >= 1 && number <= this.size.getSize();
		boolean f = this.forcedUseCandidate;
		Action newAction = new Action(i, j, number, Command.SET_VALUE, f);
		vcs.fireVetoableChange(PROP_GAME, null, newAction);
		this.game[i][j].setValue(number);
		if (historicUse) {
			this.actions.push(Command.SET_VALUE.getAction(i, j, number));
		}
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Izana Khabouri
	 */
	public void unsetValue(int i, int j, int number, boolean historicUse) throws PropertyVetoException {
		assert i >= 0 && j >= 0 && i < this.size.getSize() && j < this.size.getSize();
		assert number >= 1 && number <= this.size.getSize();
		boolean f = this.forcedUseCandidate;
		Action newAction = new Action(i, j, number, Command.UNSET_VALUE, f);
		vcs.fireVetoableChange(PROP_GAME, null, newAction);
		this.game[i][j].unsetValue();
		if (historicUse) {
			this.actions.push(Command.UNSET_VALUE.getAction(i, j, number));
		}
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void autoCompleteGrid() {
		if (autocomplete) {
			// PARCOURS LIGNE

			for (int i = 0; i < this.size.getSize(); i++) {
				this.autoRemoveCandidatesObvious(this.game[i]);
			}

			ICase[] cases = new ICase[this.size.getSize()];

			// PARCOURS COLONNE

			for (int j = 0; j < this.size.getSize(); j++) {
				for (int i = 0; i < this.size.getSize(); i++) {
					cases[i] = this.game[i][j];
				}
				this.autoRemoveCandidatesObvious(cases);
			}

			// PARCOURS REGION

			for (IRegion r : this.size.getTypeRegion()) {
				int index = 0;
				for (int i = r.getStartX(); i <= r.getEndX(); i++) {
					for (int j = r.getStartY(); j <= r.getEndY(); j++) {
						cases[index] = this.game[i][j];
						index++;
					}
				}
				this.autoRemoveCandidatesObvious(cases);
			}
		}
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void addCandidate(int i, int j, int number, boolean historicUse) throws PropertyVetoException {
		assert i >= 0 && j >= 0 && i < this.size.getSize() && j < this.size.getSize();
		assert number >= 1 && number <= this.size.getSize();
		boolean f = this.forcedUseCandidate;
		Action newAction = new Action(i, j, number, Command.ADD_CANDIDATES, f);
		vcs.fireVetoableChange(PROP_GAME, null, newAction);
		this.game[i][j].addCandidates(number);
		if (historicUse) {
			this.actions.push(Command.ADD_CANDIDATES.getAction(i, j, number));
		}
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void remCandidate(int i, int j, int number, boolean historicUse) throws PropertyVetoException {
		assert i >= 0 && j >= 0 && i < this.size.getSize() && j < this.size.getSize();
		assert number >= 1 && number <= this.size.getSize();
		boolean f = this.forcedUseCandidate;
		Action newAction = new Action(i, j, number, Command.REM_CANDIDATES, f);
		vcs.fireVetoableChange(PROP_GAME, null, newAction);
		this.game[i][j].remCandidates(number);
		if (historicUse) {
			this.actions.push(Command.REM_CANDIDATES.getAction(i, j, number));
		}
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void addingCandidates(int v, int x, int y) {
		assert x >= 0 && y >= 0 && x < this.size.getSize() && y < this.size.getSize();
		assert v >= 1 && v <= this.size.getSize();

		if (autocomplete) {

			// PARCOURS LIGNE

			for (int j = 0; j < this.size.getSize(); j++) {
				if (!this.game[x][j].getIsFixed()) {
					if (!this.game[x][j].getCandidates().contains(v)) {
						this.game[x][j].addCandidates(v);
					}
				}
			}

			// PARCOURS COLONNE

			for (int i = 0; i < this.size.getSize(); i++) {
				if (!this.game[i][y].getIsFixed()) {
					if (!this.game[i][y].getCandidates().contains(v)) {
						this.game[i][y].addCandidates(v);
					}
				}
			}

			// PARCOURS REGIONS

			IRegion region = this.searchRegion(x, y);

			for (int i = region.getStartX(); i <= region.getEndX(); i++) {
				for (int j = region.getStartY(); j <= region.getEndY(); j++) {
					if (!this.game[i][j].getIsFixed()) {
						if (!this.game[i][j].getCandidates().contains(v)) {
							this.game[i][j].addCandidates(v);
						}
					}
				}
			}
		}
		this.autoCompleteGrid();
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void removingCandidates(int v, int x, int y) {
		assert x >= 0 && y >= 0 && x < this.size.getSize() && y < this.size.getSize();
		assert v >= 1 && v <= this.size.getSize();

		if (autocomplete) {
			// PARCOURS LIGNE

			for (int j = 0; j < this.size.getSize(); j++) {
				if (!this.game[x][j].getIsFixed()) {
					if (this.game[x][j].getCandidates().contains(v)) {
						this.game[x][j].remCandidates(v);
					}
				}
			}

			// PARCOURS COLONNE

			for (int i = 0; i < this.size.getSize(); i++) {
				if (!this.game[i][y].getIsFixed()) {
					if (this.game[i][y].getCandidates().contains(v)) {
						this.game[i][y].remCandidates(v);
					}
				}
			}

			// PARCOURS REGIONS

			IRegion region = this.searchRegion(x, y);

			for (int i = region.getStartX(); i <= region.getEndX(); i++) {
				for (int j = region.getStartY(); j <= region.getEndY(); j++) {
					if (!this.game[i][j].getIsFixed()) {
						if (this.game[i][j].getCandidates().contains(v)) {
							this.game[i][j].remCandidates(v);
						}
					}
				}
			}
		}

		this.autoCompleteGrid();
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Ducroq Yohann
	 */
	// TODO YOHANN
	public void loadGrid() {
		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Ducroq Yohann
	 */
	public Element saveGrid() {
		ICase[][] oldGame = this.game.clone();
		Element gridElt = new Element("Grid");
		gridElt.setAttribute("data", data.name());
		gridElt.setAttribute("size", size.name());
		gridElt.setAttribute("level", level.name());

		int rowInd = 1;
		for (ICase[] casesRow : oldGame) {
			int columnInd = 1;
			Element rowElt = new Element("R" + rowInd++);
			for (ICase iCase : casesRow) {
				rowElt.addContent(iCase.save(String.format("C%d-%d", rowInd, columnInd++)));
			}
			gridElt.addContent(rowElt);
		}

		return gridElt;
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void clear() {
		for (int i = 0; i < this.size.getSize(); i++) {
			for (int j = 0; j < this.size.getSize(); j++) {
				if (!this.game[i][j].getIsFixedByGrid()) {
					if (this.game[i][j].getIsFixedByUser()) {
						this.game[i][j].unsetValue();
					}
					this.game[i][j].removeAllCandidates();
					if (this.autocomplete) {
						this.fullCandidates(this.game[i][j]);
					}
				}
			}
		}

		this.autoCompleteGrid();

		if (this.getFinished()) {
			this.finished = false;
		}
		this.actions.clear();

		this.pcs.firePropertyChange(PROP_GAME, null, this.game);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void fullGrid() {
		for (int i = 0; i < game.length; i++) {
			for (int j = 0; j < game.length; j++) {
				ICase c = this.getCase(i, j);
				if (!c.getIsFixed()) {
					this.fullCandidates(c);
				}
			}
		}
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void addPropertyChangeListener(String pName, PropertyChangeListener pcl) {
		if (pName == null) {
			throw new AssertionError();
		}
		this.pcs.addPropertyChangeListener(pName, pcl);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void removePropertyChangeListener(String pName, PropertyChangeListener pcl) {
		if (pName == null) {
			throw new AssertionError();
		}
		this.pcs.removePropertyChangeListener(pName, pcl);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void addVetoableChangeListener(String pName, VetoableChangeListener lnr) {
		if (pName == null) {
			throw new AssertionError();
		}
		this.vcs.addVetoableChangeListener(pName, lnr);
	}

	@Override
	/**
	 * @author Khabouri Izana
	 */
	public void removeVetoableChangeListener(String pName, VetoableChangeListener lnr) {
		if (pName == null) {
			throw new AssertionError();
		}
		this.vcs.removeVetoableChangeListener(pName, lnr);
	}

	// OUTILS

	/**
	 * Convertit une grille de case vers une grille d'entiers.
	 * 
	 * @author Tanguy Cavelier
	 */
	private int[][] caseToInt(ICase[][] cases) {
		int size = this.getSize().getSize();
		int[][] t = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = cases[i][j];
				t[i][j] = c.getIsFixed() ? cases[i][j].getValue() : EMPTY;
			}
		}
		return t;
	}

	/**
	 * Fonction qui transforme une matrice d'entier en un sudoku,les 0 qui
	 * sont dans la matrice correspondent à des cases vides et sont donc des
	 * listes de candidats potentiels pour remplir la case
	 * 
	 * @author Tanguy Cavelier
	 */
	private ICase[][] intToCell(int[][] t) {
		ICase[][] sudoku = new ICase[this.size.getSize()][this.size.getSize()];
		for (int i = 0; i < this.size.getSize(); i++) {
			for (int j = 0; j < this.size.getSize(); j++) {
				int a = t[i][j];
				if (a == 0) {
					ICase c = new Case(null, this.size.getSize());
					/*
					 * for (Integer x : this.get_Candidat(i, j)) {
					 * c.addCandidates(x);
					 * }
					 */
					if (this.autocomplete) {
						this.fullCandidates(c);
					}
					sudoku[i][j] = c;
				} else {
					sudoku[i][j] = new Case(a, this.size.getSize());
				}
			}
		}
		return sudoku;
	}

	/**
	 * @author Khabouri Izana
	 */
	private void fullCandidates(ICase c) {
		for (int i = 1; i <= this.size.getSize(); i++) {
			if (!c.getCandidates().contains(i)) {
				c.addCandidates(i);
			}
		}
	}

	/**
	 * @author Khabouri Izana
	 */
	private void autoRemoveCandidatesObvious(ICase[] cases) {
		ICase x = cases[0];
		Set<Integer> numbers = new HashSet<Integer>();
		for (int k = 0; k < cases.length; k++) {
			x = cases[k];
			if (x.getIsFixed()) {
				numbers.add(x.getValue());
			}
		}
		for (int k = 0; k < cases.length; k++) {
			x = cases[k];
			if (!x.getIsFixed()) {
				x.removeCandidateInSet(numbers, this.size.getSize());
			}
		}
	}

	/**
	 * Fonction qui prend le numéro d'une ligne x et renvoie une Set<Integer>
	 * de tous les candidats en fonction de la ligne
	 * 
	 * @author Tanguy Cavelier
	 */
	private Set<Integer> get_c_line(int x) {
		int[] tab = new int[this.size.getSize()];
		for (int k = 0; k < this.size.getSize(); k++) {
			int c = this.grid[x][k];
			if (c != 0) {
				tab[k] = c;
			}
		}
		return createSet(tab);
	}

	/**
	 * Fonction qui prend le numéro d'une colonne y et renvoie une Set<Integer>
	 * de tous les candidats en fonction de la colonne
	 * 
	 * @author Tanguy Cavelier
	 */
	private Set<Integer> get_c_col(int y) {
		int[] tab = new int[this.size.getSize()];
		for (int k = 0; k < this.size.getSize(); k++) {
			int c = this.grid[k][y];
			if (c != 0) {
				tab[k] = c;
			}
		}
		return createSet(tab);
	}

	/**
	 * Fonction qui prend le numéro d'une ligne et d'une colonne et renvoie
	 * une Set<Integer> de tous les candidats en fonction de la region où
	 * se trouve la case
	 * 
	 * @author Tanguy Cavelier
	 */
	private Set<Integer> get_c_region(int x, int y) {
		int xRegion = x / 3;
		int yRegion = y / 3;
		int k = 0;
		int[] tab = new int[this.size.getSize()];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int c = this.grid[xRegion * 3 + i][yRegion * 3 + j];
				if (c != 0) {
					tab[k] = c;
				}
				k++;
			}
		}
		return createSet(tab);
	}

	/**
	 * Fonction qui renvoie la liste des candidats à une cellule du sudoku
	 * qui se situe à la position (x,y)
	 * 
	 * @author Tanguy Cavelier
	 */
	@SuppressWarnings("unused")
	private Set<Integer> get_Candidat(int x, int y) {
		Set<Integer> t1 = get_c_line(x);
		Set<Integer> t2 = get_c_col(y);
		Set<Integer> t3 = get_c_region(x, y);
		List<Set<Integer>> list = new ArrayList<>();
		list.add(t1);
		list.add(t2);
		list.add(t3);
		return intersect(list);
	}

	/**
	 * Fonction qui fait l'intersection de tous les Set<Integer> d'une
	 * liste de Set<Integer> donnée en paramètre
	 * 
	 * @author Tanguy Cavelier
	 */
	private Set<Integer> intersect(List<Set<Integer>> list) {
		if (list == null || list.isEmpty()) {
			return new HashSet<>();
		}
		Iterator<Set<Integer>> iterator = list.iterator();
		Set<Integer> intersection = new HashSet<>(iterator.next());
		while (iterator.hasNext()) {
			intersection.retainAll(iterator.next());
		}
		return intersection;
	}

	/**
	 * Fonction qui donne une liste de tous les éléments qui ne sont pas dans
	 * le tableau donné en paramètre et qui sont compris entre 1 et SIZE la
	 * taille du sudoku
	 * 
	 * @author Tanguy Cavelier
	 */
	private Set<Integer> createSet(int[] tab) {
		Set<Integer> result = new HashSet<>();
		for (int i = 1; i <= this.size.getSize(); i++) {
			if (!contains(tab, i)) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * Fonction qui renvoie si un entier i est dans le tableau d'entier tab
	 * 
	 * @author Tanguy Cavelier
	 */
	private boolean contains(int[] tab, int i) {
		for (int j : tab) {
			if (j == i) {
				return true;
			}
		}
		return false;
	}

	// TYPE IMBRIQUE

	private enum Heuristics {
		ONLY_CANDIDATES() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				OnlyCandidate h = new OnlyCandidate(m);
				return h;
			}
		},
		SINGLE_CANDIDATES() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				SingleCandidate h = new SingleCandidate(m);
				return h;
			}
		},
		TWINS_AND_TRIPLETS() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				TwinsTriplets h = new TwinsTriplets(m);
				return h;
			}
		},
		INTERACT() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				InteractionRegion h = new InteractionRegion(m);
				return h;
			}
		},
		IDENTICAL() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				IdenticalCandidates h = new IdenticalCandidates(m);
				return h;
			}
		},
		ISOLATED() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				IsolatedGroups h = new IsolatedGroups(m);
				return h;
			}
		},
		MIXED() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				MixedGroups h = new MixedGroups(m);
				return h;
			}
		},
		XWING() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				XWing h = new XWing(m);
				return h;
			}
		},
		XYWING() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				XYWing h = new XYWing(m);
				return h;
			}
		},
		XYZWING() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				XYZWing h = new XYZWing(m);
				return h;
			}
		},
		UNICITY() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				Unicity h = new Unicity(m);
				return h;
			}
		},
		SWORDFISH() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				SwordFish h = new SwordFish(m);
				return h;
			}
		},
		JELLYFISH() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				Jellyfish h = new Jellyfish(m);
				return h;
			}
		},
		SQUIRMBAG() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				Squirmbag h = new Squirmbag(m);
				return h;
			}
		},
		// BURMA
		// COLORING
		/*
		 * TURBOTFISH() {
		 * 
		 * @Override
		 * public IHeuristic getHeuristic(IGrid m) {
		 * TurbotFish h = new TurbotFish(m);
		 * return h;
		 * }
		 * },
		 */
		XYCHAIN() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				XYChain h = new XYChain(m);
				return h;
			}
		},
		XYCOLORED() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				XYColored h = new XYColored(m);
				return h;
			}
		},
		// MEDUSA
		FORCED() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				ForcedChainCandidate h = new ForcedChainCandidate(m);
				return h;
			}
		},
		NISHIO() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				Nishio h = new Nishio(m);
				return h;
			}
		},
		VARIOUS() {
			@Override
			public IHeuristic getHeuristic(IGrid m) {
				BruteStrength h = new BruteStrength(m);
				return h;
			}
		};

		// REQUETES

		public abstract IHeuristic getHeuristic(IGrid m);
	}

	/**
	 * Méthode qui charge une nouvelle grille courante.
	 */

	public static Grid loadGrid(Element gridElt, boolean ac, boolean f) {

		// boolean autocomplete =
		// Boolean.parseBoolean(gridElt.getAttribute("autocomplete").getValue());
		// boolean forcedUseCandidate =
		// Boolean.parseBoolean(gridElt.getAttribute("forcedUseCandidate").getValue());
		Type data = Type.valueOf(gridElt.getAttribute("data").getValue());
		Size size = Size.valueOf(gridElt.getAttribute("size").getValue());
		Level level = Level.valueOf(gridElt.getAttribute("level").getValue());

		ICase[][] grille = Case.loadCases(gridElt, size);

		return new Grid(grille, ac, f, data, size, level);
	}

	public static Grid loadGridTemplate(Type t, Size s, Level l) {
		Tools tool = new Tools();
		String path = tool.getAbsolute("../templates/levels/" + l.getPath());
		if (path == null) {
			return null;
		}
		File file = new File(path);
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sxb.build(file);
			if (doc.getRootElement().getChild(s.getRepertory()) != null) {
				List<Element> parties = doc.getRootElement().getChild(s.getRepertory()).getChildren();

				Element partie = parties.get(new Random().nextInt(parties.size()));

				return new Grid(Case.loadCases(partie, s),
						Sudoku.getOptionValues().get("autocomplete"),
						Sudoku.getOptionValues().get("forcedusecandidate"),
						t, s, l);
			} else {
				MessageUser.messageError("Partie " + s.getNomButton()
						+ " de niveau " + l.getLevel(), "Aucune parties n'est prédéfinies");
			}
		} catch (IOException e) {
			MessageUser.messageError("Erreur", "Erreur lecture des templates");
		} catch (JDOMException e) {
			MessageUser.messageError("Erreur", "Erreur lecture des templates");
		}
		return null;
	}
}

