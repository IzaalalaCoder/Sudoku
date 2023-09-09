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
 * La classe XYChain implémente l'interface IHeuristic.
 * Elle représente l'heuristique : XY-Wing.
 * @author Valentin Gangloff
 */ 
public class XYChain implements IHeuristic{

    private final IGrid model;
    private final ICase[][] grid;
    private final int gridSize;
    private final int step;
    private List<Point> chain;
    private int banned;
    private Point begin;
    private Point end;
    private List<Point> remove;

    public XYChain(IGrid m) {
        if (m == null) {
            throw new AssertionError("XY-Chain constr precond");
        }

        model = m;
        grid = model.getGrid();
        gridSize = model.getSize().getSize();
        step = (int) Math.sqrt(gridSize);

    }


    @Override
    public Answer compute() {
        searchFirstLink();

        if (end == null) {
            return null;
        }

        purify();

        
        String mes = message();
        Answer answer = new Answer(mes);

        for (Point p : chain) {
            answer.addCasesOfProofs(p.getY(), p.getX());
        }
        
        for (Point p : remove) {
            answer.addRecommendedActions(new Action(p.getY(), p.getX(), 
                banned, Command.REM_CANDIDATES, false));
        }

        return answer;
    }

    /*
     * void searchFirstLink : Recherche une case pouvant 
     * potentiellement démarrer une chaîne.
     */
    private void searchFirstLink() {
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (grid[i][j].getCandidates().size() == 2) {
                    Point potLink = new Point(j, i);
                    begin = null;
                    chain = null;
                    banned = 0;
                    buildChain(potLink);

                    if (end != null) {
                        return;
                    }
                }
            }
        }
    }

    /*
     * void buildChain : Fonction récursive.
     * Vérifie si le point est un maillon possible et, l'ajoute.
     */
    private void buildChain(Point p) {
        if (begin == null) {
            begin = p;
            chain = new ArrayList<Point>();
        }
        chain.add(p);
        
        Integer[] cands = grid[p.getY()][p.getX()].getCandidates().toArray(new Integer[2]);

        if (banned == cands[0] || banned == cands[1]) {
            chain.add(p);
            end = p;
            return;
        }

        Point next;

        if (chain.size() > 1) {
            int toCheck;
            Point prev = chain.get(chain.size()- 2);
            if (grid[prev.getY()][prev.getX()].getCandidates().contains(cands[0])) {
                toCheck = cands[1];
            } else {
                toCheck = cands[0];
            }
            next = searchNext(p, toCheck);
        } else {
            next = searchNext(p, cands[0]);
            banned = cands[1];
            if (next == null) {
                next = searchNext(p, cands[1]);
                banned = cands[0];
            }
        }

        if (next == null) {
            return;
        }

        buildChain(p);
    } 

    /*
     * Point searchNext : Recherche le maillon suivant de la chaîne.
     */
    private Point searchNext(Point p, int bond) {
        Point next = searchRegion(p, bond);
        if (next == null) {
            next = searchVertical(p, bond);
        }
        if (next == null) {
            searchHorizontal(p, bond);
        }

        return next;
    }

    /*
     * Point searchRegion : Cherche un maillon dans la région de p.
     */
    private Point searchRegion(Point p, int bond) {
        if(!model.getSize().getPerfectQuare()) {
            return null;
        }
        for (int i = p.getY() - (p.getY() % step) 
        ; i < p.getY() - (p.getY() % step) + step 
        ; ++i) {
            for (int j = p.getX() - (p.getX() % step) 
            ; j < p.getX() - (p.getX() % step) + step 
            ; ++j) {
                if (i != p.getY() && j != p.getX() && grid[i][j].getCandidates().size() == 2) {
                    ICase potChain = grid[i][j];
                    if (potChain.getCandidates().contains(bond)) {
                        Point link = new Point(j, i);
                        if (!isInChain(link)) {
                            chain.add(link);
                            if (potChain.getCandidates().contains(banned)) {
                                end = link;
                                return link;
                            }
                        }
                        link = null;
                    }
                }
            }
        }
        return null;
    }

    /*
     * Point searchVertical : Cherche un maillon dans la colonne de p.
     */
    private Point searchVertical(Point p, int bond) {
        for(int i = 0; i < gridSize; ++i) {
            if (i != p.getY() && grid[i][p.getX()].getCandidates().size() == 2) {
                ICase potChain = grid[i][p.getX()];
                if (potChain.getCandidates().contains(bond)) {
                    Point link = new Point(p.getX(), i);
                    if (!isInChain(link)) {
                        chain.add(link);
                        if (potChain.getCandidates().contains(banned)) {
                            end = link;
                            return link;
                        }
                    }
                    link = null;
                }
                
                
            }
        }
        return null;
    }

    /*
     * Point searchHorizontal : Cherche un maillon dans la ligne de p.
     */
    private Point searchHorizontal(Point p, int bond) {
        for(int j = 0; j < gridSize; ++j) {
            if (j != p.getX() && grid[p.getY()][j].getCandidates().size() == 2) {
                ICase potChain = grid[p.getY()][j];
                if (potChain.getCandidates().contains(bond)) {
                    Point link = new Point(j, p.getY());
                    if (!isInChain(link)) {
                        chain.add(link);
                        if (potChain.getCandidates().contains(banned)) {
                            end = link;
                            return link;
                        }
                    }
                    link = null;
                }
            }
        }
        return null;
    }
    

    /*
     * boolean isInChain : Vérifie si p est dans la chaîne.
     */
    private boolean isInChain(Point p) {
        for (Point k : chain) {
            if (k.getX() == p.getX() && k.getY() == p.getY()) {
                return true;
            }
        }
        return false;
    }

    /*
     * void purify : Recherche les candidats à éliminer.
     */
    private void purify() {
        remove = new ArrayList<Point>();

        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (grid[i][j].getCandidates().contains(banned) 
                && (isVisibleBy(begin, i, j) || isVisibleBy(end, i, j))) {
                    Point rem = new Point(j, i);
                    if (!isInChain(rem)){
                        remove.add(rem);
                    }
                }
            }
        }
    }

    /*
     * boolean isVisibleBy : Fonction outil de purify.
     * Il regarde si le point est visible par p et donc supprimable.
     */
    private boolean isVisibleBy (Point p, int i, int j) {
        return (i == p.getY() || j == p.getX());
    }

    /*
     * String message : Construit le message générateur de d'answer.
     */
    private String message() {
        String msg = "Il est possible de démarrer une chaîne avec " 
        + Tools.getData(banned, model.getType()) 
        + " en partant de la case (" + begin.getX() + "," + begin.getY() + ")"
        + " terminant à la case (" + end.getX() + "," + end.getY() + ")"
        + " ainsi, les fortes liaison font que les cases visibles par le debut "
        + "et la fin ne peuvent contenir " + Tools.getData(banned, model.getType());
        
        return msg;
    }
}