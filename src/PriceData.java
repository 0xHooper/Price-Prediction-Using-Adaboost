import java.util.List;
import java.util.StringJoiner;

public class PriceData {
    String date;
    List<Double> numbersData;
    double label;

    PriceData(String date, List<Double> numbersData){
        this.date = date;
        this.numbersData = numbersData;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("|");
        numbersData.forEach(e -> sj.add(e.toString()));
        return sj.toString() + "|" +
                label;
    }
}
