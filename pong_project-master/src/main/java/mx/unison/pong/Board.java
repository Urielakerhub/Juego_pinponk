package mx.unison.pong;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private Ball ball;
    private Paddle paddle;
    private BlockManager blockManager;

    private Timer timer;

    private int score = 0;
    private int lives = 3;

    private boolean paused = false;
    private boolean gameOver = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Board() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(15, 15, 15));
        setFocusable(true);

        paddle = new Paddle(WIDTH, HEIGHT);
        ball = new Ball(312, 350);
        blockManager = new BlockManager();

        configureKeys();

        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void configureKeys() {

        InputMap input = getInputMap(
                WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actions = getActionMap();

        input.put(KeyStroke.getKeyStroke("LEFT"), "left");
        input.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");

        input.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        input.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");

        input.put(KeyStroke.getKeyStroke("A"), "left");
        input.put(KeyStroke.getKeyStroke("released A"), "stopLeft");

        input.put(KeyStroke.getKeyStroke("D"), "right");
        input.put(KeyStroke.getKeyStroke("released D"), "stopRight");

        input.put(KeyStroke.getKeyStroke("SPACE"), "pause");
        input.put(KeyStroke.getKeyStroke("ENTER"), "restart");

        actions.put("left", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                leftPressed = true;
            }
        });

        actions.put("stopLeft", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                leftPressed = false;
            }
        });

        actions.put("right", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                rightPressed = true;
            }
        });

        actions.put("stopRight", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                rightPressed = false;
            }
        });

        actions.put("pause", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!gameOver) {
                    paused = !paused;
                    repaint();
                }
            }
        });

        actions.put("restart", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (gameOver) {
                    restartGame();
                }
            }
        });
    }

    private void updateGame() {

        if (paused || gameOver) {
            return;
        }

        if (leftPressed) {
            paddle.moveLeft();
        }

        if (rightPressed) {
            paddle.moveRight();
        }

        ball.move();

        // Colisiones con paredes
        CollisionManager.checkWallCollision(
                ball, WIDTH, HEIGHT
        );

        // Colisión con raqueta
        CollisionManager.checkPaddleCollision(
                ball, paddle
        );

        // Colisión con bloques
        if (CollisionManager.checkBlockCollision(
                ball, blockManager)) {

            score += 10;
        }

        // La pelota cayó
        if (ball.getY() + Ball.SIZE >= HEIGHT) {

            lives--;

            if (lives <= 0) {
                gameOver = true;
            } else {
                ball.reset(312, 350);
            }
        }

        // Se destruyeron todos los bloques
        if (blockManager.getRemainingBlocks() == 0) {
            blockManager.reset();
            ball.reset(312, 350);
            score += 100;
        }

        repaint();
    }

    private void restartGame() {

        score = 0;
        lives = 3;

        gameOver = false;
        paused = false;

        ball.reset(312, 350);
        blockManager.reset();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Fondo
        g.setColor(new Color(15, 15, 15));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Bordes
        g.setColor(new Color(90, 90, 90));
        g.setStroke(new BasicStroke(3));
        g.drawRect(2, 2, WIDTH - 4, HEIGHT - 4);

        // Puntuación y vidas
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.WHITE);

        g.drawString("PUNTOS: " + score, 20, 30);
        g.drawString("VIDAS: " + lives, 520, 30);

        // Bloques
        for (Block block : blockManager.getBlocks()) {

            g.setColor(block.getColor());
            g.fill(block.getBounds());

            g.setColor(new Color(30, 30, 30));
            g.draw(block.getBounds());
        }

        // Raqueta
        g.setColor(new Color(200, 200, 200));
        g.fill(paddle.getBounds());

        // Pelota
        g.setColor(Color.WHITE);
        g.fill(ball.getBounds());

        // Pausa
        if (paused) {
            drawMessage(g, "PAUSADO", "Presiona ESPACIO");
        }

        // Fin del juego
        if (gameOver) {
            drawMessage(g, "GAME OVER", "Presiona ENTER para reiniciar");
        }

        g.dispose();
    }

    private void drawMessage(
            Graphics2D g, String title, String subtitle) {

        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setTextAlign(
                java.awt.font.TextAttribute.WEIGHT,
                java.awt.font.TextAttribute.WEIGHT_BOLD
        );

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));

        FontMetrics fm = g.getFontMetrics();

        g.drawString(
                title,
                (WIDTH - fm.stringWidth(title)) / 2,
                220
        );

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));

        fm = g.getFontMetrics();

        g.drawString(
                subtitle,
                (WIDTH - fm.stringWidth(subtitle)) / 2,
                255
        );
    }
}
