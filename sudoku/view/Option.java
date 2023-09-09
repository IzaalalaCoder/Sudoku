package sudoku.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sudoku.util.Tools;

/**
 * La classe Option représente la fenêtre de dialogue ainsi que
 * les différentes actions que l'on peut faire avec les paramètres.
 * 
 * @author Khabouri Izana
 */
public class Option {
    
//CONSTANTES 
  private final String pathImg = new Tools().getAbsolute("../templates/assets/sudoku_logo.jpg");
  private final String f = new Tools().getAbsolute("../templates/sudokuappcache.xml");
  private final String TRUE = "true";
  private final String FALSE = "false";
  private final int ROWS =  Parameter.values().length;
  private final int COLUMNS =  0;
  private final int WIDTH = 300;
  private final int HEIGHT = 150;
  private final int DELAY = 1000;

  // ATTRIBUTS
  
  private boolean change = false;
  private JDialog window;
  private Map<String, Boolean> options;
  private Map<String, JCheckBox> boxOptions;
  private JButton valid;
  
  // CONSTRUCTEUR 
  
  public Option() {
      this.readXMLFile();
      this.createView();
      this.placeComponents();
      this.createController();
  }

  // REQUETES
  
  /**
   * Retourne les différentes options associé à leur valeurs qui 
   * vaut soit vrai soit faux.
   */
  public Map<String, Boolean> getOption() {
      return this.options;
  }
  
  /**
   * Retourne vrai si les valeurs ont été modifiés et faux sinon.
   * Remet à faux après lecture.
   */
  public boolean change() {
      boolean changeOption = this.change;
      if (change == true) {
          this.change = false;
      }
      return changeOption;
  }
  
  // COMMANDES
  
  /**
   * Affiche la fenêtre.
   */
  public void display() {
      this.window.setSize(WIDTH, HEIGHT);
      this.window.setLocationRelativeTo(null);
      this.window.setVisible(true);
  }
  
  // OUTILS
  
  /**
   * Créer la vue.
   */
  private void createView() {
      this.window = new JDialog((JFrame) null, "Options", true);
      ImageIcon img = new ImageIcon(pathImg.toString());
      this.window.setIconImage(img.getImage());
      this.valid = new JButton("Valider");
  }
  
  /**
   * Place les composants.
   */
  private void placeComponents() {
      JPanel p = new JPanel(new GridLayout(2, 0));
      { // --
        JPanel q = new JPanel(new BorderLayout());
        { // --
            JPanel r = new JPanel(new GridLayout(ROWS, COLUMNS));
            
            { // --
                for (Parameter opt : Parameter.values()) {
                JLabel lbl = new JLabel(opt.getExplained());
                r.add(lbl);
                r.add(this.boxOptions.get(opt.getNode()));
            }
            }
          q.add(r, BorderLayout.WEST);
          r = new JPanel(new GridLayout(ROWS, COLUMNS));
          { // --
              for (Parameter opt : Parameter.values()) {
                r.add(this.boxOptions.get(opt.getNode()));
            }
          }
          
          q.add(r, BorderLayout.EAST);
        }
        p.add(q);
        q = new JPanel();
        { // --
            q.add(this.valid);
        }
        p.add(q);
      } 
      this.window.add(p);
  }
  
  /**
   * Crée le controleur.
   */
  private void createController() {
      this.window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      this.valid.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
              checkIfChange();
              try {
                  Thread.sleep(DELAY);
                  window.dispose();
              } catch (InterruptedException e1) {}
          }
          
      });
  }
  
  /**
   * Modifie la valeur de change si il y a eu changement.
   */
  private void checkIfChange() {
      for (String str : this.options.keySet()) {
          if (!options.get(str).equals(boxOptions.get(str).isSelected())) {
              options.replace(str, boxOptions.get(str).isSelected());
              change = true;
          }
      }
  }
  
  private File getXMLFile() {
      File file = new File(f);
      return file;
  }
  
  /**
   * Sauvegarde l'état courant de nos options.
   * Ecrase le fichier.
   */
  public void writeXMLFile() {
      File file = this.getXMLFile();
      file.delete();
      try {
           
           DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
           DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
           
           Document doc = docBuilder.newDocument();
           Element racine = doc.createElement("option");
           doc.appendChild(racine);
           
           for (Parameter opt : Parameter.values()) {
               Element element = doc.createElement(opt.getNode());
               racine.appendChild(element);
               
               Attr attr = doc.createAttribute(opt.getAttribute());
               attr.setValue(options.get(opt.getNode()) ? TRUE : FALSE);
               element.setAttributeNode(attr);
           }
           
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           transformer.setOutputProperty(OutputKeys.INDENT, "yes");
           DOMSource source = new DOMSource(doc);
           StreamResult resultat = new StreamResult(file);
           
           transformer.transform(source, resultat);
      } 
      catch (ParserConfigurationException pce) {} 
      catch (TransformerException tfe) {}
  }
  
  /**
   * Lit le fichier XML cache qui contient les informations sur 
   * nos différents paramètres.
   */
  private void readXMLFile() {
      this.options = new HashMap<String, Boolean>();
      this.boxOptions = new HashMap<String, JCheckBox>();
      File file = this.getXMLFile();
      
       final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
   try {
       final DocumentBuilder builder = f.newDocumentBuilder();
       final Document document = builder.parse(file);
       final Element racine = document.getDocumentElement();
       final NodeList racineNoeuds = racine.getChildNodes();

       final int nbRacineNoeuds = racineNoeuds.getLength();
       for (int i = 0; i < nbRacineNoeuds; i++) {
         if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
             final Element elem = (Element) racineNoeuds.item(i);
             Parameter opt = getParameter(elem.getTagName());
             this.options.put(opt.getNode(), 
                     toBoolean(elem.getAttribute(opt.getAttribute())));
             boxOptions.put(opt.getNode(), new JCheckBox());
             boxOptions.get(opt.getNode()).setSelected(toBoolean(elem.getAttribute(opt.getAttribute())));
         }
     }

   } catch (ParserConfigurationException e) {}
     catch (SAXException e) {}
        catch (IOException e) {}
  }
  
  /**
   * Retourne le paramètre associé au noeud.
   */
  private Parameter getParameter(String node) {
      for (Parameter opt : Parameter.values()) {
          if (node.equals(opt.getNode())) {
              return opt;
          }
      }
      return null;
  }
  
  /**
   * Retourne un booléen en fonction de si la chaîne de caractère vaut 
   * true ou false.
   */
  private boolean toBoolean(String str) {
      return str.equals(this.TRUE);
  }
  
  
  // TYPES IMBRIQUEES
  
  /**
   * Liste des paramètres qui sont modifiables depuis notre fenêtre 
   * des options.
   */
  private enum Parameter {
      
      // CONSTANTES 
      
      AUTOCOMPLETE("Auto-complétion de la grille", "autocomplete", "state"),
      DISPLAY("Mode clair", "display", "light"),
      USE_CANDIDATES("Forcer l'utilisation des candidats", "forcedusecandidate", "forced"),
      CHRONOMETER("Utiliser un chronomètre", "chronometer", "active");
      
      // ATTRIBUTS
      
      private String explained;
      private String node;
      private String attributes;
      
      // CONSTRUCTEUR 
      
      private Parameter(String str, String n, String a) {
          this.explained = str;
          this.node = n;
          this.attributes = a;
      }
      
      // REQUETES
      
      public String getExplained() {
          return this.explained;
      }
      
      public String getNode() {
          return this.node;
      }
      
      public String getAttribute() {
          return this.attributes;
      }
      
  }
}
