package scraper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Scraper
{
    public static void main( String[] args ) throws Exception
    {
        final int topLimit = 100;
        final Map<String,String> currencies = Scraper.getTopCurrencyList(topLimit);

        XSSFWorkbook workbook = new XSSFWorkbook();

        try {
            for (Map.Entry<String,String> currency : currencies.entrySet()) {
                Document document = Jsoup.connect(String.format("https://coinmarketcap.com/currencies/%s/historical-data/?start=20130428&end=20171215", currency.getValue())).get();
                XSSFSheet sheet = workbook.createSheet(currency.getKey());

                int rowNumber = 0;
                for (Element record : document.select("tbody tr")) {
                    Row row = sheet.createRow(rowNumber++);
                    int columnNumber = 0;
                    for (Element data : record.select("td")) {
                        Cell cell = row.createCell(columnNumber++);
                        cell.setCellValue(data.text());
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("An error occurred while fetching historical data");
        }

        FileOutputStream outputStream = new FileOutputStream("scrapedData.xlsx");
        workbook.write(outputStream);
        workbook.close();

        System.out.println("Done");
    }

    protected static Map<String,String> getTopCurrencyList(int topLimit) throws Exception {
        try {
            Document document = Jsoup.connect("https://coinmarketcap.com/all/views/all/").get();

            Map<String,String> currencyList = new HashMap<String, String>();
            for (Element element : document.select("span.currency-symbol a")) {
                String urlPart = element.attr("href");
                currencyList.put(element.text(), urlPart.substring(12, urlPart.length() - 1));

                if (currencyList.size() == topLimit) {
                    break;
                }
            }

            return currencyList;
        } catch (Exception e) {
            throw new Exception("An error occurred while fetching top list");
        }
    }
}
