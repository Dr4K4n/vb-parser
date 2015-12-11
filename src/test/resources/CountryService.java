import System.Collections.Generic;
import InsuranceDB.DataAccess;

public class CountryService {

    public IGetData myGetData; 

    public Country findById(Integer countryId) {
        DataSet dataSet  = myGetData.GetOneDataSet("SELECT * FROM tblCountry WHERE id_country = " + countryId)

        if (dataSet.Tables(0).Rows.Count <> 1) {
            throw new BalticException("No or multiple countries found with id " + countryId)
        }

        return ConvertDataRowToCountry(dataSet.Tables(0).Rows(0))
    }

    public List<Country> findAll() {
        List<Country> result = new ArrayList<>();
        DataSet dataSet = myGetData.GetOneDataSet("SELECT * FROM tblCountry ORDER BY countryname")
        For Each dataRow As DataRow In dataSet.Tables(0).Rows {
            result.Add(ConvertDataRowToCountry(dataRow));
        }
        Return result;
    }

    private Country convertDataRowToCountry(DataRow dataRow) {
        Country result = new Country();
        result.id = CInt(dataRow.Item("id_country"))
        result.name = CStr(dataRow.Item("countryname"))
        result.code = CStr(dataRow.Item("countrycode"))
        result.citizen = CStr(dataRow.Item("citizen"))
        result.isoCode = CStr(dataRow.Item("isocountrycode"))
        return result;
    }

}
