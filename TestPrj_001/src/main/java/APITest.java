import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

public class APITest {

    public static void APITesting() throws FileNotFoundException {

        String strLog_RequestBody = "";
        String strlog_ResposnseBody = "";
        String strlog_APIUrl = "";

        try {
            //Get URL Parameter
            String strAuthURL = "http://apiproxy.paytm.com/v2/movies/upcoming";
            strlog_APIUrl = strAuthURL;
            Response responseAuthenticate = given()
                    .contentType(ContentType.JSON)
                    .body(strLog_RequestBody)
                    .get(strlog_APIUrl);
            String strResponseBody = responseAuthenticate.getBody().asString();
            strlog_ResposnseBody = strResponseBody;
            Headers hdrResponseHeaders = responseAuthenticate.headers();
            System.out.println("strResponseBody : " + strResponseBody);
            JsonParser parser = new JsonParser();
            JsonElement responseBodyAsJson = parser.parse(strResponseBody);
            int iResponseStatus = responseAuthenticate.getStatusCode();
            System.out.println("API iResponseStatus: " + iResponseStatus);
            if (iResponseStatus == 200) {
                System.out.println("Status code is 200 - Passed :"+strlog_ResposnseBody);
            } else {
                System.out.println("Status code is not 200 - Failed");
            }

            int arraySize = responseBodyAsJson.getAsJsonObject().get("upcomingMovieData").getAsJsonArray().size();
            System.out.println("ArraySize: "+arraySize);

            int rownum = 1;
            FileInputStream file = new FileInputStream("Book3.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row = sheet.getRow(0);
            Map<String, Integer> map = new HashMap<String,Integer>();
            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for(short colIx=minColIx; colIx<maxColIx; colIx++) {
                XSSFCell cell = row.getCell(colIx);
                map.put(cell.getStringCellValue(),cell.getColumnIndex());
            }
            file.close();
            FileOutputStream outFile = new FileOutputStream("AllData.xlsx");
            workbook.write(outFile);
            outFile.close();

            for (int i = 0; i < arraySize; i++) {
                int cellnum = 0;
                List<String> valuesList = new ArrayList<String>();
                JsonObject currentDetails = responseBodyAsJson.getAsJsonObject().get("upcomingMovieData").getAsJsonArray().get(i).getAsJsonObject();
                Set<?> s = currentDetails.keySet();
                Iterator<?> itr = s.iterator();
                LinkedHashMap<String, String> dataMapJSON = new LinkedHashMap<String, String>();
                do {
                    String key = itr.next().toString();
                    //System.out.println(key);
                    JsonElement value = currentDetails.get(key);
                    //System.out.println("Value: "+value);
                    String strValue = String.valueOf(value);
                    //System.out.println("Value: "+strValue);
                    if (strValue.startsWith("[")) {
                        dataMapJSON.put(key, strValue);
                    } else {
                        if(value.isJsonNull()){dataMapJSON.put(key, "null");}
                        else{
                        dataMapJSON.put(key, value.getAsString());}
                    }
                    //Write All data to excel
                    cellnum = writeExcelData(key, "AllData.xlsx", i + 1, map.get(key), dataMapJSON);
                    cellnum++;

                } while (itr.hasNext());
                //System.out.println(dAData);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if(dataMapJSON.get("releaseDate").equals("null")){
                    System.out.println(dataMapJSON.get("movie_name")+" have release date as NULL "+" Status: FAIL");
                } else{
                Date movieReleaseDate = simpleDateFormat.parse(dataMapJSON.get("releaseDate"));
                Date now = new Date();
                if(now.before(movieReleaseDate)){
                    System.out.println(dataMapJSON.get("movie_name")+" has future release date: "+movieReleaseDate+" Status: PASS");
                } else {
                    System.out.println(dataMapJSON.get("movie_name")+" does not have future release date: "+movieReleaseDate+" Status: FAIL");
                }
                }

                String moviePosterURL = dataMapJSON.get("moviePosterUrl");
                if(moviePosterURL.contains(".jpg")){
                    System.out.println(dataMapJSON.get("movie_name")+" has poster URL format .jpg"+" Status: PASS");
                } else {
                    System.out.println(dataMapJSON.get("movie_name")+" does not have poster URL format .jpg"+" Status: FAIL");
                }

                String movieLanguage = dataMapJSON.get("language");
                if(movieLanguage.contains(",")){
                    System.out.println(dataMapJSON.get("movie_name")+" has more than one language"+" Status: FAIL");
                } else{
                    System.out.println(dataMapJSON.get("movie_name")+" does not have more than one language"+" Status: PASS");
                }

                String uniqueCode = dataMapJSON.get("paytmMovieCode");
                if(valuesList.size()!=0&&valuesList.contains(uniqueCode)){
                    System.out.println(dataMapJSON.get("movie_name")+" has duplicate movie code"+" Status: FAIL");
                } else {
                    System.out.println(dataMapJSON.get("movie_name")+" has unique movie code"+" Status: PASS");
                    valuesList.add(dataMapJSON.get("paytmMovieCode"));
                }

                if(dataMapJSON.get("isContentAvailable").equals("0")){
                    cellnum = writeExcelData("movie_name", "ContentNotAvailble.xlsx", rownum, 0,dataMapJSON );
                    rownum++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int writeExcelData(String key, String s2, int i2, Integer integer, LinkedHashMap<String, String> dataMapJSON) throws IOException {
        FileInputStream file;
        XSSFWorkbook workbook;
        XSSFSheet sheet;
        XSSFRow row;
        int cellnum;
        FileOutputStream outFile;
        file = new FileInputStream(s2);
        workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheetAt(0);
        row = sheet.getRow(i2);
        if (row == null) {
            row = sheet.createRow(i2);
        }
        cellnum = integer;
        Cell cell = row.getCell(cellnum);
        if (cell == null) {
            cell = row.createCell(cellnum);
        }
        cell.setCellValue(dataMapJSON.get(key));
        file.close();
        outFile = new FileOutputStream(s2);
        workbook.write(outFile);
        outFile.close();
        return cellnum;
    }

}
