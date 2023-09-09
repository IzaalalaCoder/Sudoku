package sudoku.model.heuristic;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Point;
import sudoku.util.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * La classe Jellyfish implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Jellyfish.
 * 
 * @author Valentin Gangloff
 */
public class Jellyfish implements IHeuristic {

    // ATTRIBUTS

    private final IGrid model;
    private int checkedCandidate;
    List<Point> Keep;
    List<Point> Remove;

    /**
     * 
     * La classe Jellyfish implémente l'heuristique ainsi nommée Jellyfish.
     * Son unique méthode publique compute renverra un objet de type Answer
     * contenant
     * une suggestion de modifications sur la grille en adéquation avec
     * l'application
     * de l'heuristique.
     * 
     * @author Valentin GANGLOFF
     */

    // CONSTRUCTEUR

    public Jellyfish(IGrid m) {
        if (m == null) {
            throw new AssertionError("Jellyfish constr arg null");
        }

        model = m;
    }

    // REQUETES

    // REQUETES

    @Override
    public Answer compute() {
        ICase[][] cells = model.getGrid();
        Map<Integer, List<Point>> candCols = checkCandidates(cells);

        if (candCols == null) {
            return null;
        }

        purify(candCols);

        if (Remove.size() == 0) {
            return null;
        }
        String mes = message(candCols);
        Answer answer = new Answer(mes);

        for (Point p : Keep) {
            answer.addCasesOfProofs(p.getY(), p.getX());
        }

        if (Remove.size() != 0) {
            for (Point p : Remove) {
                answer.addRecommendedActions(new Action(p.getY(), p.getX(), this.checkedCandidate,
                        Command.REM_CANDIDATES, false));
            }
        }
        return answer;
    }

    // OUTILS

    /*
     * void purify : "Purifie" les listes de candidats pour
     * vérifier lesquels garder comme preuve et lesquels
     * afficher pour supression.
     */
    private void purify(Map<Integer, List<Point>> candCols) {
        Set<Integer> keySet = candCols.keySet();
        Integer[] keys = keySet.toArray(new Integer[keySet.size()]);
        if (keys.length > 4) {
            for (int i = 4; i < keys.length; ++i) {
                candCols.remove(keys[i]);
            }
        }
        keySet = candCols.keySet();
        keys = keySet.toArray(new Integer[keySet.size()]);
        ICase[][] cells = model.getGrid();
        Keep = new ArrayList<Point>();
        Remove = new ArrayList<Point>();
        boolean matched;
        for (int j : keys) {
            List<Point> col = candCols.get(j);
            for (int i = 0; i < model.getSize().getSize(); ++i) {
                if (cells[i][j].getCandidates().contains(checkedCandidate)) {
                    matched = false;
                    for (Point k : col) {
                        if (k.getY() == i) {
                            Keep.add(k);
                            matched = true;
                        }
                    }
                    if (!matched) {
                        Remove.add(new Point(j, i));
                    }
                }

            }

        }
    }

    /*
     * Map<Integer, List<Point>> checkCandidates : Vérifie quels candidats
     * sont présents en suffisamment d'occurences pour les traiter.
     */
    private Map<Integer, List<Point>> checkCandidates(ICase[][] cells) {
        Map<Integer, List<Point>> candidatesForCols;
        List<Point> foundCells;

        for (int n = 1; n <= model.getSize().getSize(); ++n) {
            checkedCandidate = n;
            candidatesForCols = new HashMap<Integer, List<Point>>();
            for (int j = 0; j < model.getSize().getSize(); ++j) {
                foundCells = new ArrayList<Point>();
                for (int i = 0; i < model.getSize().getSize(); ++i) {
                    if (cells[i][j].getCandidates().contains(n)) {
                        foundCells.add(new Point(j, i));
                    }
                }
                if (foundCells.size() >= 2 && foundCells.size() <= 4) {
                    candidatesForCols.put(j, foundCells);
                }
            }

            if (candidatesForCols.keySet().size() >= 4) {
                Set<Integer> keySet = candidatesForCols.keySet();
                Integer[] keys = keySet.toArray(new Integer[keySet.size()]);
                int[] checked = new int[model.getSize().getSize()];
                Arrays.fill(checked, 0);
                for (int i = 0; i < keys.length; ++i) {
                    List<Point> toCheck = candidatesForCols.get(keys[i]);
                    for (Point k : toCheck) {
                        ++checked[k.getY()];
                    }
                }
                for (int i = 0; i < keys.length; ++i) {
                    List<Point> toCheck = candidatesForCols.get(keys[i]);
                    for (Point k : toCheck) {
                        if (checked[k.getY()] < 2) {
                            toCheck.remove(k);
                        }
                    }
                    if (toCheck.size() < 2) {
                        candidatesForCols.remove(keys[i]);
                    }
                }
                if (candidatesForCols.keySet().size() < 4) {
                    return null;
                }
                return candidatesForCols;
            }
        }

        return null;
    }

    /*
     * String message : Construit le message générateur d'answer.
     */
    private String message(Map<Integer, List<Point>> candLines) {
        String msg = "Avec la valeur " + Tools.getData(checkedCandidate, model.getType());

        msg = msg + " presente en multibles occurences aux colonnes ";
        for (int i : candLines.keySet()) {
            msg = msg + i + ", ";
        }

        msg = msg
                + "nous avons donc un JellyFish";

        msg = msg + ", ce qui veut dire que les "
                + Tools.getData(checkedCandidate, model.getType()) + " en positions ";
        return msg;
    }
}
