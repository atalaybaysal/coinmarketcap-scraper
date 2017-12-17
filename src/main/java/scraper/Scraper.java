package scraper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;

public class Scraper
{
    public static void main( String[] args ) throws IOException
    {
        XSSFWorkbook workbook = new XSSFWorkbook();

        final String[] cryptoCurrencies = {"bitcoin", "ethereum"};

        for (int i = 0; cryptoCurrencies.length > i; i++) {

            try {
                Document document = Jsoup.connect(String.format("https://coinmarketcap.com/currencies/%s/historical-data/?start=20130428&end=20171215", cryptoCurrencies[i])).get();
                XSSFSheet sheet = workbook.createSheet(cryptoCurrencies[i]);

                int rowNumber = 0;
                for (Element record : document.select("tbody tr")) {
                    Row row = sheet.createRow(rowNumber++);
                    int columnNumber = 0;
                    for (Element data : record.select("td")) {
                        Cell cell = row.createCell(columnNumber++);
                        cell.setCellValue(data.text());
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }

        FileOutputStream outputStream = new FileOutputStream("scrapedData.xlsx");
        workbook.write(outputStream);
        workbook.close();

        System.out.println("Done");
    }
}
