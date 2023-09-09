package sudoku.model;

import java.util.Set;

import org.jdom2.Element;

import sudoku.model.info.Size;

import java.util.HashSet;
import java.util.List;

/**
 * La classe Case implémente l'interface ICase.
 * 
 * @author Khabouri Izana
 */
public class Case implements ICase {

    // ATTRIBUTS

    private final int maxValue;
    private final boolean fixedByGrid;
    private boolean fixedByUser;
    private int value;
    private Set<Integer> candidates;

    // CONSTRUCTEURS

    public Case(Integer value, int max) {
        if (max < ICase.SIZE_MIN) {
            throw new AssertionError();
        }
        this.fixedByGrid = (value == null) ? false : true;
        this.fixedByUser = false;
        this.value = (value == null) ? ICase.CASE_EMPTY : value;
        this.maxValue = max;
        this.candidates = new HashSet<Integer>();
    }

    protected Case(Integer value, int max, boolean fixedByGrid,
            boolean fixedByUser, Set<Integer> candidates) {
        this.value = value;
        this.maxValue = max;
        this.fixedByGrid = fixedByGrid;
        this.fixedByUser = fixedByUser;
        this.candidates = candidates;
    }

    // REQUETES

    @Override
    public boolean isEquals(ICase o) {
        if (this.fixedByGrid != o.getIsFixedByGrid()) {
            return false;
        }

        if (this.fixedByUser != o.getIsFixedByUser()) {
            return false;
        }

        if (this.value != o.getValue()) {
            return false;
        }

        if (!this.candidates.equals(o.getCandidates())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean getIsFixedByGrid() {
        return this.fixedByGrid;
    }

    @Override
    public boolean getIsFixedByUser() {
        return this.fixedByUser;
    }

    @Override
    public boolean getIsFixed() {
        return this.fixedByGrid || this.fixedByUser;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Set<Integer> getCandidates() {
        Set<Integer> set = new HashSet<Integer>();
        for (Integer element : this.candidates) {
            int value = element.intValue();
            set.add(value);
        }
        return set;
    }

    // COMMANDES

    @Override
    public void removeCandidateInSet(Set<Integer> n, int size) {
        for (int i = 1; i <= size; i++) {
            boolean isInNumbers = n.contains(i);
            if (this.candidates.contains(i)) {
                if (isInNumbers) {
                    this.candidates.remove(i);
                }
            }
        }
    }

    @Override
    public void setValue(int x) {
        if (this.fixedByGrid) {
            throw new AssertionError("Impossible de modifier la case");
        }
        if (this.fixedByUser) {
            throw new AssertionError("Veuillez supprimer votre chiffre");
        }
        if (x < 1 || x > this.maxValue) {
            throw new AssertionError("Chiffre incorrect");
        }
        this.value = x;
        this.fixedByUser = true;
    }

    @Override
    public void unsetValue() {
        if (this.fixedByGrid) {
            throw new AssertionError("Impossible de modifier la case");
        }
        if (!(this.fixedByUser)) {
            throw new AssertionError("Rien à supprimer");
        }
        this.value = CASE_EMPTY;
        this.fixedByUser = false;
    }

    @Override
    public void addCandidates(int x) {
        if (this.fixedByGrid) {
            throw new AssertionError("Impossible de modifier la case");
        }
        if (x < 1 || x > this.maxValue) {
            throw new AssertionError("Chiffre incorrect");
        }
        if (this.candidates.contains(x)) {
            throw new AssertionError("Chiffre déjà présent");
        }
        this.candidates.add(x);
    }

    @Override
    public void remCandidates(int x) {
        if (this.fixedByGrid) {
            throw new AssertionError("Impossible de modifier la case");
        }
        if (x < 1 || x > this.maxValue) {
            throw new AssertionError("Chiffre incorrect");
        }
        if (!(this.candidates.contains(x))) {
            throw new AssertionError("Chiffre absent" + x);
        }
        this.candidates.remove(x);
    }

    @Override
    public void removeAllCandidates() {
        this.candidates.clear();
    }

    @Override
    public void clear() {
        fixedByUser = false;
        value = ICase.CASE_EMPTY;
        this.removeAllCandidates();
    }

    @Override
    public Element save(String nom) {
        Element elt = new Element(nom);

        elt.setAttribute("fixedByGrid", Boolean.toString(fixedByGrid));
        elt.setAttribute("fixedByUser", Boolean.toString(fixedByUser));
        elt.setAttribute("value", Integer.toString(value));
        StringBuilder candidatesAtt = new StringBuilder();

        for (int candid : candidates) {
            candidatesAtt.append(candid).append("_");
        }

        if (candidatesAtt.length() > 0) {
            candidatesAtt.deleteCharAt(candidatesAtt.length() - 1);
        }

        elt.setAttribute("candidates", candidatesAtt.toString());
        return elt;

    }

    static Case load(Element elt, Size s) {
        String[] candidStr = elt.getAttributeValue("candidates").split("_");
        HashSet<Integer> candidats = new HashSet<>();
        if (!candidStr[0].equals("")) {
            for (String num : candidStr) {
                candidats.add(Integer.valueOf(num));
            }
        }
        return new Case(Integer.valueOf(elt.getAttributeValue("value")),
                s.getSize(),
                Boolean.parseBoolean(elt.getAttributeValue("fixedByGrid")),
                Boolean.parseBoolean(elt.getAttributeValue("fixedByUser")),
                candidats);
    }

    public static ICase[][] loadCases(Element gridElt, Size size) {
        List<Element> rows = gridElt.getChildren();
        if (rows.size() != size.getSize()) {
            throw new RuntimeException("Le nombre de ligne du sudoku ne correspond pas au format donné");
        }
        ICase[][] grille = new ICase[rows.size()][rows.size()];
        int rowInd = 0;
        for (Element row : rows) {
            List<Element> cells = row.getChildren();
            if (cells.size() != size.getSize()) {
                throw new RuntimeException("Le nombre de cases du sudoku ne correspond pas au format donné");
            }

            int cellsInd = 0;
            for (Element cell : cells) {
                grille[rowInd][cellsInd++] = Case.load(cell, size);
            }
            rowInd++;
            // cases[row.getName().charAt(row.getName().length() - 1)] =
        }
        return grille;
    }

    // OUTILS

    /*
     * private void setCanValues(Set<Integer> values) {
     * this.canValues = new HashSet<Integer>();
     * for (Integer e : values) {
     * canValues.add(e);
     * }
     * }
     */
}
