import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CountryWithProvinces extends Country {
    private List<Country> provinces;
    public CountryWithProvinces(String name, List<Country> provinces){
        super(name);
        this.provinces = provinces;
    }

    public CountryWithoutProvinces getListEntry(int index){
        return (CountryWithoutProvinces) this.provinces.get(index);
    }
    @Override
    int getConfirmedCases(LocalDate date){
        int sum = 0;
        for(Country i:provinces){
            sum += i.getConfirmedCases(date);
        }
        return sum;
    }
    @Override
    int getDeaths(LocalDate date){
        int sum = 0;
        for(Country i:provinces){
            sum += i.getDeaths(date);
        }
        return sum;
    }
}