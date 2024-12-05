package Service;

import java.util.Random;

public class MatrixFactorizationModel {
    private int numUsers;
    private int numItems;
    private int numFactors;
    private double[][] userFactors;
    private double[][] itemFactors;

    public MatrixFactorizationModel(int numUsers, int numItems, int numFactors) {
        this.numUsers = numUsers;
        this.numItems = numItems;
        this.numFactors = numFactors;
        this.userFactors = new double[numUsers][numFactors];
        this.itemFactors = new double[numItems][numFactors];
        initializeFactors();
    }

    private void initializeFactors() {
        Random rand = new Random();
        for (int u = 0; u < numUsers; u++) {
            for (int f = 0; f < numFactors; f++) {
                userFactors[u][f] = rand.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < numItems; i++) {
            for (int f = 0; f < numFactors; f++) {
                itemFactors[i][f] = rand.nextGaussian() * 0.01;
            }
        }
    }

    public double predict(int user, int item) {
        if (user < 0 || user >= numUsers || item < 0 || item >= numItems) {
            throw new IllegalArgumentException("User or item index out of bounds");
        }
        double prediction = 0.0;
        for (int f = 0; f < numFactors; f++) {
            prediction += userFactors[user][f] * itemFactors[item][f];
        }
        return prediction;
    }

    public double[][] getUserFactors() {
        return userFactors;
    }

    public double[][] getItemFactors() {
        return itemFactors;
    }
}
