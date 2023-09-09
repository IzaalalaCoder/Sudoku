package sudoku.view.menu;

import sudoku.model.info.Level;
import sudoku.model.info.Size;
import sudoku.util.Tools;
import sudoku.view.MessageUser;
import sudoku.view.Option;
import sudoku.view.Sudoku;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Launcher {

	// CONSTANTES

	private final static Tools tool = new Tools();
	private final static String PATH_BACK = tool.getAbsolute("../templates/assets/background.png");
	private final String PATH_LOGO = tool.getAbsolute("../templates/assets/sudoku_logo.jpg");
	private final String PATH_PENCIL = tool.getAbsolute("../templates/assets/pencil.png");
	private final String PATH_SAVE = tool.getAbsolute("../templates/assets/save.png");
	private final String PATH_OPTION = tool.getAbsolute("../templates/assets/engrenage.png");
	private final String PATH_TITLE = tool.getAbsolute("../templates/assets/titre.png");
	private final String PATH_RESOLVE = tool.getAbsolute("../templates/assets/resolve.png");
	private final String MSG_ERROR_FILE = "Le fichier n'est pas au format .xml";
	private final String TITLE_ERROR_FILE = "Erreur lecture";
	private final Option option = new Option();
	private final Font buttonFont = new Font("SansSerif", Font.BOLD, 15);

	// ATTRIBUTS

	private JFrame frame;
	private JButton newGameButton;
	private JButton loadGameButton;
	private JButton optionButton;
	private JButton resolveButton;

	// private final Font radioTitleFont = new Font("SansSerif", Font.BOLD, 15);
	// private final Font radioButtonFont = new Font("SansSerif", Font.BOLD, 12);

	// CONSTRUCTEURS
	public Launcher() {
		createView();
		placeComponents();
		createController();
	}

	// COMMANDES

	public void display() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// OUTILS

	private void createView() {
		frame = new JFrame("SudokuApp");
		frame.setPreferredSize(new Dimension(650, 280));
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon(PATH_LOGO).getImage());
		frame.setContentPane(new BackgroundPanel(PATH_BACK, false));
		frame.getContentPane().setBackground(Color.white);

		newGameButton = new JButton("Nouvelle partie");
		newGameButton.setIcon(new ImageIcon(PATH_PENCIL));
		newGameButton.setFont(buttonFont);

		loadGameButton = new JButton("Partie sauvegardé");
		loadGameButton.setIcon(new ImageIcon(PATH_SAVE));
		loadGameButton.setFont(buttonFont);

		resolveButton = new JButton("Résoudre une grille");
		resolveButton.setIcon(new ImageIcon(PATH_RESOLVE));
		resolveButton.setFont(buttonFont);

		optionButton = new JButton("Options");
		optionButton.setIcon(new ImageIcon(PATH_OPTION));
		optionButton.setFont(buttonFont);
	}

	private void placeComponents() {
		JPanel p = new BackgroundPanel(PATH_TITLE, true);
		{
			p.setOpaque(false);
		}
		frame.add(p, BorderLayout.CENTER);

		p = new JPanel(new GridLayout(0, 1));
		{
			p.setOpaque(false);
			p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
			JPanel q = new JPanel(new GridLayout(0, 3));
			{
				q.setOpaque(false);
				q.add(newGameButton);
				q.add(loadGameButton);
				q.add(resolveButton);
			}
			p.add(q);
			q = new JPanel();
			{
				q.setOpaque(false);
				q.add(optionButton);
			}
			p.add(q);

		}
		frame.add(p, BorderLayout.SOUTH);
	}

	private void createController() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*
		 * frame.addWindowStateListener(new WindowStateListener() {
		 * 
		 * @Override
		 * public void windowStateChanged(WindowEvent e) {
		 * System.out.println(frame.getSize().toString());
		 * }
		 * });
		 */

		optionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				option.display();
				if (option.change()) {
					option.writeXMLFile();
				}
			}
		});

		resolveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Sudoku s = Sudoku.newGridForResolve();
				if (s != null) {
					s.display();
					frame.dispose();
				}
			}

		});

		loadGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter fnef = new FileNameExtensionFilter("xml files (*.xml)", "xml");
				chooser.setFileFilter(fnef);
				chooser.addChoosableFileFilter(fnef);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					if (!file.toString().endsWith(".xml")) {
						MessageUser.messageError(TITLE_ERROR_FILE, MSG_ERROR_FILE);
					}
					Sudoku s = Sudoku.newGameFromSave(file);
					if (s != null) {
						s.display();
						frame.dispose();
					}
				}
			}
		});

		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SetupDialog(frame).display();
			}
		});
	}

	private static class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final BufferedImage background;
		private double mult = 1.1;

		public BackgroundPanel(String lien, boolean titre) {
			BufferedImage background1;
			try {
				background1 = ImageIO.read(new File(lien));
			} catch (IOException e) {
				background1 = null;
			}

			if (titre) {
				mult = 1;
			}
			this.background = background1;
			setLayout(new BorderLayout());
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
					RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);

			g2d.scale(1, 1);
			Image scaled = background;
			if (getWidth() < getHeight()) {
				scaled = background.getScaledInstance((int) (getWidth() * mult), -1, Image.SCALE_SMOOTH);
			} else {
				scaled = background.getScaledInstance(-1, (int) (getHeight() * mult), Image.SCALE_SMOOTH);
			}
			// int x = (getWidth() - scaled.getWidth(this)) / 2;
			// int y = (getHeight() - scaled.getHeight(this)) / 2;
			g2d.drawImage(scaled, 0, 0, this);
			g2d.dispose();

		}
	}

	private static class SetupDialog extends JDialog {
		private static final long serialVersionUID = 1L;
		private JButton playButton;
		private EnumMap<Size, JRadioButton> sizeButtons;
		private EnumMap<Level, JRadioButton> levelButtons;
		private final JFrame parent;

		private EnumMap<sudoku.model.info.Type, JRadioButton> typeButtons;

		public SetupDialog(JFrame parent) {
			this.parent = parent;
			setModalityType(ModalityType.APPLICATION_MODAL);
			createView();
			placeComponents();
			createController();
		}

		public void display() {
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		}

		private void createView() {
			playButton = new JButton("Jouer");
			setContentPane(new BackgroundPanel(PATH_BACK, false));
			levelButtons = new EnumMap<Level, JRadioButton>(Level.class);

			for (int i = 0; i < Level.values().length - 1; i++) {
				Level level = Level.values()[i];
				levelButtons.put(level, new JRadioButton(level.getLevel()));
			}
			levelButtons.get(Level.values()[0]).setSelected(true);

			sizeButtons = new EnumMap<Size, JRadioButton>(Size.class);

			for (Size size : Size.values()) {
				sizeButtons.put(size, new JRadioButton(size.getNomButton()));
			}
			sizeButtons.get(Size.values()[0]).setSelected(true);

			typeButtons = new EnumMap<sudoku.model.info.Type, JRadioButton>(sudoku.model.info.Type.class);

			for (sudoku.model.info.Type type : sudoku.model.info.Type.values()) {
				typeButtons.put(type, new JRadioButton(type.getLabel()));
			}
			typeButtons.get(sudoku.model.info.Type.values()[0]).setSelected(true);
		}

		private void placeComponents() {
			JPanel p = new JPanel();
			{
				p.setOpaque(false);
				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

				JPanel q = new JPanel(new GridLayout(1, 0));
				{
					q.setOpaque(false);
					TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
							"Difficulté");
					// tb.setTitleFont(radioTitleFont);
					q.setBorder(tb);

					ButtonGroup bg = new ButtonGroup();
					for (JRadioButton rb : levelButtons.values()) {
						rb.setOpaque(false);
						// rb.setFont(radioButtonFont);
						bg.add(rb);
						q.add(rb);
					}
				}
				p.add(q);

				q = new JPanel(new GridLayout(1, 0));
				{
					q.setOpaque(false);
					TitledBorder tb = BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(),
							"Taille de la grille");
					// tb.setTitleFont(radioTitleFont);
					q.setBorder(tb);

					ButtonGroup bg = new ButtonGroup();
					for (JRadioButton rb : sizeButtons.values()) {
						rb.setOpaque(false);
						// rb.setFont(radioButtonFont);
						bg.add(rb);
						q.add(rb);
					}
				}
				p.add(q);

				q = new JPanel(new GridLayout(1, 0));
				{
					q.setOpaque(false);
					TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
							"Type de case");
					// tb.setTitleFont(radioTitleFont);
					q.setBorder(tb);

					ButtonGroup bg = new ButtonGroup();
					for (JRadioButton rb : typeButtons.values()) {
						rb.setOpaque(false);
						// rb.setFont(radioButtonFont);
						bg.add(rb);
						q.add(rb);
					}
					changeActivatedType(sizeButtons.entrySet().iterator().next().getKey());
				}
				p.add(q);

			}
			this.add(p, BorderLayout.CENTER);
			this.add(playButton, BorderLayout.SOUTH);
		}

		private void createController() {

			for (Map.Entry<Size, JRadioButton> entree : sizeButtons.entrySet()) {
				entree.getValue().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						changeActivatedType(entree.getKey());
					}
				});
			}

			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Level l = Level.values()[0];
					sudoku.model.info.Type t = sudoku.model.info.Type.values()[0];
					Size s = Size.values()[0];

					for (Map.Entry<Level, JRadioButton> entry : levelButtons.entrySet()) {
						if (entry.getValue().isSelected()) {
							l = entry.getKey();
							break;
						}
					}

					for (Map.Entry<Size, JRadioButton> entry : sizeButtons.entrySet()) {
						if (entry.getValue().isSelected()) {
							s = entry.getKey();
							break;
						}
					}

					for (Map.Entry<sudoku.model.info.Type, JRadioButton> entry : typeButtons.entrySet()) {
						if (entry.getValue().isSelected()) {
							t = entry.getKey();
							break;
						}
					}

					Sudoku game = Sudoku.newGame(t, s, l);
					dispose();
					if (game != null) {
						parent.dispose();
						game.display();
					}

				}
			});
		}

		private void changeActivatedType(Size s) {
			EnumMap<sudoku.model.info.Type, JRadioButton> listeTypeButtons = typeButtons.clone();
			boolean selected = false;
			for (sudoku.model.info.Type type : s.getCanValue()) {
				if (listeTypeButtons.get(type).isSelected()) {
					selected = true;
				}
				listeTypeButtons.remove(type).setEnabled(true);
			}
			if (!selected) {
				typeButtons.get(s.getCanValue()[0]).setSelected(true);
			}
			for (JRadioButton rb : listeTypeButtons.values()) {
				rb.setEnabled(false);
			}
		}
	}
}
