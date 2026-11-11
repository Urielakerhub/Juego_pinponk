package mx.unison.pong;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Board extends JPanel {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private final Paddle paddle;
    private final BallRunner ball;
    private final BlockManager blockManager;
    private final Timer timer;

    private int score = 0;
    private int lives = 3;
    private int level = 1;
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Board() {
        setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        setBackground(new Color(15, 15, 15));
        setFocusable(true);

        paddle = new Paddle(WIDTH, HEIGHT);
        ball = new BallRunner(WIDTH / 2 - BallRunner.SIZE / 2, HEIGHT - 105);
        blockManager = new BlockManager();

        configureKeys();
        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void configureKeys() {
        InputMap input = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = getActionMap();

        bind(input, actions, "LEFT", "left", () -> leftPressed = true);
        bind(input, actions, "released LEFT", "stopLeft", () -> leftPressed = false);
        bind(input, actions, "RIGHT", "right", () -> rightPressed = true);
        bind(input, actions, "released RIGHT", "stopRight", () -> rightPressed = false);
        bind(input, actions, "A", "leftA", () -> leftPressed = true);
        bind(input, actions, "released A", "stopLeftA", () -> leftPressed = false);
        bind(input, actions, "D", "rightD", () -> rightPressed = true);
        bind(input, actions, "released D", "stopRightD", () -> rightPressed = false);
        bind(input, actions, "SPACE", "pause", () -> {
            if (!gameOver) paused = !paused;
        });
        bind(input, actions, "ENTER", "restart", () -> {
            if (gameOver) restartGame();
        });
    }

    private void bind(InputMap input, ActionMap actions, String key,
                      String name, Runnable action) {
        input.put(KeyStroke.getKeyStroke(key), name);
        actions.put(name, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                action.run();
                repaint();
            }
        });
    }

    private void updateGame() {
        if (paused || gameOver) return;

        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();

        ball.move();
        CollisionManager.checkWallCollision(ball, WIDTH, HEIGHT);
        CollisionManager.checkPaddleCollision(ball, paddle);

        if (CollisionManager.checkBlockCollision(ball, blockManager)) {
            score += 10;
        }

        if (ball.getY() + BallRunner.SIZE >= HEIGHT) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                ball.reset(WIDTH / 2 - BallRunner.SIZE / 2, HEIGHT - 105);
            }
        }

        if (blockManager.getRemainingBlocks() == 0) {
            level++;
            score += 100;
            blockManager.reset();
            ball.reset(WIDTH / 2 - BallRunner.SIZE / 2, HEIGHT - 105);
        }
        repaint();
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        level = 1;
        paused = false;
        gameOver = false;
        leftPressed = false;
        rightPressed = false;
        paddle.reset();
        ball.reset(WIDTH / 2 - BallRunner.SIZE / 2, HEIGHT - 105);
        blockManager.reset();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(15, 15, 15));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(90, 90, 90));
        g.setStroke(new BasicStroke(3));
        g.drawRect(2, 2, WIDTH - 4, HEIGHT - 4);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("PUNTOS: " + score, 20, 30);
        g.drawString("VIDAS: " + lives, 275, 30);
        g.drawString("NIVEL: " + level, 520, 30);

        for (Block block : blockManager.getBlocks()) {
            g.setColor(block.getColor());
            g.fill(block.getBounds());
            g.setColor(new Color(30, 30, 30));
            g.draw(block.getBounds());
        }

        g.setColor(new Color(200, 200, 200));
        g.fill(paddle.getBounds());
        g.setColor(Color.WHITE);
        g.fill(ball.getBounds());

        if (paused) drawMessage(g, "PAUSADO", "Presiona ESPACIO para continuar");
        if (gameOver) drawMessage(g, "GAME OVER", "Presiona ENTER para reiniciar");
        g.dispose();
    }

    private void drawMessage(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(title, (WIDTH - metrics.stringWidth(title)) / 2, 220);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        metrics = g.getFontMetrics();
        g.drawString(subtitle, (WIDTH - metrics.stringWidth(subtitle)) / 2, 255);
    }
}
