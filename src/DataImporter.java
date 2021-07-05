import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.IntStream;

public class DataImporter {
    List<PriceData> priceData;
    List<String> columnNames = new ArrayList<>();

    DataImporter(String file){
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            priceData = getDataFromCSVFile(scanner);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    private List<PriceData> getDataFromCSVFile(Scanner scanner) {
        List<PriceData> data = new LinkedList<>();
        getColumnNames(scanner);
        /*
        skipping first 600 rows to get data selected manually
         */
        IntStream.range(0, 600).forEach(i -> scanner.nextLine());
        data.add(readDataFromLine(scanner));
        while(scanner.hasNext()){
            data.add(readDataFromLine(scanner));
        }
        return data;
    }

    private void getColumnNames(Scanner scanner) {
        String columnNamesToSplit = scanner.nextLine();
        String[] columnNames = columnNamesToSplit.split(",");
        this.columnNames.addAll(Arrays.asList(columnNames));
    }

    private PriceData readDataFromLine(Scanner scanner) {
        String[] lineData = scanner.nextLine().split(",");
        String data = lineData[0];
        List<Double> numbers = new ArrayList<>();
        for (int i = 1;i< lineData.length;i++){
            numbers.add(Double.parseDouble(lineData[i]));
        }
        return new PriceData(data, numbers);
    }
}
