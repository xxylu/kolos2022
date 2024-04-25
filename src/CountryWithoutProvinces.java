import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CountryWithoutProvinces extends Country {
    Map<LocalDate, Pair> statistics = new HashMap<>();
    public CountryWithoutProvinces(String name){
        super(name);
    }
    public void addDailyStatistics(LocalDate date, int deaths, int cases){
        this.statistics.put(date,new Pair(deaths,cases));
    }
    @Override
    int getConfirmedCases(LocalDate date){
        return statistics.get(date).val2;
    }
    @Override
    int getDeaths(LocalDate date){
        return statistics.get(date).val1;
    }
}