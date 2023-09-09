package sudoku.model.info;

/**
 * Le type énuméré des niveaux disponible dans le jeu du sudoku
 * classique.
 * 
 * @author Khabouri Izana
 */
public enum Level {

  // CONSTANTES

  EASY("Facile", "easy.xml"),
  MEDIUM("Moyen", "medium.xml"),
  HARD("Difficile", "hard.xml"),
  EXTREM("Extreme", "extrem.xml"),
  UNKNOW("Inconnu", "");

  // ATTRIBUTS

  private String path;
  private String level;

  // CONSTRUCTEUR

  private Level(String level, String path) {
    this.path = path;
    this.level = level;
  }

  // REQUETES

  /**
   * Retourne le chemin d'accès au dossier du niveau.
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Retourne le niveau sous forme de chaîne de caractères.
   */
  public String getLevel() {
    return this.level;
  }
}
