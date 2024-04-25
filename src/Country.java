import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.swap;


public abstract class Country {
    private final String name;
    private static String deathsFile;
    private static String casesFile;

    public Country(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    private static class CountryColumns{
        public final int firstColumnIndex;
        public final int columnCount;
        public CountryColumns(int index, int count){
            this.firstColumnIndex = index;
            this.columnCount = count;
        }
    }

    private static CountryColumns getCountryColumns(String line, String countryName) throws CountryNotFoundException{
        long countCountry = Arrays.stream(line.split(";",-1)).filter(str -> str.equals(countryName)).count();
        if(countCountry == 0){
            throw new CountryNotFoundException(countryName);
        }
        return new CountryColumns(
                Arrays.stream(line.split(";",-1)).toList().indexOf(countryName),
                (int)countCountry
        );
    }
    public static void setFiles(String deathsFile, String casesFile)  throws FileNotFoundException{
        if(!(new File(deathsFile).isFile() || new File(deathsFile).canRead())){
            throw new FileNotFoundException(deathsFile);
        }
        if(!(new File(casesFile).isFile() || new File(casesFile).canRead())){
            throw new FileNotFoundException(casesFile);
        }
        Country.deathsFile = deathsFile;
        Country.casesFile = casesFile;
    }
    public static Country fromCsv(String countryName){
        try(
                BufferedReader bufferedReaderDeaths = new BufferedReader(new FileReader(deathsFile));
                BufferedReader bufferedReaderCases = new BufferedReader(new FileReader(casesFile));
        ){
            CountryColumns columns =  getCountryColumns(bufferedReaderDeaths.readLine(), countryName);
            bufferedReaderCases.readLine();
            bufferedReaderCases.readLine();
            bufferedReaderDeaths.readLine();
            if(columns == null){
                throw new CountryNotFoundException(countryName);
            }

            if(columns.columnCount == 1){
                CountryWithoutProvinces countryWithoutProvinces = new CountryWithoutProvinces(countryName);
                while(true) {
                    String line1 =bufferedReaderCases.readLine();
                    String line2 =bufferedReaderDeaths.readLine();
                    if(line1 == null || line2 == null) break;
                    List<String> cases = Arrays.stream(line1.split(";", -1)).toList();
                    List<String> deaths = Arrays.stream(line2.split(";", -1)).toList();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
                    LocalDate date = LocalDate.parse(cases.getFirst(), formatter);
                    int currentCases = Integer.parseInt(cases.get(columns.firstColumnIndex));
                    int currentDeaths = Integer.parseInt(deaths.get(columns.firstColumnIndex));
                    countryWithoutProvinces.addDailyStatistics(date, currentDeaths, currentCases);
                }
                return countryWithoutProvinces;
            }
            else{
                List<Country> provinces = new ArrayList<>();
                for(int i = 0; i < columns.columnCount; i++){
                    provinces.add(new CountryWithoutProvinces(countryName));
                }
                Country countryWithProvinces;
                countryWithProvinces = new CountryWithProvinces(countryName,provinces);
                while(true) {
                    String line1 =bufferedReaderCases.readLine();
                    String line2 =bufferedReaderDeaths.readLine();
                    if(line1 == null || line2 == null) break;
                    List<String> cases = Arrays.stream(line1.split(";", -1)).toList();
                    List<String> deaths = Arrays.stream(line2.split(";", -1)).toList();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
                    LocalDate date = LocalDate.parse(cases.getFirst(), formatter);
                    for(int i = 0 ; i < columns.columnCount; i++) {
                        int currentCases = Integer.parseInt(cases.get(columns.firstColumnIndex+i));
                        int currentDeaths = Integer.parseInt(deaths.get(columns.firstColumnIndex+i));
                        ((CountryWithoutProvinces)provinces.get(i)).addDailyStatistics(date, currentDeaths, currentCases);
                    }

                }
                return countryWithProvinces;
            }
        } catch(IOException a){
            System.err.println(a.getMessage());
        }catch(CountryNotFoundException e){
            System.err.println(new CountryNotFoundException(countryName).getMessage());
        }
        return null;
    }

    public static List<Country> fromCsv(String[] countries){
        List<Country> returnCountries = new ArrayList<>();
        for(String i: countries){
            Country country = Country.fromCsv(i);
            if(country != null)
                returnCountries.add(country);
        }
        return returnCountries;
    }

    abstract int getConfirmedCases(LocalDate date);
    abstract int getDeaths(LocalDate date);

    public static int getDeathsFromSpan(Country country,LocalDate startDate, LocalDate endDate){
        int sum = 0;
        for(int i = 0; startDate.plusDays(i).isBefore(endDate.plusDays(1));i++){
            sum += country.getDeaths(startDate.plusDays(i));
        }
        return sum;
    }
    public static void sortByDeath(List<Country> countries, LocalDate startDate, LocalDate endDate){
        //trash code begins, works at least
        ArrayList<Integer> arr1 = new ArrayList<>();
        List<Integer> arr = countries.stream()
                .map(o -> Country.getDeathsFromSpan(o,startDate,endDate))
                .toList();
        arr1.addAll(arr);
        int counter = 0;
        for(Integer i : arr1){
            counter+=1;
        }
        System.out.println(arr1);
        for(int i = 0; i < counter; i++){
            for(int j = i; j < counter; j++){
                if(arr.get(i) < arr.get(j)){
                    swap(arr1,i,j);
                    swap(countries,i,j);
                }
            }
        }
    }

}