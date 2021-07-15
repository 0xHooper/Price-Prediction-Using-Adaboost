import java.util.ArrayList;
import java.util.List;

public class Stump {
    private static final char OP_LESSER_THAN = '<';
    private static final char OP_GREATER_THAN = '>';
    private static final char[] OP_ARRAY = { OP_LESSER_THAN, OP_GREATER_THAN };

    private final double threshold;
    private final char operation;
    private double weightedError;
    private double alpha;

    private final int[] classifiedAs;

    private final List<String> columnNames;
    private final int columnIndex;

    private Stump(double threshold, char operation, int columnIndex, int samplesCount, List<String> columnNames) {
        this.threshold = threshold;
        this.operation = operation;
        this.columnIndex = columnIndex;
        this.classifiedAs = new int[samplesCount];
        this.columnNames = columnNames;
    }

    public static Stump findBestStump(List<PriceData> trainingSet, int numberOfSteps, double[] weights, List<String> columnNames) {
        int numberOfColumns = trainingSet.get(0).numbersData.size();
        Stump bestStump = new Stump(0,'0',0,0, columnNames);
        bestStump.weightedError = Double.MAX_VALUE;

        bestStump = countBestStumpForSet(trainingSet, numberOfSteps, weights, columnNames, numberOfColumns, bestStump);
        bestStump.alpha = 0.5* Math.log((1 - bestStump.getWeightedError())/bestStump.getWeightedError());

        return bestStump;
    }

    private static Stump countBestStumpForSet(List<PriceData> trainingSet, int numberOfSteps, double[] weights, List<String> columnNames, int numberOfColumns, Stump bestStump) {
        for (int i = 4; i < numberOfColumns; i++) {
            ColumnData columnData = getColumnData(trainingSet, i);
            double stepSize = (columnData.max - columnData.min)/ numberOfSteps;
            double currentThreshold = columnData.min + stepSize;
            bestStump = countBestStumpForColumn(trainingSet, weights, columnNames, bestStump, i, columnData, stepSize, currentThreshold);
        }
        return bestStump;
    }

    private static Stump countBestStumpForColumn(List<PriceData> trainingSet, double[] weights, List<String> columnNames, Stump bestStump, int i, ColumnData columnData, double stepSize, double currentThreshold) {
        for (; Double.compare(currentThreshold, columnData.max) < 0; currentThreshold += stepSize){
            for (char operation : OP_ARRAY) {
                bestStump = countStump(trainingSet, weights, columnNames, bestStump, i, columnData, currentThreshold, operation);
            }
        }
        return bestStump;
    }

    private static Stump countStump(List<PriceData> trainingSet, double[] weights, List<String> columnNames, Stump bestStump, int i, ColumnData columnData, double currentThreshold, char operation) {
        Stump currentStump = new Stump(currentThreshold, operation, i, trainingSet.size(), columnNames);
        currentStump.weightedError = currentStump.calculateColumnWeightedError(columnData, weights, trainingSet);
        if (Double.compare(bestStump.weightedError, currentStump.weightedError) == 1) {
            bestStump = currentStump;
        }
        return bestStump;
    }

    public int classify(double columnValue) {
        switch (operation) {
            case OP_LESSER_THAN:
                return Double.compare(columnValue, threshold) == -1 ? 1 : -1;
            case OP_GREATER_THAN:
                return Double.compare(columnValue, threshold) == 1 ? 1 : -1;
            default:
                throw new IllegalArgumentException("Illegal Operation Character");
        }
    }

    public int classify(List<Double> list){
        return classify(list.get(columnIndex));
    }

    private double calculateColumnWeightedError(ColumnData columnData, double[] weights, List<PriceData> trainingSet) {
        double totalError = 0;
        for (int i = 0; i < columnData.data.size(); i++) {
            Double singleData = columnData.data.get(i);
            totalError += calculateWeightedError(singleData, trainingSet.get(i).label, weights[i], i);
        }
        return totalError;
    }
    
    private double calculateWeightedError(double columnValue, double label, double weight, int index) {
        classifiedAs[index] = classify(columnValue);
        return classifiedAs[index] == label ? 0 : weight;
    }

    private static ColumnData getColumnData(List<PriceData> set, int columnIndex){
        List<Double> columnData = new ArrayList<>();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (PriceData singlePriceData : set){
            double singleData = singlePriceData.numbersData.get(columnIndex);
            min = Math.min(singleData, min);
            max = Math.max(singleData, max);
            columnData.add(singleData);
        }
        return new ColumnData(columnData, min, max);
    }

    public double getWeightedError() {
        return weightedError;
    }

    public int getClassifiedAs(int index) {
        return classifiedAs[index];
    }

    public double getAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        return "threshold= " + threshold +
                ", operation= " + operation +
                ", columnName= " + columnNames.get(columnIndex+1) +
                ", alpha= " + alpha;
    }

    private static class ColumnData {
        private final List<Double> data;
        private final double min;
        private final double max;

        private ColumnData(List<Double> data, double min, double max) {
            this.data = data;
            this.min = min;
            this.max = max;
        }
    }
}