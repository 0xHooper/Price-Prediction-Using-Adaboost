import java.util.ArrayList;
import java.util.List;

public class Stump {
    public static final char OP_LESSER_THAN = '<';
    public static final char OP_GREATER_THAN = '>';
    public static final char[] OP_ARRAY = { OP_LESSER_THAN, OP_GREATER_THAN };
    private final double threshold;
    private final char operation;
    private final int columnIndex;
    private double weightedError;
    double alpha;
    private final int[] classEst;

    private Stump(double threshold, char operation, int columnIndex, int samplesCount) {
        this.threshold = threshold;
        this.operation = operation;
        this.columnIndex = columnIndex;
        this.classEst = new int[samplesCount];
    }

    public static Stump bestStump(List<PriceData> trainingSet, int numberOfSteps, double[] weights) {
        int columnsCount = trainingSet.get(0).numbersData.size(); // =8
        Stump bestStump = new Stump(0,'0',0,0);
        double minError = Double.MAX_VALUE;

        for (int i = 4; i < columnsCount; i++) {
            ColumnData columnData = getColumnData(trainingSet, i);
            double stepSize = (columnData.max - columnData.min)/numberOfSteps;
            double currentThreshold = columnData.min + stepSize;
            for (;Double.compare(currentThreshold, columnData.max) < 0; currentThreshold += stepSize){
                for (char operation : OP_ARRAY) {
                    Stump currentStump = new Stump(currentThreshold, operation, i, trainingSet.size());
                    currentStump.weightedError = currentStump.calculateColumnWeightedError(columnData, weights, trainingSet);
                    if (Double.compare(minError, currentStump.weightedError) == 1) {
                        minError = currentStump.weightedError;
                        bestStump = currentStump;
                    }
                }
            }
        }
        bestStump.alpha = 0.5* Math.log((1 - bestStump.getWeightedError())/bestStump.getWeightedError());
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
        int stumpLabel = classify(columnValue);
        classEst[index] = stumpLabel;
        return stumpLabel == label ? 0 : weight;
    }

    public static ColumnData getColumnData(List<PriceData> set, int columnIndex){
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

    public int[] getClassEst() {
        return classEst;
    }

    @Override
    public String toString() {
        return "threshold=" + threshold +
                ", operation=" + operation +
                ", columnIndex=" + columnIndex +
                ", alpha=" + alpha;
    }

    static class ColumnData {
        private final List<Double> data;
        private final double min;
        private final double max;

        public ColumnData(List<Double> data, double min, double max) {
            this.data = data;
            this.min = min;
            this.max = max;
        }
    }
}