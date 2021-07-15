import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Adaboost {
    private List<Stump> model;

    public Adaboost(List<PriceData> trainingSet, int numberOfSteps, int maxIteration, List<String> columnNames) {
        train(trainingSet, numberOfSteps, maxIteration, columnNames);
    }

    private void train(List<PriceData> trainingSet, int numberOfSteps, int maxIteration, List<String> columnNames) {
        int samplesCount = trainingSet.size();
        double[] weights = new double[samplesCount];
        Arrays.fill(weights, ((double) 1 / samplesCount));

        List<Stump> model = new ArrayList<>();
        for (int i = 0; i < maxIteration; i++) {
            Stump stump = Stump.bestStump(trainingSet, numberOfSteps, weights, columnNames);
            model.add(stump);

            weights = updateWeights(trainingSet, stump, weights);
            if (stump.getWeightedError() <= 0)
                break;
        }
        this.model = model;
    }

    public int classify(List<Double> observation){
        double sum = model.stream()
                .mapToDouble(classifier -> classifier.classify(observation) * classifier.alpha)
                .sum();
        if (Double.compare(sum, 0) == 1)
            return 1;
        return -1;
    }

    private double[] updateWeights(List<PriceData> trainingSet, Stump stump, double[] weights) {
        IntStream.range(0, weights.length).forEach(current -> updateWeight(trainingSet, stump, weights, current));
        return normalizeWeights(weights);
    }

    private void updateWeight(List<PriceData> trainingSet, Stump stump, double[] weights, int currentIndex) {
        if (stump.getClassEst()[currentIndex] == trainingSet.get(currentIndex).label) {
            weights[currentIndex] *= Math.pow(Math.E, (-stump.alpha));
        } else {
            weights[currentIndex] *= Math.pow(Math.E, (stump.alpha));
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