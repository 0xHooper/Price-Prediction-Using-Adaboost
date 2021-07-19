import java.util.ArrayList;
import java.util.List;

public class DataPreparation {
    private final List<PriceData> trainingSet = new ArrayList<>();
    private final List<PriceData> testingSet = new ArrayList<>();

    private final List<PriceData> priceData;

    public DataPreparation(List<PriceData> priceData, double reward, double risk){
        this.priceData = priceData;

        labelData(reward, risk);
        prepareDataForClassification();

        int numberOfNotTradeWeWant = 360;
        int numberOfMakeTradeWeWant = 360;

        splitDataIntoSets(numberOfNotTradeWeWant, numberOfMakeTradeWeWant);
    }

    private void splitDataIntoSets(int numberOfNotTradeWeWant, int numberOfMakeTradeWeWant) {
        for (int i = 0; i< priceData.size(); i++) {
            if (i==0)
                System.out.println("Training set start date " + priceData.get(i).date);
            PriceData h = priceData.get(i);
            if (h.label == 1) {
                if (numberOfMakeTradeWeWant > 0) {
                    trainingSet.add(h);
                    numberOfMakeTradeWeWant--;
                }
            } else if (numberOfNotTradeWeWant > 0) {
                trainingSet.add(h);
                numberOfNotTradeWeWant--;
            }
            if (numberOfMakeTradeWeWant == 0 && numberOfNotTradeWeWant == 0 && testingSet.isEmpty())
                System.out.println("Testing set start date " + h.date);
            if (numberOfMakeTradeWeWant == 0 && numberOfNotTradeWeWant == 0) {
                testingSet.add(h);
            }
        }
    }

    private void labelData(double reward, double risk) {
        for (int i = 0; i < priceData.size(); i++){
            int j = i;
            double currentOpen = priceData.get(i).numbersData.get(0);
            boolean hitHigh = priceData.get(j).numbersData.get(1) >= currentOpen * reward;
            boolean hitLow = priceData.get(j).numbersData.get(2) <= currentOpen * risk;
            while (!hitHigh && !hitLow && j< priceData.size()-1){
                j++;
                hitHigh = priceData.get(j).numbersData.get(1) >= currentOpen* reward;
                hitLow = priceData.get(j).numbersData.get(2) <= currentOpen* risk;
            }
            // removing data rows, that cannot be labeled
            if (!hitHigh && !hitLow){
                removeUnlabeledData(i);
                break;
            }
            // setting label
            else if (hitHigh)
                priceData.get(i).label = 1.0;
            else
                priceData.get(i).label = -1.0;
        }
    }

    private void removeUnlabeledData(int startIndex) {
        int dataToRemove = priceData.size() - startIndex;
        while(dataToRemove > 0) {
            priceData.remove(priceData.size() - 1);
            dataToRemove--;
        }
    }

    private void prepareDataForClassification() {
        /*
        As algorithm use only RSI and MACD indicators, normalization can be skipped
         */
        for (int i = priceData.size()-1;i>0;i--){
            for (int j = 4;j<priceData.get(i).numbersData.size();j++){
                priceData.get(i).numbersData.set(j, priceData.get(i-1).numbersData.get(j));
            }
        }
        System.out.println("Data prepared for classification");
    }

    public List<PriceData> getTestingSet() {
        return testingSet;
    }

    public List<PriceData> getTrainingSet(){
        return trainingSet;
    }
}
