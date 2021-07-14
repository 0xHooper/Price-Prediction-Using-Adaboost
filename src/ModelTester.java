import java.util.ArrayList;
import java.util.List;

public class ModelTester {
    private final Adaboost model;
    private final List<PriceData> testingSet;

    private int truePositive = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0;
    private int maxTrueInARow = 0, maxFalseInARow = 0;
    private final List<Boolean> makeTradeResults = new ArrayList<>();

    public ModelTester(Adaboost model, List<PriceData> testingSet) {
        this.model = model;
        this.testingSet = testingSet;
        test();
    }

    private void test(){
        printModelInfo();
        countResults();
        countMaxInARow();
    }

    private void printModelInfo() {
        model.getModel().forEach(e -> System.out.println(e.toString()));
    }

    private void countResults() {
        for (PriceData currentRow : testingSet){
            int classifiedAs = model.classify(currentRow.numbersData);
            if (classifiedAs == 1)
                if (classifiedAs == currentRow.label) {
                    truePositive++;
                    makeTradeResults.add(true);
                }
                else {
                    falsePositive++;
                    makeTradeResults.add(false);
                }
            if (classifiedAs == -1)
                if(classifiedAs == currentRow.label)
                    trueNegative++;
                else
                    falseNegative++;
        }
    }

    private void countMaxInARow() {
        int currentInARow = 0;
        boolean previous = !makeTradeResults.get(0);
        for (boolean singleResult : makeTradeResults){
            if (singleResult == previous){
                currentInARow++;
            } else
                currentInARow = 1;
            if (singleResult){
                if (maxTrueInARow < currentInARow)
                    maxTrueInARow = currentInARow;
            } else {
                if (maxFalseInARow < currentInARow)
                    maxFalseInARow = currentInARow;
            }
            previous = singleResult;
        }
    }

    public void printTestInfo(double positionSize){
        System.out.println("Profitable trade/all trades " + truePositive + "/" + (truePositive+falsePositive));
        System.out.println("Precision " + ((double)truePositive/(truePositive+falsePositive)));
        System.out.println("Max wins in a row: " + maxTrueInARow);
        System.out.println("Max loss in a row: " + maxFalseInARow);
        System.out.println("Potential profit " + (truePositive*0.15 - falsePositive*0.07) *positionSize);
    }
}
