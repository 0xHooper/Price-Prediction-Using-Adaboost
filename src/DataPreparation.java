import java.util.ArrayList;
import java.util.List;

public class DataPreparation {
    List<PriceData> trainingSet = new ArrayList<>();
    List<PriceData> testingSet = new ArrayList<>();

    public DataPreparation(DataImporter dataImporter, double reward, double risk){
        labelData(dataImporter, reward, risk);
        prepareDataForClassification(dataImporter);

        int numberOfNotTradeWeWant = 360;
        int numberOfMakeTradeWeWant = 360;

        splitDataIntoSets(dataImporter, numberOfNotTradeWeWant, numberOfMakeTradeWeWant);
    }

    private void splitDataIntoSets(DataImporter dataImporter, int numberOfNotTradeWeWant, int numberOfMakeTradeWeWant) {
        for (int i = 0; i< dataImporter.priceData.size(); i++) {
            if (i==0)
                System.out.println("Training test start date " + dataImporter.priceData.get(i).date);
            PriceData h = dataImporter.priceData.get(i);
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
                System.out.println("Testing test start date " + h.date);
            if (numberOfMakeTradeWeWant == 0 && numberOfNotTradeWeWant == 0) {
                testingSet.add(h);
            }
        }
    }

    private void labelData(DataImporter dataImporter, double reward, double risk) {
        for (int i = 0; i < dataImporter.priceData.size(); i++){
            int j = i;
            double currentOpen = dataImporter.priceData.get(i).numbersData.get(0);
            boolean hitHigh = dataImporter.priceData.get(j).numbersData.get(1) >= currentOpen * reward;
            boolean hitLow = dataImporter.priceData.get(j).numbersData.get(2) <= currentOpen * risk;
            while (!hitHigh && !hitLow && j< dataImporter.priceData.size()-1){
                j++;
                hitHigh = dataImporter.priceData.get(j).numbersData.get(1) >= currentOpen* reward;
                hitLow = dataImporter.priceData.get(j).numbersData.get(2) <= currentOpen* risk;
            }
            // removing data rows, that cannot be labeled
            if (!hitHigh && !hitLow){
                removeUnlabeledData(dataImporter, i);
                break;
            }
            // setting label
            else if (hitHigh)
                dataImporter.priceData.get(i).label = 1.0;
            else
                dataImporter.priceData.get(i).label = -1.0;
        }
    }

    private void removeUnlabeledData(DataImporter dataImporter, int startIndex) {
        int dataToRemove = dataImporter.priceData.size() - startIndex;
        while(dataToRemove > 0) {
            dataImporter.priceData.remove(dataImporter.priceData.size() - 1);
            dataToRemove--;
        }
    }

    private void prepareDataForClassification(DataImporter dataImporter) {
        /*
        As algorithm use only RSI and MACD indicators, normalization can be skipped
         */
        for (int i = dataImporter.priceData.size()-1;i>0;i--){
            for (int j = 4;j<dataImporter.priceData.get(i).numbersData.size();j++){
                dataImporter.priceData.get(i).numbersData.set(j, dataImporter.priceData.get(i-1).numbersData.get(j));

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
