package client.gui;

import client.connection.ClientNetwork;
import client.model.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class CanvasPanel extends JPanel {

    private Line line;
    private Color brushColor = Color.BLACK;
    private int brushSize = 4;

    private int prevX, prevY;
    boolean drawing = false;

    private java.util.List<Line> lines = new ArrayList<>();
    private ClientNetwork clientNetwork;

    public CanvasPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
                drawing = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!drawing) return;
                line = new Line();
                int currentX = e.getX();
                int currentY = e.getY();

                line.setX1(prevX);
                line.setY1(prevY);
                line.setX2(currentX);
                line.setY2(currentY);
                line.setBrushColor(brushColor);
                line.setBrushSize(brushSize);
                
                lines.add(line);

                // Send line to server if connected
                if (clientNetwork != null && clientNetwork.isConnected()) {
                    clientNetwork.sendLine(line);
                }

                repaint();

                prevX = currentX;
                prevY = currentY;
            }
        });
    }

    public void setClientNetwork(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
    }

    public java.util.List<Line> getLinesList() {
        return lines;
    }

    public void clearCanvas() {
        lines.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Line line : lines) {
            g2.setStroke(new BasicStroke(line.getBrushSize()));
            g2.setColor(line.getBrushColor());
            g2.drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
        }
    }

    public void undoLastLine() {
        if (!lines.isEmpty()) {
            lines.remove(lines.size() - 1);
            repaint();
        }
    }

    public void redoLastLine() {
        // Can be expanded later
    }

    public void setBrushColor(Color color) {
        this.brushColor = color;
    }

    public Color getBrushColor() {
        return brushColor;
    }

    public void setBrushSize(int size) {
        this.brushSize = size;
    }

    public int getBrushSize() {
        return brushSize;
    }
}