package sudoku.view.controller.menu;

import java.awt.BorderLayout;

import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.adobe.acrobat.Viewer;

import sudoku.util.Tools;
import sudoku.view.MessageUser;
/*
 * www.codeurjava.com
 */
public class NoticeAction extends JPanel{

	private static final long serialVersionUID = 1L;
	private Viewer viewer;
 
	public NoticeAction(String nomfichier) throws Exception {
   this.setLayout(new BorderLayout()); 
   viewer = new Viewer();
   this.add(viewer, BorderLayout.CENTER);
   FileInputStream fis = new FileInputStream(nomfichier);
   viewer.setDocumentInputStream(fis);
   viewer.activate();
 }
 
 public static void display() {
 
 String nomfichier = "rapport_de_projet.pdf";
 String pathFile = new Tools().getAbsolute("../templates/" + nomfichier);
 NoticeAction lecteur = null;
  try {
  	lecteur = new NoticeAction(pathFile);
    JFrame f = new JFrame("Lecteur PDF");
    f.setSize(1024,768);
    f.setLocationRelativeTo(null);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
    f.getContentPane().add(lecteur);
  } catch (Exception e) {
  	MessageUser.messageError("Erreur", e.getMessage());
  }
   
 }
}