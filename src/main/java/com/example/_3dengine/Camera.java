package com.example._3dengine;

public class Camera {
    private double[][] projectionMatrix;
    private double fov, aspectRatio, near, far;

    // Camera position
    private double posX = 0, posY = 0, posZ = 0;

    // Camera rotation (Euler angles in radians)
    private double pitch = 0; // rotation around X axis (up/down)
    private double yaw = 0;   // rotation around Y axis (left/right)
    private double roll = 0;  // rotation around Z axis (tilt)

    public Camera(double fov, double aspectRatio, double near, double far) {
        this.fov = Math.toRadians(fov);
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
        this.projectionMatrix = createProjectionMatrix();
    }

    private double[][] createProjectionMatrix() {
        double f = 1.0 / Math.tan(fov / 2.0);
        double rangeInv = 1.0 / (near - far);

        return new double[][]{
                {f / aspectRatio, 0, 0,                         0},
                {0,               f, 0,                         0},
                {0,               0, (far + near) * rangeInv,   2 * far * near * rangeInv},
                {0,               0, -1,                        0}
        };
    }

    public double[] projectPoint(double x, double y, double z) {
        double[][] viewMatrix = createViewMatrix();
        double[] worldPoint = {x, y, z, 1.0};
        double[] cameraPoint = multiplyMatrixVector(viewMatrix, worldPoint);

        double[] projected = multiplyMatrixVector(projectionMatrix, cameraPoint);

        if (projected[3] <= 0) return null; // Point behind camera

        projected[0] /= projected[3];
        projected[1] /= projected[3];

        return new double[]{projected[0], projected[1]};
    }

    private double[][] createViewMatrix() {
        double[][] rotX = createRotationMatrixX(-pitch);
        double[][] rotY = createRotationMatrixY(-yaw);
        double[][] rotZ = createRotationMatrixZ(-roll);

        double[][] rotation = multiplyMatrices(rotZ, multiplyMatrices(rotY, rotX));

        double[][] translation = {
                {1, 0, 0, -posX},
                {0, 1, 0, -posY},
                {0, 0, 1, -posZ},
                {0, 0, 0, 1}
        };

        return multiplyMatrices(rotation, translation);
    }

    private double[][] createRotationMatrixX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[][]{
                {1, 0,    0,   0},
                {0, cos, -sin, 0},
                {0, sin,  cos, 0},
                {0, 0,    0,   1}
        };
    }

    private double[][] createRotationMatrixY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[][]{
                {cos,  0, sin, 0},
                {0,    1, 0,   0},
                {-sin, 0, cos, 0},
                {0,    0, 0,   1}
        };
    }

    private double[][] createRotationMatrixZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[][]{
                {cos, -sin, 0, 0},
                {sin,  cos, 0, 0},
                {0,    0,   1, 0},
                {0,    0,   0, 1}
        };
    }

    private double[][] multiplyMatrices(double[][] a, double[][] b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    private double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        double[] result = new double[4];
        for (int i = 0; i < 4; i++) {
            result[i] = matrix[i][0] * vector[0] +
                    matrix[i][1] * vector[1] +
                    matrix[i][2] * vector[2] +
                    matrix[i][3] * vector[3];
        }
        return result;
    }

    // Movement methods (relative to camera orientation)
    public void moveForward(double distance) {
        posX -= Math.sin(yaw) * Math.cos(pitch) * distance;
        posY -= Math.sin(pitch) * distance; // <- fix: pitch nach oben = y runter
        posZ -= Math.cos(yaw) * Math.cos(pitch) * distance;
    }

    public void moveBackward(double distance) {
        moveForward(-distance);
    }

    public void strafeLeft(double distance) {
        posX -= Math.cos(yaw) * distance;
        posZ += Math.sin(yaw) * distance;
    }

    public void strafeRight(double distance) {
        strafeLeft(-distance);
    }

    public void moveUp(double distance) {
        posY += distance;
    }

    public void moveDown(double distance) {
        posY -= distance;
    }

    // Rotation methods
    public void rotateYaw(double angle) {
        yaw += angle;
    }
}
