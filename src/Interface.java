import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.*;

public class Interface {
    int rezhim = 1; // Режим рисования
    int xPad;
    int xf;
    int yf;
    int yPad;
    boolean pressed = false;
    Color currentColor; // текущий цвет
    JFrame frame;
    MyPanel panel;
    BufferedImage holst; // поверхность рисования
    JTextArea messageNS; // окно вывода для сообщений от нейронной сети


    private void createButtonLearningSystem(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("Обучить");

        String[] signs = {
                "Овен", "Телец", "Близнецы", "Рак", "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог", "Водолей", "Рыбы"};
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            Action signZodiacAction = new AbstractAction(signs[finalI]) {
                public void actionPerformed(ActionEvent event) {
                    System.out.println("selected " + signs[finalI]);
                    // тут вызывается функция обучения с учителем
                    messageNS.setText(signs[finalI] /*сюда подать ответ нейронной сети*/);

                }
            };
            JMenuItem loadMenu = new JMenuItem(signZodiacAction);
            fileMenu.add(loadMenu);
            menuBar.add(fileMenu);
        }
    }

    private void createButtonDetectImage(JMenuBar menuBar) {
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
    }

    private void createButtonClearHolst(JMenuBar menuBar) {
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
    }

    private void createReplyPane() {
        messageNS = new JTextArea();
        messageNS.setBounds(0, 0, 380, 20);
        messageNS.setBackground(Color.LIGHT_GRAY);
        frame.add(messageNS);
    }

    private void createPanel() {
        panel = new MyPanel();
        panel.setBounds(20, 30, 320, 320);
        panel.setBackground(Color.white);
        panel.setOpaque(true);
        frame.add(panel);
    }

    private void createFrame() {
        frame = new JFrame("perceptron");
        frame.setSize(380, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menuBar.setBounds(0, 0, 380, 10);
        return menuBar;
    }

    private void initAllComponent() {
        createFrame();
        currentColor = Color.black;

        JMenuBar menuBar = createMenuBar();

        createReplyPane();
        createButtonDetectImage(menuBar);
        createButtonLearningSystem(menuBar);
        createButtonClearHolst(menuBar);
        createPanel();
    }

    public Interface() {
        initAllComponent();

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (pressed == true) {
                    Graphics g = holst.getGraphics();
                    Graphics2D g2 = (Graphics2D) g;
                    // установка цвета
                    g2.setColor(currentColor);
                    switch (rezhim) {
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
    }
}