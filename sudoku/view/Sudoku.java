package sudoku.view;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import javax.swing.border.Border;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import sudoku.model.*;

import sudoku.model.Action;
import sudoku.model.heuristic.*;
import sudoku.model.info.ColorSudoku;
import sudoku.model.info.Command;
import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.model.info.Type;

import sudoku.model.regions.IRegion;
import sudoku.util.Chronometer;
import sudoku.util.Point;
import sudoku.util.Tools;
import sudoku.view.controller.DraftNumberAction;
import sudoku.view.controller.ManageController;
import sudoku.view.controller.NumberAction;

import sudoku.view.controller.menu.*;
import sudoku.view.controller.menu.GameRuleAction;
import sudoku.view.controller.menu.RedoAction;
import sudoku.view.controller.menu.ResetAction;
import sudoku.view.controller.menu.UndoAction;
import sudoku.view.menu.Item;
import sudoku.view.menu.Menu;
import sudoku.view.menu.MenuGridResolve;
import sudoku.view.menu.MenuNewGame;

/**
 * La vue de l'application.
 * 
 * @author Izana Khabouri
 */
public class Sudoku {

	// CONSTANTES ET ATTRIBUTS STATIQUES
	private final String pathImg = new Tools().getAbsolute("../templ"
			+ "ates/assets/sudoku_logo.jpg");
	private final int BORDER_SIZE_REGION = 2;
	private final int BORDER_SIZE_GRID = 2;
	private final int SIZE_NUMBER_FIXED_USER = 27;
	private final int SIZE_NUMBER_FIXED_GRID = 25;
	private final Color BORDER_REGION = Color.black;

	private static final Option OPTION = new Option();
	private static final Chronometer CHRONOMETER = new Chronometer();

	// LIGHT MODE
	public static final Color FONT_LIGHT = new Color(0, 0, 0);
	private final Color BACKGROUND_LIGHT = new Color(255, 255, 255);
	private final Color FONT_DRAFT_CHECKED_LIGHT = new Color(0, 0, 0);
	private final Color FONT_DRAFT_NOT_CHECKED_LIGHT = new Color(0, 0, 0, 20);

	// DARK MODE
	public static final Color FONT_DARK = new Color(255, 255, 255);
	private final Color BACKGROUND_DARK = new Color(33, 33, 33);
	private final Color FONT_DRAFT_CHECKED_DARK = new Color(255, 255, 255);
	private final Color FONT_DRAFT_NOT_CHECKED_DARK = new Color(255, 255, 255, 20);

	// ATTRIBUTS

	private JFrame frame;
	private JPanel[][] panels;
	private JPanel panel;
	private JPanel informations;
	private JButton help;
	private JButton finish;
	private JButton resolve;
	private JLabel size;
	private JLabel lvl;
	private JLabel data;
	private JTextArea output;
	private Map<String, JMenuItem> bkey;
	private Set<JComponent> events;
	private GameRuleAction rules;
	private IGrid model;

	// CONSTRUCTEURS

	private Sudoku(IGrid model) {
		createModel(model);
		createView();
		placeComponents();
		createController();
	}

	// COMMANDES

	/**
	 * Affiche notre fenêtre à l'écran.
	 */
	public void display() {
		frame.pack();
		this.frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Détruit notre fenêtre.
	 */
	public void dispose() {
		this.frame.dispose();
	}

	// OUTILS

	/**
	 * Crée notre modèle.
	 */
	private void createModel(IGrid model) {
		this.rules = null;
		this.model = model;
	}

	/**
	 * Initialise les cases de la grilles en fonction du modèle.
	 */
	private void initPanels() {
		final int size = model.getSize().getSize();
		this.panels = new JPanel[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.panels[i][j] = new JPanel(new BorderLayout());
			}
		}
	}

