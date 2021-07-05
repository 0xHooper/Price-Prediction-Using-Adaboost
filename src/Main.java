public class Main {

    public static void main(String[] args) {
        DataImporter dataImporter = new DataImporter("resources/ETHUSD4H.csv");
        DataPreparation dataPreparation = new DataPreparation(dataImporter, 1.15, 0.93);

        System.out.println("training...");
        Adaboost model = new Adaboost(dataPreparation.getTrainingSet(), 10, 18);

        DataTester dataTester = new DataTester(model, dataPreparation.getTestingSet());
        dataTester.printInfo(100);
    }
}