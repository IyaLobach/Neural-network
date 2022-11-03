import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.filechooser.FileFilter;

public class Interface {
    int rezhim = 1; // Режим рисования
    int xPad;
    int xf;
    int yf;
    int yPad;
    int thickness;
    boolean pressed = false;
    Color currentColor; // текущий цвет
    JFrame frame;
    MyPanel panel;
    JButton buttonsForColors;
    JColorChooser colorChooser;
    BufferedImage holst; // поверхность рисования
    boolean loading = false;// если мы загружаем картинку
    String fileName;

    public Interface() {
        frame = new JFrame("perceptron");
        frame.setSize(380, 380);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentColor = Color.black;

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menuBar.setBounds(0, 0, 380, 10);


        Action saveAction = new AbstractAction("Распознать") {
            public void actionPerformed(ActionEvent event) {
                int resizeWidth = 32;
                int resizeHeight = 32;

                BufferedImage bufferedImageOutput = new BufferedImage(resizeWidth,
                        resizeHeight, holst.getType());

                Graphics2D g2d = bufferedImageOutput.createGraphics();
                g2d.drawImage(holst, 0, 0, resizeWidth, resizeHeight, null);
                g2d.dispose();

                try {
                    ImageIO.write(bufferedImageOutput, "png", new File("imageForSelect.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // тут вызывается функция распознания изображения imageForSelect.png,
                // а ответ этой функции выводится в соответствующую панель
            }
        };

        JMenuItem saveMenu = new JMenuItem(saveAction);
        menuBar.add(saveMenu);
        JMenu fileMenu = new JMenu("Обучить");

        String[] signs = {
                "Овен", "Телец", "Близнецы", "Рак", "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог", "Водолей", "Рыбы"};
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            Action signZodiacAction = new AbstractAction(signs[finalI]) {
                public void actionPerformed(ActionEvent event) {
                    System.out.println("selected " + signs[finalI]);
                    // тут вызывается функция обучения с учителем
                }
            };
            JMenuItem loadMenu = new JMenuItem(signZodiacAction);
            fileMenu.add(loadMenu);
            menuBar.add(fileMenu);
        }


        Action saveasAction = new AbstractAction("Очистить холст") {
            public void actionPerformed(ActionEvent event) {
                Graphics g = holst.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(currentColor); // установка цвета
                g2.setStroke(new BasicStroke(1000.0f));
                g2.setColor(Color.WHITE);
                g2.drawLine(0, 0, 320, 320);
                panel.repaint();
                frame.repaint();
            }
        };
        JMenuItem saveasMenu = new JMenuItem(saveasAction);
        menuBar.add(saveasMenu);

        panel = new MyPanel();
        panel.setBounds(20, 0, 320, 320);
        panel.setBackground(Color.white);
        panel.setOpaque(true);
        frame.add(panel);

        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.VERTICAL);

        JButton penbutton = new JButton(new ImageIcon("iconForButton/pen.png"));
        penbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 0;
            }
        });
        toolbar.add(penbutton);
        JButton brushbutton = new JButton(new ImageIcon("iconForButton/brush.png"));
        brushbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 1;
            }
        });
        toolbar.add(brushbutton);

        JButton lasticbutton = new JButton(new ImageIcon("iconForButton/lastic.png"));
        lasticbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 2;
            }
        });
        toolbar.add(lasticbutton);

        JButton textbutton = new JButton(new ImageIcon("iconForButton/text.png"));
        textbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 3;
            }
        });
        toolbar.add(textbutton);

        JButton linebutton = new JButton(new ImageIcon("iconForButton/line.png"));
        linebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 4;
            }
        });
        toolbar.add(linebutton);

        JButton elipsbutton = new JButton(new ImageIcon("iconForButton/elips.png"));
        elipsbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 5;
            }
        });
        toolbar.add(elipsbutton);

        JButton rectbutton = new JButton(new ImageIcon("iconForButton/rect.png"));
        rectbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rezhim = 6;
            }
        });
        toolbar.add(rectbutton);

        toolbar.setBounds(0, 0, 30, 300);
