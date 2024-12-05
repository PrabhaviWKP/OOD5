package Service;

public class ALS {
    private MatrixFactorizationModel model;
    private int numUsers;
    private int numItems;
    private int numFactors;
    private double learningRate;
    private double regularization;
    private int numIterations;

    public ALS(int numUsers, int numItems, int numFactors, double learningRate, double regularization, int numIterations) {
        this.numUsers = numUsers;
        this.numItems = numItems;
        this.numFactors = numFactors;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.numIterations = numIterations;
        this.model = new MatrixFactorizationModel(numUsers, numItems, numFactors);
    }

    public void train(int[][] ratings) {
        for (int iter = 0; iter < numIterations; iter++) {
            // Update user factors
            for (int u = 0; u < numUsers; u++) {
                for (int f = 0; f < numFactors; f++) {
                    double sum = 0.0;
                    double regSum = 0.0;
                    for (int i = 0; i < numItems; i++) {
                        if (ratings[u][i] > 0) {
                            sum += (model.predict(u, i) - ratings[u][i]) * model.getItemFactors()[i][f];
                            regSum += model.getItemFactors()[i][f] * model.getItemFactors()[i][f];
                        }
                    }
                    model.getUserFactors()[u][f] -= learningRate * (sum + regularization * model.getUserFactors()[u][f]);
                }
            }

            // Update item factors
            for (int i = 0; i < numItems; i++) {
                for (int f = 0; f < numFactors; f++) {
                    double sum = 0.0;
                    double regSum = 0.0;
                    for (int u = 0; u < numUsers; u++) {
                        if (ratings[u][i] > 0) {
                            sum += (model.predict(u, i) - ratings[u][i]) * model.getUserFactors()[u][f];
                            regSum += model.getUserFactors()[u][f] * model.getUserFactors()[u][f];
                        }
                    }
                    model.getItemFactors()[i][f] -= learningRate * (sum + regularization * model.getItemFactors()[i][f]);
                }
            }
        }
    }

    public MatrixFactorizationModel getModel() {
        return model;
    }
}
