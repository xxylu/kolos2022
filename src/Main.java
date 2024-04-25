import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Country.setFiles("deaths.csv", "confirmed_cases.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        LocalDate date = LocalDate.parse("3/22/20", formatter);
        LocalDate date2 = LocalDate.parse("3/30/20", formatter);

        String[] countries ={"Australia","Yemen","Zimbabwe","Afghanistan"};
        List<Country> arr = Country.fromCsv(countries);
        Country.sortByDeath(arr,date,date2);
        for(Country i : arr){
            System.out.println(i.getName());
        }

//        Country country = Country.fromCsv("Australia");
//        System.out.println(country.getConfirmedCases(date));

//        Map<LocalDate, Pair> statistics = country.getListEntry(1).statistics;
//        for(LocalDate i : statistics.keySet() ){
//            System.out.println(i + " " +statistics.get(i).toString());
//        }
    }
}