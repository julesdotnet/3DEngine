package com.example._3dengine;

import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Renderer extends Pane {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private Camera camera;
    private GeometricalBody body;

    public Renderer(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        widthProperty().addListener(this::handleResize);
        heightProperty().addListener(this::handleResize);
    }

    private void handleResize(Observable observable) {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        if (camera != null && body != null) {
            clear();
            drawBody(camera, body);
        }
    }

    public void drawPoint(double x, double y, Color color) {
        gc.setFill(color);
        double size = 4;
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }

    public void drawLine(double x1, double y1, double x2, double y2, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.strokeLine(x1, y1, x2, y2);
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawBody(Camera camera, GeometricalBody body) {
        this.camera = camera;
        this.body = body;

        double[][] vertices = body.getVertices();
        double[][] edges = body.getEdges();

        double scale = 100;
        double offsetX = canvas.getWidth() / 2;
        double offsetY = canvas.getHeight() / 2;

        // Project vertices
        double[][] projected = new double[vertices.length][];
        for (int i = 0; i < vertices.length; i++) {
            double[] v = vertices[i];
            double[] p = camera.projectPoint(v[0], v[1], v[2] + 5);
            if (p != null) {
                double x = p[0] * scale + offsetX;
                double y = -p[1] * scale + offsetY;
                projected[i] = new double[]{x, y};
            } else {
                projected[i] = null;
            }
        }

        // Draw edges
        if (edges != null) {
            for (double[] edge : edges) {
                if (edge.length < 2) continue;

                int from = (int) edge[0];
                int to = (int) edge[1];

                // Validate indices
                if (from < 0 || from >= projected.length || to < 0 || to >= projected.length)
                    continue;

                double[] a = projected[from];
                double[] b = projected[to];

                if (a != null && b != null) {
                    drawLine(a[0], a[1], b[0], b[1], Color.GREEN);
                }
            }
        }

        // Draw points
        for (double[] p : projected) {
            if (p != null) {
                drawPoint(p[0], p[1], Color.RED);
            }
        }
    }

    @Override
    protected double computePrefWidth(double height) {
        return canvas.getWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        return canvas.getHeight();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }
}
