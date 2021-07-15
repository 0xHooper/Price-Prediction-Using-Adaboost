import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Adaboost {
    private final List<Stump> model;

    private final List<PriceData> trainingSet;
    private final int numberOfSteps;
    private final int maxIteration;
    private final List<String> columnNames;

    public Adaboost(List<PriceData> trainingSet, int numberOfSteps, int maxIteration, List<String> columnNames) {
        this.model = new ArrayList<>();
        this.trainingSet = trainingSet;
        this.numberOfSteps = numberOfSteps;
        this.maxIteration = maxIteration;
        this.columnNames = columnNames;

        train();
    }

    private void train() {
        int samplesCount = trainingSet.size();
        double[] weights = new double[samplesCount];
        Arrays.fill(weights, (1.0/samplesCount));

        for (int i = 0; i < maxIteration; i++) {
            Stump stump = Stump.bestStump(trainingSet, numberOfSteps, weights, columnNames);
            model.add(stump);

            weights = updateWeights(stump, weights);
            if (stump.getWeightedError() <= 0)
                break;
        }
    }

    public int classify(List<Double> observation){
        double sum = model.stream()
                .mapToDouble(classifier -> classifier.classify(observation) * classifier.getAlpha())
                .sum();
        return Double.compare(sum, 0) == 1 ? 1 : -1;
    }

    private double[] updateWeights(Stump stump, double[] weights) {
        IntStream.range(0, weights.length).forEach(i -> updateWeight(stump, weights, i));
        return normalizeWeights(weights);
    }

    private void updateWeight(Stump stump, double[] weights, int index) {
        if (stump.getClassifiedAs(index) == trainingSet.get(index).label) {
            weights[index] *= Math.pow(Math.E, (-stump.getAlpha()));
        } else {
            weights[index] *= Math.pow(Math.E, (stump.getAlpha()));
        }
    }

    private double[] normalizeWeights(double[] weights) {
        double total = Arrays.stream(weights).sum();
        IntStream.range(0, weights.length).forEach(i -> weights[i] /= total);
        return weights;
    }

    public List<Stump> getModel() {
        return model;
    }
}