	/**
	 * Crée les différents éléments dits majeurs.
	 */
	private void createView() {
		this.frame = new JFrame("SudokuApp");
		ImageIcon img = new ImageIcon(pathImg);
		this.frame.setIconImage(img.getImage());
		this.size = new JLabel(this.model.getSize().getNomButton());
		this.lvl = new JLabel(model.getLevel().getLevel());
		this.data = new JLabel(model.getType().getLabel());
		this.events = new HashSet<JComponent>();
		this.bkey = new HashMap<String, JMenuItem>();
		this.help = new JButton("Aide");
		this.finish = new JButton("Terminé");
		this.resolve = new JButton("Résoudre");
		this.resolve.setPreferredSize(new Dimension(95, 18));
		;
		this.output = new JTextArea(5, 50);
		this.output.setCaretPosition(0);
		this.output.setEditable(false);
		this.output.setLineWrap(true);
		this.initPanels();
	}

	/**
	 * Retourne le menu de notre fenêtre.
	 */
	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu();
		for (Menu m : Menu.values()) {
			menu = new JMenu(m.getMenu());
			menu.setMnemonic(m.getMnemonic());
			for (Item item : m.getItems()) {
				if (item == null) {
					menu.addSeparator();
				} else if (item.getLabel() != null) {
					JMenuItem it = new JMenuItem(item.getLabel());
					menu.add(it);
					this.bkey.put(item.getLabel(), it);
				} else {
					if (item == Item.NEW_GAME) {
						menu.add(MenuNewGame.getJMenuOfNewGame());
						menu.addSeparator();
					} else if (item == Item.RESOLVE_GRID) {
						menu.addSeparator();
						menu.add(MenuGridResolve.getJMenuOfGridResolve());
					}
				}
			}
			menuBar.add(menu);
		}
		menuBar.add(Box.createHorizontalGlue());
		CHRONOMETER.setVisible(OPTION.getOption().get("chronometer"));
		menuBar.add(CHRONOMETER);
		return menuBar;
	}

	/**
	 * La méthode placeComponents place les composants dans notre fenêtre.
	 */
	private void placeComponents() {
		this.frame.setJMenuBar(this.createMenu());
		this.setMenuEnabled();
		JPanel p = new JPanel();
		{ // --
			JPanel q = this.createPanelInformation();
			p.add(q, FlowLayout.LEFT);
		}
		frame.add(p, BorderLayout.NORTH);
		p = new JPanel();
		{ // --
			JScrollPane pane = new JScrollPane(this.output);
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			pane.setPreferredSize(new Dimension(400, 60));
			Font font = output.getFont();
			float size = font.getSize() - 4.0f;
			output.setFont(font.deriveFont(size));
			p.add(pane);
			p.add(this.help);
			p.add(this.finish);
		}
		frame.add(p, BorderLayout.SOUTH);
		this.panel = new JPanel(new BorderLayout());
		{ // --
			this.panel.add(this.createGrid(), BorderLayout.CENTER);
		}
		frame.add(this.panel, BorderLayout.CENTER);
	}

	/**
	 * Retourne un objet de type JPanel qui contient la grille de sudoku.
	 */
	private JPanel createGrid() {
		JPanel p = new JPanel();
		{ // --
			Color c = BORDER_REGION;
			JPanel q = new JPanel(new GridLayout(model.getSize().getNumberRegionCols(),
					model.getSize().getNumberRegionLines()));
			q.setBorder(BorderFactory.createLineBorder(c, BORDER_SIZE_GRID));
			{ // --
				for (IRegion region : model.getSize().getTypeRegion()) {
					Border border = BorderFactory.createLineBorder(c, BORDER_SIZE_REGION);
					JPanel r = createRegion(region);
					{ // --
						r.setPreferredSize((this.model.getSize().getDimension()));
						r.setBorder(border);
					}
					q.add(r);
				}
			}
			p.add(q);
		}
		return p;
	}

	/**
	 * Crée une région associé à la région region.
	 */
	private JPanel createRegion(IRegion region) {
		ICase[][] t = this.model.getGrid();
		JPanel p = new JPanel(new GridLayout(model.getSize().getNumberRegionLines(),
				model.getSize().getNumberRegionCols()));
		{ // --
			for (int i = region.getStartX(); i <= region.getEndX(); i++) {
				for (int j = region.getStartY(); j <= region.getEndY(); j++) {
					boolean byGrid = t[i][j].getIsFixedByGrid();
					boolean isColor = this.model.getType() == Type.COLOR;
					if (t[i][j].getValue() == 0) {
						this.panels[i][j].add(!isColor ? createDraftGrid(i, j, null)
								: createDraftColorGrid(i, j, null));
					} else {
						this.panels[i][j]
								.add(!isColor ? getNumberFixed(i, j, byGrid) : getNumberColorFixed(i, j, byGrid));
					}
					boolean isLightMode = OPTION.getOption().get("display");
					this.panels[i][j].setBackground(!isLightMode ? BACKGROUND_DARK : BACKGROUND_LIGHT);

					int top = (i == region.getStartX()) ? 0 : 1;
					int right = (j == region.getEndY()) ? 0 : 1;
					panels[i][j].setBorder(BorderFactory.createMatteBorder(top, 0,
							0, right, Color.BLACK));
					this.panels[i][j].setVisible(true);
					p.add(this.panels[i][j]);
				}
			}
		}
		return p;
	}

	/**
	 * Crée un panel qui contient une grille contenant tout les brouillons.
	 * A ces brouillons s'ajoutent une commande associé aux nombres non fixés.
	 * Ainsi crée la commande associé à chacun de ces différents brouillon.
	 */
	private JPanel createDraftColorGrid(int i, int j, Color color) {
		Size size = this.model.getSize();
		boolean isLightMode = OPTION.getOption().get("display");
		JPanel p = new JPanel(new GridLayout(size.getNumberRegionCols(),
				size.getNumberRegionLines()));
		for (int x = 1; x <= size.getSize(); x++) {
			JLabel lbl = new JLabel(Tools.getData(x, model.getType()));
			JPanel q = new JPanel(new BorderLayout());
			ColorSudoku cs = ColorSudoku.values()[(x - 1)];
			MouseListener e = new DraftNumberAction(model, i, j, !isLightMode);
			lbl.addMouseListener(e);
			if (!events.contains(lbl)) {
				events.add(lbl);
			}
			q.add(lbl, BorderLayout.CENTER);
			boolean contains = model.getGrid()[i][j].getCandidates().contains(x);
			Color c = (contains ? cs.getColor() : cs.getColorNotChecked(!isLightMode));
			lbl.setForeground(color == null ? c : color);
			q.setBackground(color == null ? c : color);
			final int sizeFont = model.getSize().getSizeDraftFont();
			lbl.setFont(new Font("Serif", Font.PLAIN, sizeFont));
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			p.add(q);
		}
		p.setBackground(!isLightMode ? BACKGROUND_DARK : BACKGROUND_LIGHT);
		return p;
	}

	private JPanel createDraftGrid(int i, int j, Color color) {
		JPanel p = new JPanel(new GridLayout(model.getSize().getNumberRegionCols(),
				model.getSize().getNumberRegionLines()));
		boolean isLightMode = OPTION.getOption().get("display");
		for (int x = 1; x <= this.model.getSize().getSize(); x++) {
			JLabel lbl = new JLabel(Tools.getData(x, model.getType()));
			MouseListener e = new DraftNumberAction(model, i, j, !isLightMode);
			lbl.addMouseListener(e);
			if (!events.contains(lbl)) {
				events.add(lbl);
			}
			if (model.getGrid()[i][j].getCandidates().contains(x)) {
				lbl.setForeground(!isLightMode ? FONT_DRAFT_CHECKED_DARK
						: FONT_DRAFT_CHECKED_LIGHT);
			} else {
				lbl.setForeground(!isLightMode ? FONT_DRAFT_NOT_CHECKED_DARK
						: FONT_DRAFT_NOT_CHECKED_LIGHT);
			}
			final int sizeFont = model.getSize().getSizeDraftFont();
			lbl.setFont(new Font("Serif", Font.PLAIN, sizeFont));
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			p.add(lbl);
		}
		if (color != null) {
			p.setBackground(color);
		} else {
			p.setBackground(!isLightMode ? BACKGROUND_DARK
					: BACKGROUND_LIGHT);
		}
		return p;
	}

	/**
	 * Créer un label dont le numéro est fixé soit par la grille soit par
	 * le joueur. L'affichage et les évènements sont différents selon la fixation.
	 * Retourne le label.
	 */
	private JLabel getNumberFixed(int x, int y, boolean byGrid) {
		assert x >= 0 && y >= 0 && x < this.model.getSize().getSize()
				&& y < this.model.getSize().getSize();
		final int number = this.model.getGrid()[x][y].getValue();
		JLabel lbl = new JLabel(Tools.getData(number, model.getType()));
		boolean isLightMode = OPTION.getOption().get("display");
		lbl.setForeground(!isLightMode ? FONT_DARK : FONT_LIGHT);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		if (!byGrid) {
			MouseListener e = new NumberAction(model, x, y);
			lbl.addMouseListener(e);
			if (!events.contains(lbl)) {
				events.add(lbl);
			}
		}
		int f = (byGrid) ? Font.BOLD : Font.PLAIN;
		int s = (byGrid) ? SIZE_NUMBER_FIXED_GRID : SIZE_NUMBER_FIXED_USER;
		lbl.setFont(new Font("Serif", f, s));
		return lbl;
	}

	private JPanel getNumberColorFixed(int x, int y, boolean byGrid) {
		assert x >= 0 && y >= 0 && x < model.getSize().getSize()
				&& y < model.getSize().getSize();
		final int number = this.model.getGrid()[x][y].getValue();
		JLabel lbl = new JLabel(Tools.getData(number, model.getType()));
		Color c = ColorSudoku.values()[number - 1].getColor();
		lbl.setForeground(c);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel p = new JPanel();
		p.add(lbl);
		p.setBackground(c);
		if (!byGrid) {
			MouseListener e = new NumberAction(model, x, y);
			p.addMouseListener(e);
			if (!events.contains(p)) {
				events.add(p);
			}
		}
		return p;
	}

	/**
	 * Retourn un panel qui liste les informations du jeu en cours dont la
	 * difficulté de la
	 * grille, ainsi que le temps mis sur la grille.
	 */
	private JPanel createPanelInformation() {
		this.informations = new JPanel();
		{ // -
			JPanel q = new JPanel(new GridLayout(1, 1));
			{ // --
				q.add(new JLabel("Taille : "));
				q.add(this.size);
			}
			informations.add(q);
			q = new JPanel(new GridLayout(1, 1));
			{ // --
				q.add(new JLabel("Niveau : "));
				q.add(this.lvl);
			}
			informations.add(q);
			q = new JPanel(new GridLayout(1, 1));
			{ // --
				q.add(new JLabel("Type : "));
				q.add(this.data);
			}
			informations.add(q);
		}
		return informations;
	}

	/**
	 * Crée le controlleur.
	 */
	private void createController() {
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.setResizable(false);
		this.createControllerForMenu();
		this.createControllerOnFinish();
		this.createControllerOnHelp();
		this.removeAndCreatePropAndVetoController();
	}

	/**
	 * Supprime les évènement liées aux différentes propriétés du modèle.
	 * Les recrée.
	 */
	private void removeAndCreatePropAndVetoController() {

		ManageController.removeListenerOnModel(model);

		// la commande help
		// ---------------------------------------------------------------------

		this.model.addPropertyChangeListener(IGrid.PROP_HELP, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Answer a = (Answer) evt.getNewValue();
				actionHelp(a);
			}
		});

		// la propriété finished
		// ----------------------------------------------------------------

		this.model.addVetoableChangeListener(IGrid.PROP_FINISHED, new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
				if ((boolean) evt.getOldValue()) {
					throw new PropertyVetoException(null, evt);
				}
				String msg = "Etes-vous sur d'avoir fini ?";
				String title = "Finir le jeu";
				if (!MessageUser.messageQuestion(title, msg)) {
					throw new PropertyVetoException(null, evt);
				}
			}
		});

		this.model.addPropertyChangeListener(IGrid.PROP_FINISHED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				MessageUser msgUser = new MessageUser();
				msgUser.messageEndGame(model.checkGame());
				finish.setEnabled(false);
			}
		});

		// la propriété data
		// ----------------------------------------------------------------

		this.model.addPropertyChangeListener(IGrid.PROP_DATA, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshGrid();
			}
		});

		// la propriété game
		// ----------------------------------------------------------------

		this.model.addVetoableChangeListener(IGrid.PROP_GAME, new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
				Action o = (Action) evt.getNewValue();
				if (o.getValue() < 1 || o.getValue() > model.getSize().getSize()) {
					throw new PropertyVetoException(null, evt);
				}
				ICase c = model.getGrid()[o.getPositionX()][o.getPositionY()];
				int value = c.getValue();
				switch (o.getCommand()) {
					case ADD_CANDIDATES:
						if (c.getCandidates().contains(o.getValue())) {
							throw new PropertyVetoException(null, evt);
						}
						break;
					case REM_CANDIDATES:
						if (!c.getCandidates().contains(o.getValue())) {
							throw new PropertyVetoException(null, evt);
						}
						break;
					case SET_VALUE:
						if (o.getForcedUseCandidate()) {
							if (!c.getCandidates().contains(o.getValue())) {
								throw new PropertyVetoException(null, evt);
							}
						}
						if (value != ICase.CASE_EMPTY) {
							throw new PropertyVetoException(null, evt);
						}
						break;
					case UNSET_VALUE:
						if (value == ICase.CASE_EMPTY) {
							throw new PropertyVetoException(null, evt);
						}
						break;
					default:
						break;
				}
			}
		});

		this.model.addPropertyChangeListener(IGrid.PROP_GAME, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshGrid();
			}
		});

		// la propriété autocomplete
		// ----------------------------------------------------------------

		this.model.addPropertyChangeListener(IGrid.PROP_AUTOCOMPLETE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				model.autoCompleteGrid();
				refreshGrid();
			}
		});

	}

	/**
	 * Crée le contrôleur associé au bouton help.
	 */
	private void createControllerOnHelp() {
		this.help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.help();
			}
		});
	}

	/**
	 * Crée le contrôleur sur le bouton finish.
	 */
	private void createControllerOnFinish() {
		this.finish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.setFinished();
				} catch (PropertyVetoException e1) {
				}
			}
		});
		finish.setEnabled(!this.model.getFinished());
	}

	/**
	 * Supprime les écouteurs sur les boutons et items qui ne sont plus utilisé lors
	 * d'un changement de grille.
	 */
	private void removeController() {
		JButton[] btns = new JButton[] {
				this.help,
				this.finish
		};
		ManageController.removeActionListener(btns);
		JMenuItem[] items = new JMenuItem[] {
				this.bkey.get(Item.RESET_GAME.getLabel()),
				this.bkey.get(Item.UNDO_GAME.getLabel()),
				this.bkey.get(Item.SAVE_GAME.getLabel()),
				this.bkey.get(Item.REDO_GAME.getLabel()),
				this.bkey.get(Item.CHANGE_DATA.getLabel()),
				this.bkey.get(Item.BURMA.getLabel()),
				this.bkey.get(Item.TURBOTFISH.getLabel())
		};
		ManageController.removeActionListener(items);
	}

	/**
	 * Lors d'une demande de nouvelle partie, on remet à jour l'affichage.
	 */
	private void refreshController() {
		this.removeController();
		this.addControllerNotFixed();
		this.createControllerOnFinish();
		this.createControllerOnHelp();
		this.removeAndCreatePropAndVetoController();
		this.setMenuEnabled();
	}

	/**
	 * Crée le contrôleur associé au menu.
	 */
	private void createControllerForMenu() {
		this.addControllerFixed();
		this.addControllerNotFixed();
	}

	/**
	 * Réaction d'aide
	 */
	private void actionHelp(Answer a) {
		refreshGrid();
		if (a != null) {
			for (Point p : a.getCasesOfProofs()) {
				final int x = (int) p.getX();
				final int y = (int) p.getY();
				Color c = null;
				if ((boolean) OPTION.getOption().get("display")) {
					c = Color.white.darker();
				} else {
					c = Color.black.brighter();
				}
				refreshCase(x, y, c);
			}
			int step = 0;
			for (Action act : a.getAllRecommendedActions()) {
				if (act.getCommand() == Command.NOTHING_CMD) {
					step += 1;
				}
				Color c = null;
				if (act.getCommand() == Command.REM_CANDIDATES) {
					c = DisplayColorAnswer.REM.getColorChart()[step];
				} else if (act.getCommand() == Command.SET_VALUE) {
					c = DisplayColorAnswer.SET.getColorChart()[step];
				} else if (act.getCommand() == Command.KEEP_CANDIDATE) {
					c = DisplayColorAnswer.KEEP.getColorChart()[step];
				}
				refreshCase(act.getPositionX(), act.getPositionY(), c);
			}
			output.append(a.getAuthor() + " -> ");
			output.append(a.computeMessage() + "\n");
		} else {
			output.append("->> aucune aide n'a été trouvée \n");
		}
		output.append("---------------------------"
				+ "------------------------------------"
				+ "----------------------------------------\n");
	}

	/**
	 * Crée le contrôleur sur des éléments modifiable.
	 */
	private void addControllerNotFixed() {
		this.bkey.get(Item.TURBOTFISH.getLabel()).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TurbotFish h = new TurbotFish(model);
				Answer a = h.compute();
				actionHelp(a);
				// System.out.println(a != null ? a.computeMessage() : "rien");
			}

		});

		this.bkey.get(Item.BURMA.getLabel()).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Burma h = new Burma(model);
				Answer a = h.compute();
				actionHelp(a);
				// System.out.println(a != null ? a.computeMessage() : "rien");
			}

		});

		this.bkey.get(Item.SAVE_GAME.getLabel()).addActionListener(new SaveAction(this));

		this.bkey.get(Item.CHANGE_DATA.getLabel()).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Type d = MessageUser.messageQuestionOnData(model);
				if (d != null) {
					model.setType(d);
					data.setText(d.getLabel());
					refreshGrid();
				}
			}
		});

		this.bkey.get(Item.RESET_GAME.getLabel()).addActionListener(new ResetAction(this.model));
		this.bkey.get(Item.UNDO_GAME.getLabel()).addActionListener(new UndoAction(this.model));
		this.bkey.get(Item.REDO_GAME.getLabel()).addActionListener(new RedoAction(this.model));
	}

	/**
	 * Crée le contrôleur sur des éléments non modifiable.
	 */
	private void addControllerFixed() {
		// Contrôleur de la nouvelle partie
		Map<String, JMenuItem> mapNewGame = MenuNewGame.getItems();
		for (Size size : Size.values()) {
			for (Level level : Tools.getCorrectLevel()) {
				for (Type type : size.getCanValue()) {
					String label = size.getNomButton() + " "
							+ " " + level.getLevel() + " " + type.getLabel();
					JMenuItem it = mapNewGame.get(label);
					it.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (MessageUser.messageQuestion("Demande de poursuite", "Voulez-vous poursuivre ?")) {
								IGrid newGrid = Grid.loadGridTemplate(type, size, level);
								if (newGrid != null) {
									model = newGrid;
									CHRONOMETER.resetTime();
									recreateGrid(false);
								}
							}
						}
					});
				}
			}
		}

		// Contrôleur de la résolution de la grille
		Map<Size, JMenuItem> mapResolve = MenuGridResolve.getItems();
		for (Size size : Size.values()) {
			JMenuItem it = mapResolve.get(size);
			it.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (MessageUser.messageQuestion("Demande de poursuite", "Voulez-vous poursuivre ?")) {
						Level l = Level.UNKNOW;
						Type t = Type.INTEGER;
						int s = size.getSize();
						model = new Grid(new int[s][s], false,
								false, size, l, t);
						recreateGrid(true);
					}
				}
			});
		}

		// Menu
		this.bkey.get(Item.HELP_GAME.getLabel()).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rules = new GameRuleAction();
				rules.display();
			}
		});

		this.bkey.get(Item.PREFERENCE_GAME.getLabel()).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OPTION.display();
				if (OPTION.change()) {
					changePropGrid(OPTION.getOption());
					OPTION.writeXMLFile();
				}
			}
		});

		this.bkey.get(Item.QUIT_GAME.getLabel()).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "Voulez vous vraiment quitter l'application";
				String title = "Quitter l'application";
				if (MessageUser.messageQuestion(title, msg)) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					frame.setVisible(false);
					if (rules != null && rules.getVisible()) {
						rules.dispose();
					}
					dispose();
				}
			}
		});
		this.bkey.get(Item.LOAD_GAME.getLabel()).addActionListener(new LoadAction(this));
	}

	/**
	 * Change le modèle sur les propriété lié à la gestion de la grille.
	 */
	private void changePropGrid(Map<String, Boolean> opt) {
		if (model.getCanAutoComplete() != opt.get("autocomplete")) {
			model.setCanAutoComplete(opt.get("autocomplete"));
			if (model.getCanAutoComplete()) {
				model.fullGrid();
			}
			model.autoCompleteGrid();
		}
		if (model.getForcedUseCandidate() != opt.get("forcedusecandidate")) {
			model.setForcedUseCandidate(opt.get("forcedusecandidate"));
		}

		CHRONOMETER.setVisible(opt.get("chronometer"));
		refreshGrid();
	}

	/**
	 * Recrée une nouvelle grille.
	 */
	private void recreateGrid(boolean doResolve) {
		output.setText("");
		removeGrid();
		this.size.setText(model.getSize().getNomButton());
		this.lvl.setText(model.getLevel().getLevel());
		this.data.setText(model.getType().getLabel());
		updateButtonResolve();
		this.initPanels();
		refreshGrid();
		refreshController();
		if (doResolve) {
			resolve.setFocusPainted(false);
			this.updateControllerForResolve();
		} else {
			help.setFocusPainted(false);
			help.setEnabled(true);
		}
		this.frame.setLocationRelativeTo(null);
	}

	/**
	 * Met a jour l'affichage du bouton résoudre.
	 */
	private void updateButtonResolve() {
		boolean btnResolveHere = false;
		boolean isResolve = this.model.getLevel() == Level.UNKNOW;
		for (Component c : this.informations.getComponents()) {
			JPanel p = (JPanel) c;
			if (p.getComponents()[0].getClass() == JButton.class) {
				btnResolveHere = true;
			}
		}
		if (isResolve) {
			if (!btnResolveHere) {
				JPanel q = new JPanel(new GridLayout(1, 1));
				{ // --
					q.add(this.resolve);
				}
				this.informations.add(q);
			}
		} else {
			if (btnResolveHere) {
				final int len = this.informations.getComponents().length;
				this.informations.remove(len - 1);
			}
		}
	}

	/**
	 * Met à jour le controlleur sur le bouton résolve.
	 */
	private void updateControllerForResolve() {
		ManageController.removeActionListener(new JButton[] { this.help, this.finish, this.resolve });
		this.resolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BruteStrength h = new BruteStrength(model);
				IGrid g = h.isResolve();
				if (g != null) {
					final int size = g.getSize().getSize();
					for (int i = 0; i < size; i++) {
						for (int j = 0; j < size; j++) {
							ICase c = model.getGrid()[i][j];
							if (!c.getIsFixed()) {
								c = g.getGrid()[i][j];
								try {
									model.setValue(i, j, c.getValue(), false);
								} catch (PropertyVetoException e1) {
								}
							}
						}
					}
				} else {
					if (model.checkGame()) {
						MessageUser.messageInformation("Résolution", "La grille est déjà résolue");
					} else {
						MessageUser.messageError("Résolution", "La grille est insoluble");
					}
				}
			}
		});

		this.finish.setEnabled(false);
		this.help.setEnabled(false);
	}

	/**
	 * Supprime l'affichage de la grille.
	 */
	private void removeGrid() {
		for (JPanel[] panelTab : panels) {
			for (JPanel p : panelTab) {
				p.removeAll();
			}
		}
		ManageController.removeMouseListener(events);
		events.clear();
	}

	/**
	 * Remet à jour toute la grille.
	 */
	public void refreshGrid() {
		removeGrid();
		panel.removeAll();
		refreshController();
		panel.add(createGrid(), BorderLayout.CENTER);
		frame.add(panel, BorderLayout.CENTER);

		this.setMenuEnabled();
		frame.pack();
		frame.repaint();
	}

	/**
	 * Remet à jour l'accessibilité aux items associé aux redo et au undo.
	 */
	private void setMenuEnabled() {
		boolean canRedo = this.model.getAllAction().canRedo();
		this.bkey.get(Item.REDO_GAME.getLabel()).setEnabled(canRedo);

		boolean canUndo = this.model.getAllAction().canUndo();
		this.bkey.get(Item.UNDO_GAME.getLabel()).setEnabled(canUndo);
	}

	/**
	 * Remet à jour la case à la position (i, j).
	 */
	public void refreshCase(int i, int j, Color color) {
		assert i >= 0 && j >= 0 && i < model.getSize().getSize()
				&& j < model.getSize().getSize();
		assert color != null;
		ICase c = this.model.getGrid()[i][j];
		boolean isColor = this.model.getType() == Type.COLOR;
		if (!c.getIsFixed()) {
			if (!isColor) {
				this.getDraftCase(i, j).setBackground(color);
			}
		} else {
			JPanel p = this.panels[i][j];
			if (!isColor) {
				p.setBackground(color);
			}
		}
		frame.pack();
		frame.repaint();
	}

	/**
	 * Retourne la case brouillon à la case (i, j).
	 */
	private JPanel getDraftCase(int i, int j) {
		JPanel p = (JPanel) this.panels[i][j].getComponents()[0];
		boolean isColor = this.model.getType() == Type.COLOR;
		return isColor ? (JPanel) p.getComponents()[0] : p;
	}

	/**
	 * Méthode qui charge une grille selon le niveau de difficulté.
	 */
	public void load(File file) {
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sxb.build(file);
			boolean autocompletion = OPTION.getOption().get("autocomplete");
			boolean forced = OPTION.getOption().get("forcedusecandidate");
			Element racine = doc.getRootElement();
			Grid newGrid = Grid.loadGrid(racine.getChild("Grid"), autocompletion, forced);
			CHRONOMETER.newTime(racine.getChild("Chronometer"));
			removeGrid();
			help.setFocusPainted(false);
			output.setText("");
			model = newGrid;
			this.initPanels();
			refreshController();
			refreshGrid();
			this.frame.setLocationRelativeTo(null);
		} catch (IOException e) {
			MessageUser.messageError("Erreur lecture", e.getMessage());
		} catch (JDOMException e) {
			MessageUser.messageError("Erreur lecture", e.getMessage());
		}
	}

	public void save(File file) {
		// verifier si la partie n'est pas fini
		Element racine = new Element("Sudoku");
		Document doc = new Document(racine);
		racine.addContent(CHRONOMETER.saveChronometer());
		racine.addContent(model.saveGrid());
		try {
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fos = new FileOutputStream(file);
			sortie.output(doc, fos);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, Boolean> getOptionValues() {
		return OPTION.getOption();
	}

	public static Sudoku newGame(Type t, Size s, Level l) {
		IGrid newGrid = Grid.loadGridTemplate(t, s, l);
		if (newGrid == null) {
			MessageUser.messageError("Chargement d'une nouvelle partie",
					"Erreur de chargement d'une nouvelle partie");
			return null;
		}
		return new Sudoku(newGrid);
	}

	public static Sudoku newGridForResolve() {
		Size s = Size.NINE;
		int[][] tab = new int[s.getSize()][s.getSize()];
		Level lvl = Level.UNKNOW;
		Type t = Type.INTEGER;
		IGrid newGrid = new Grid(tab, false, false, s, lvl, t);
		return new Sudoku(newGrid);
	}

	public static Sudoku newGameFromSave(File file) {
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sxb.build(file);
			boolean autocompletion = OPTION.getOption().get("autocomplete");
			boolean forced = OPTION.getOption().get("forcedusecandidate");
			Element racine = doc.getRootElement();
			Grid model = Grid.loadGrid(racine.getChild("Grid"), autocompletion, forced);

			CHRONOMETER.newTime(racine.getChild("Chronometer"));

			return new Sudoku(model);

		} catch (IOException | JDOMException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur lecture", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

}
