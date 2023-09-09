package sudoku.view.controller.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.border.TitledBorder;

import sudoku.util.Tools;

/**
 * La classe GameRuleAction implémente l'interface ActionListener.
 * Elle gère l'ouverture du fichier expliquant les règles du jeu.
 * 
 * @author Khabouri Izana
 */
public class GameRuleAction {

	// CONSTANTES

	private final Font font = new Font("Serif", Font.PLAIN, 15);
	private final String pathGrid = new Tools().getAbsolute("../temp"
			+ "lates/assets/grid.jpg");

	private final String[] textExplained = new String[] {
			"Le sudoku est une grille de taille NxN c'est-à-dire que le nombre de ligne ainsi",
			"que de colonnes sont identiques. La grille est "
					+ "ainsi composée :",
			"- De lignes",
			"- De colonnes",
			"- De régions",
			"Chaque ligne, colonne et région est "
					+ "composée de cases dont ils",
			"contiennent au choix :",
			"- Des nombres",
			"- Des lettres",
			"- Des symboles",
			"- Des couleurs"
	};

	private final String[] textRule = new String[] {
			"Les règles sont extrêmement simple même si "
					+ "le niveau peut être complexe",
			"Le but étant de remplir chaque case vide de façon à ce que l'on ne rencontre",
			"pas de doublons dans les lignes, les colonnes et dans les régions",
			"Si vous êtes perdu, vous pouvez utiliser ",
			"l'aide ou vous pouvez également apprendre les techniques de résolution"
	};

	private final int size = 550;

	// ATTRIBUTS

	private JFrame frame;

	// CONSTRUCTEUR

	public GameRuleAction() {
		this.createView();
		this.placeComponents();
	}

	// REQUETES

	public boolean getVisible() {
		return this.frame.isVisible();
	}

	// COMMANDES

	/*
	 * @Override
	 * public void actionPerformed(ActionEvent e) {
	 * frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	 * frame.setLocationRelativeTo(null);
	 * frame.setVisible(true);
	 * }
	 */

	public void dispose() {
		this.frame.dispose();
	}

	public void display() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// OUTILS

	/*
	 * Créer la vue.
	 */
	private void createView() {
		this.frame = new JFrame("Règles du jeu");
		frame.setSize(size, size);
		this.frame.setResizable(false);
	}

	/**
	 * Place les composants sur la fenêtre.
	 */
	private void placeComponents() {
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(size - 20, (int) ((float) size * 1.5) - 100));
		p.add(this.createPanelExplication());
		p.add(this.createPanelRule());
		JScrollPane s = new JScrollPane(p);
		s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.frame.add(s, BorderLayout.CENTER);
	}

	/**
	 * Crée la partie explication sur ce qu'est un sudoku.
	 */
	private JPanel createPanelExplication() {
		TitledBorder b = BorderFactory.createTitledBorder("Le sudoku");
		JPanel p = new JPanel(new GridLayout(2, 0));
		{ // --
			JLabel image = new JLabel(new ImageIcon(this.pathGrid));
			p.add(image);

			JPanel q = new JPanel(new GridLayout(textExplained.length, 0));
			{ // --
				for (String txt : this.textExplained) {
					JLabel lbl = new JLabel(txt);
					lbl.setFont(font);
					q.add(lbl);
				}
			}
			p.add(q);
		}
		p.setBorder(b);
		return p;
	}

	/**
	 * Créer la partie qui explique comment jouer au sudoku.
	 */
	private JPanel createPanelRule() {
		TitledBorder b = BorderFactory.createTitledBorder("Comment jouer ?");
		JPanel p = new JPanel();
		{ // --
			JPanel q = new JPanel(new GridLayout(textRule.length, 0));
			{ // --
				for (String txt : this.textRule) {
					JLabel lbl = new JLabel(txt);
					lbl.setFont(font);
					q.add(lbl);
				}
			}
			p.add(q);
		}
		p.setBorder(b);
		return p;
	}
}
