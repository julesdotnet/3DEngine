package com.example._3dengine;

public class GeometricalBody {

    private final double[][] vertices;
    private final double[][] edges;


    public GeometricalBody(double[][] vertices, double[][] edges) {
        this.vertices = vertices;
        this.edges = edges;

    }

    public double[][] getVertices() {
        return vertices;
    }

    public double[][] getEdges() {
        return edges;
    }

    private static final double[][] sphereVertices = {
            {0, 0, 0},
            {0, 1, 0},
            {1, 0.5, 0},
            {0, 0.5, 1},
            {-1, 0.5, 0},
            {0, 0.5, -1}
    };

    private static final double[][] sphereEdges = {

            {0, 2},
            {0, 3},
            {0, 4},
            {0, 5},
            {2, 3},
            {3, 4},
            {4, 5},
            {5, 2},
            {1, 2},
            {1, 3},
            {1, 4},
            {1, 5}
    };


    public static final GeometricalBody DIAMOND_BODY = new GeometricalBody(sphereVertices, sphereEdges);

    public static final GeometricalBody CUBE_BODY = new GeometricalBody(
            new double[][] {
                    {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
                    {-1, -1,  1}, {1, -1,  1}, {1, 1,  1}, {-1, 1,  1}
            },
            new double[][] {
                    {0,1}, {1,2}, {2,3}, {3,0},
                    {4,5}, {5,6}, {6,7}, {7,4},
                    {0,4}, {1,5}, {2,6}, {3,7}
            }
    );

    public static final GeometricalBody PYRAMID_BODY = new GeometricalBody(
            new double[][] {
                    {0, 1, 0},
                    {-1, 0, -1},
                    {1, 0, -1},
                    {1, 0, 1},
                    {-1, 0, 1}
            },
            new double[][] {
                    {0,1}, {0,2}, {0,3}, {0,4},
                    {1,2}, {2,3}, {3,4}, {4,1}
            }
    );

}
