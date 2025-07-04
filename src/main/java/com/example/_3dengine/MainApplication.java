package com.example._3dengine;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainApplication extends Application {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Map<String, GeometricalBody> bodyMap = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {
        Renderer renderer = new Renderer(800, 600);
        Camera camera = new Camera(40, 800.0 / 600.0, 0.1, 1000);

        // Populate initial shapes
        bodyMap.put("Sphere", GeometricalBody.DIAMOND_BODY);
        bodyMap.put("Cube", GeometricalBody.CUBE_BODY);
        bodyMap.put("Pyramid", GeometricalBody.PYRAMID_BODY);

        ComboBox<String> shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(bodyMap.keySet());
        shapeSelector.setValue("Sphere");

        VBox topPanel = new VBox(shapeSelector);
        topPanel.setSpacing(5);

        // Left Sidebar for adding new shapes
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextArea verticesArea = new TextArea();
        verticesArea.setPromptText("Vertices, formatted as: 0,0,0; 1,0,0");

        TextArea edgesArea = new TextArea();
        edgesArea.setPromptText("Edges, formatted as 0,1; 1,2");

        Button addShapeButton = new Button("Add Shape");

        VBox sidebar = new VBox(10, new Label("New Shape"),
                new Label("Name:"), nameField,
                new Label("Vertices:"), verticesArea,
                new Label("Edges:"), edgesArea,
                addShapeButton);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-padding: 10; -fx-background-color: #dddddd;");

        // Container layout
        BorderPane root = new BorderPane();
        root.setTop(topPanel);
        root.setLeft(sidebar);
        root.setCenter(renderer);

        Scene scene = new Scene(root, 1000, 600);  // extra width for sidebar

        final GeometricalBody[] currentBody = {bodyMap.get("Sphere")};

        // Handle ComboBox selection
        shapeSelector.setOnAction(e -> {
            String selected = shapeSelector.getValue();
            currentBody[0] = bodyMap.get(selected);
        });

        // Handle adding new shape
        addShapeButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) return;

                double[][] vertices = parsePoints(verticesArea.getText());
                double[][] edges = parseEdges(edgesArea.getText());

                GeometricalBody newBody = new GeometricalBody(vertices, edges);
                bodyMap.put(name, newBody);
                shapeSelector.getItems().add(name);
                nameField.clear();
                verticesArea.clear();
                edgesArea.clear();
            } catch (Exception ex) {
                ex.printStackTrace(); // Ideally, show an alert instead
            }
        });

        // Handle keyboard input
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double speed = 0.1;
                double rotSpeed = 0.02;

                if (pressedKeys.contains(KeyCode.W)) camera.moveForward(speed);
                if (pressedKeys.contains(KeyCode.S)) camera.moveBackward(speed);
                if (pressedKeys.contains(KeyCode.D)) camera.strafeRight(speed);
                if (pressedKeys.contains(KeyCode.A)) camera.strafeLeft(speed);
                if (pressedKeys.contains(KeyCode.J)) camera.rotateYaw(rotSpeed);
                if (pressedKeys.contains(KeyCode.L)) camera.rotateYaw(-rotSpeed);
                if (pressedKeys.contains(KeyCode.Q)) camera.moveUp(0.08);
                if (pressedKeys.contains(KeyCode.SHIFT)) camera.moveDown(0.08);

                renderer.clear();
                renderer.drawBody(camera, currentBody[0]);
            }
        };
        timer.start();

        stage.setTitle("3D Renderer | Julian Rieder KG");
        stage.setScene(scene);
        stage.show();
        scene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }

    // Parse vertices string into double[][]
    private double[][] parsePoints(String input) {
        String[] lines = input.split(";");
        List<double[]> points = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.trim().split(",");
            if (parts.length != 3) continue;
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            points.add(new double[]{x, y, z});
        }
        return points.toArray(new double[0][]);
    }

    // Parse edges string into double[][]
    private double[][] parseEdges(String input) {
        String[] lines = input.split(";");
        List<double[]> edges = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.trim().split(",");
            if (parts.length != 2) continue;
            int from = Integer.parseInt(parts[0].trim());
            int to = Integer.parseInt(parts[1].trim());
            edges.add(new double[]{from, to});
        }
        return edges.toArray(new double[0][]);
    }
}
