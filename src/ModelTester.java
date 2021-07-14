import java.util.ArrayList;
import java.util.List;

public class ModelTester {
    int truePositive = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0;
    int maxTrueInARow = 0;
    int maxFalseInARow = 0;

    public ModelTester(Adaboost model, List<PriceData> testingSet) {
        test(model, testingSet);
    }

    private void test(Adaboost adaboost, List<PriceData> testingSet){
        adaboost.getModel().forEach(e -> System.out.println(e.toString()));
        testing(adaboost, testingSet);
    }

    private void testing(Adaboost adaboost,List<PriceData> testingSet){
        List<Boolean> list = new ArrayList<>();
        countResults(adaboost, testingSet, list);
        countMaxInARow(list);
    }

    private void countResults(Adaboost adaboost, List<PriceData> testingSet, List<Boolean> list) {
        for (PriceData p : testingSet){
            int classifiedAs = adaboost.classify(p.numbersData);
            if (classifiedAs == 1)
                if (classifiedAs == p.label) {
                    truePositive++;
                    list.add(true);
                }
                else {
                    falsePositive++;
                    list.add(false);
                }
            if (classifiedAs == -1)
                if(classifiedAs == p.label)
                    trueNegative++;
                else
                    falseNegative++;
        }
    }

    private void countMaxInARow(List<Boolean> list) {
        int currentInARow = 0;
        boolean previous = false;
        for (boolean b : list){
            if (b == previous){
                currentInARow++;
            } else
                currentInARow = 1;
            if (b){
                if (maxTrueInARow < currentInARow)
                    maxTrueInARow = currentInARow;
            } else {
                if (maxFalseInARow < currentInARow)
                    maxFalseInARow = currentInARow;
            }
            previous = b;
        }
    }

    public void printInfo(double positionSize){
        System.out.println("Profitable trade/all trades " + truePositive + "/" + (truePositive+falsePositive));
        System.out.println("Precision " + ((double)truePositive/(truePositive+falsePositive)));
        System.out.println("Max wins in a row: " + maxTrueInARow);
        System.out.println("Max loss in a row: " + maxFalseInARow);
        System.out.println("Potential profit " + (truePositive*0.15 - falsePositive*0.07)*positionSize);
    }
}
