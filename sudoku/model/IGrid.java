package sudoku.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import org.jdom2.Element;

import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.model.info.Type;

import sudoku.model.regions.IRegion;

import sudoku.util.IStack;

/**
 * L'interface énumère les méthodes de gestion d'une grille de sudoku.
 * 
 * @author Khabouri Izana et Cavelier Tanguy
 */
public interface IGrid {

  // CONSTANTES

  public static final String PROP_GAME = "game";
  public static final String PROP_AUTOCOMPLETE = "autocomplete";
  public static final String PROP_FINISHED = "finished";
  public static final String PROP_DATA = "data";
  public static final String PROP_HELP = "help";

  // REQUETES

  /**
   * Méthode retournant si en effet la partie est finie.
   */
  public boolean getFinished();

  /**
   * Fonction qui renvoie un booleen en fonction de si la partie a été gagnée ou
   * non.
   */
  public boolean checkGame();

  /**
   * Retourne si la grille autorise la complétion automatique.
   */
  public boolean getCanAutoComplete();

  /**
   * Retourne si l'utilisation des candidats est forcé.
   */
  public boolean getForcedUseCandidate();

  /**
   * Fonction qui renvoie la case qui se trouve au coordonnées [x,y] dans la
   * grille de sudoku
   */
  public ICase getCase(int x, int y);

  /**
   * Fonction qui renvoie l'objet courant de type Grid qui représente une grille
   * de sudoku
   */
  public ICase[][] getGrid();

  /**
   * Convertit une grille de case vers une grille d'entiers.
   */
  public int[][] getGridOfInteger();

  /**
   * Fonction qui renvoie la ligne numéro n de la grille de sudoku
   */
  public ICase[] getLine(int x);

  /**
   * Fonction qui renvoie la colonne numéro n de la grille de sudoku
   */
  public ICase[] getColumn(int y);

  /**
   * Fonction qui prend les coordonnées de 2 cases du sudoku et qui renvoie
   * si les 2 cases se trouvent dans la même région
   */
  public boolean isInSameRegion(int line1, int colonne1, int line2, int colonne2);

  /**
   * Fonction qui renvoie la région dans laquelle la case(x,y) se situe dans la
   * grille de sudoku
   */
  public ICase[] getRegion(int x, int y);

  /**
   * Fonction qui renvoie la région dans laquelle la case(x,y) se situe dans la
   * grille de sudoku.
   * La région est cette fois-ci de type énuméré.
   */
  public IRegion searchRegion(int x, int y);

  /**
   * Renvoie une copie de la grille.
   */
  public IGrid getCopy();

  /**
   * Retourne la pile des actions réalisés sur la grille.
   */
  public IStack<Action> getAllAction();

  /**
   * Retourne la taille de la grille.
   */
  public Size getSize();

  /*
   * Méthode renvoyant le niveau de difficulté de la grille.
   */
  public Level getLevel();

  /**
   * Retourne le type des données de la grille.
   */
  public Type getType();

  /**
   * Retourne les écouteurs d'évènement de type VetoableChangeListener
   * concernant la propriété pName.
   */
  public VetoableChangeListener[] getVetoableChangeListeners(String pName);

  /**
   * Retourne les écouteurs d'évènement de type PropertyChangeListener
   * concernant la propriété pName.
   */
  public PropertyChangeListener[] getPropertyChangeListeners(String pName);

  // COMMANDES

  /**
   * Méthode demandant l'aide aux heuristiques.
   */
  public void help();

  /*
   * Méthode modifiant le niveau de difficulté de la grille de sudoku.
   */
  public void setType(Type type);

  /**
   * Méthode qui rend la partie finie.
   */
  public void setFinished() throws PropertyVetoException;

  /**
   * Méthode qui modifie l'état de l'autocomplétion de la grille.
   */
  public void setCanAutoComplete(boolean b);

  /**
   * Méthode qui modifie l'état de l'utilisation des candidats.
   */
  public void setForcedUseCandidate(boolean b);

  /**
   * Place le numéro number dans la case (i, j).
   */

  public void setValue(int i, int j, int number, boolean historicUse) throws PropertyVetoException;

  /**
   * Supprime le numéro placé à la case (i, j).
   */
  public void unsetValue(int i, int j, int number, boolean historicUse) throws PropertyVetoException;

  /**
   * Rempli la grille selon les cases déjà fixé que cela soit
   * par la grille ou par l'utilisateur.
   */
  public void autoCompleteGrid();

  /**
   * Méthode qui ajoute le candidat number de la case (i, j).
   * 
   */
  public void addCandidate(int i, int j, int number, boolean historicUse) throws PropertyVetoException;

  /**
   * Méthode qui supprime le candidat number de la case (i, j).
   * 
   */
  public void remCandidate(int i, int j, int number, boolean historicUse) throws PropertyVetoException;

  /**
   * Méthode qui ajoute le candidat v de la région, de la ligne et de la colonne.
   * 
   */
  public void addingCandidates(int v, int x, int y);

  /**
   * Méthode qui supprime le candidat v de la région, de la ligne et de la
   * colonne.
   * 
   */
  public void removingCandidates(int v, int x, int y);

  /**
   * Méthode qui charge une grille selon le niveau de difficulté.
   */
  public void loadGrid();

  /**
   * Méthode qui sauvegarde la grille courante.
   */
  public Element saveGrid();

  /**
   * Méthode qui remet à zéro toute la grille.
   */
  public void clear();

  /**
   * Rempli la grille entièrement.
   */
  public void fullGrid();

  /**
   * Ajoute un écoute d'évènement pcl de type PropertyChangeListener
   * pour la propriété du nom pName.
   */
  public void addPropertyChangeListener(String pName, PropertyChangeListener pcl);

  /**
   * Retire un écoute d'évènement pcl de type PropertyChangeListener
   * pour la propriété du nom pName.
   */
  public void removePropertyChangeListener(String pName, PropertyChangeListener pcl);

  /**
   * Ajoute un écoute d'évènement vcl de type VetoableChangeListener
   * pour la propriété du nom pName.
   */
  public void addVetoableChangeListener(String pName, VetoableChangeListener lnr);

  /**
   * Retire un écoute d'évènement vcl de type VetoableChangeListener
   * pour la propriété du nom pName.
   */
  public void removeVetoableChangeListener(String pName, VetoableChangeListener lnr);
}