//        frame.add(toolbar);

        colorChooser = new JColorChooser(currentColor);
        colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                currentColor = colorChooser.getColor();
                buttonsForColors.setBackground(currentColor);
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (pressed == true) {
                    Graphics g = holst.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(currentColor);
                    switch (rezhim) {
                        // карандаш
                        case 0:
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // кисть
                        case 1:
                            g2.setStroke(new BasicStroke(12.0f));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // ластик
                        case 2:
                            g2.setStroke(new BasicStroke(3.0f));
                            g2.setColor(Color.WHITE);
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                    }
                    xPad = e.getX();
                    yPad = e.getY();
                }
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                Graphics g = holst.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(currentColor);
                switch (rezhim) {
                    // карандаш
                    case 0:
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // кисть
                    case 1:
                        g2.setStroke(new BasicStroke(3.0f));
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // ластик
                    case 2:
                        g2.setStroke(new BasicStroke(3.0f));
                        g2.setColor(Color.WHITE);
                        g2.drawLine(xPad, yPad, xPad + 1, yPad + 1);
                        break;
                    // текст
                    case 3:
                        // устанавливаем фокус для панели,
                        // чтобы печатать на ней текст
                        panel.requestFocus();
                        break;
                }
                xPad = e.getX();
                yPad = e.getY();

                pressed = true;
                panel.repaint();
            }

            public void mousePressed(MouseEvent e) {
                xPad = e.getX();
                yPad = e.getY();
                xf = e.getX();
                yf = e.getY();
                pressed = true;
            }

            public void mouseReleased(MouseEvent e) {

                Graphics g = holst.getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                // установка цвета
                g2.setColor(currentColor);
                // Общие рассчеты для овала и прямоугольника
                int x1 = xf, x2 = xPad, y1 = yf, y2 = yPad;
                if (xf > xPad) {
                    x2 = xf;
                    x1 = xPad;
                }
                if (yf > yPad) {
                    y2 = yf;
                    y1 = yPad;
                }
                switch (rezhim) {
                    // линия
                    case 4:
                        g.drawLine(xf, yf, e.getX(), e.getY());
                        break;
                    // круг
                    case 5:
                        g.drawOval(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                    // прямоугольник
                    case 6:
                        g.drawRect(x1, y1, (x2 - x1), (y2 - y1));
                        break;
                }
                xf = 0;
                yf = 0;
                pressed = false;
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                // устанавливаем фокус для панели,
                // чтобы печатать на ней текст
                panel.requestFocus();
            }

            public void keyTyped(KeyEvent e) {
                if (rezhim == 3) {
                    Graphics g = holst.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(currentColor);
                    g2.setStroke(new BasicStroke(2.0f));

                    String str = new String("");
                    str += e.getKeyChar();
                    g2.setFont(new Font("Arial", 0, 15));
                    g2.drawString(str, xPad, yPad);
                    xPad += 10;
                    // устанавливаем фокус для панели,
                    // чтобы печатать на ней текст
                    panel.requestFocus();
                    panel.repaint();
                }
            }
        });

        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Interface();
            }
        });
    }

//    class MyFrame extends JFrame {
//        public void paint(Graphics g) {
//            super.paint(g);
//        }
//
//        public MyFrame(String title) {
//            super(title);
//        }
//    }

    class MyPanel extends JPanel {
        public MyPanel() {
        }

        public void paintComponent(Graphics g) {
            if (holst == null) {
                holst = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D d2 = (Graphics2D) holst.createGraphics();
                d2.setColor(Color.white);
                d2.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            super.paintComponent(g);
            g.drawImage(holst, 0, 0, this);
        }

        public void clearHolst() {
            Graphics g = holst.createGraphics();
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, this.getWidth(), this.getHeight());

        }
    }

    // Фильтр картинок
    class TextFileFilter extends FileFilter {
        private String ext;

        public TextFileFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(java.io.File file) {
            if (file.isDirectory()) return true;
            return (file.getName().endsWith(ext));
        }

        public String getDescription() {
            return "*" + ext;
        }
    }
}