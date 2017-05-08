package demo.crawler;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.ad.Ad;

import java.io.*;
import java.util.List;

/**
 * Created by Ling on 5/7/17.
 */
public class CrawlerMain {
    public static void main(String[] args) throws IOException {
        if(args.length < 2)
        {
            System.out.println("Usage: Crawler <rawQueryDataFilePath> <adsDataFilePath> <proxyFilePath>");
            System.exit(0);
        }
        String rawQueryDataFilePath = args[0];
        String adsDataFilePath = args[1];
        String proxyFilePath = args[2];

        ObjectMapper mapper = new ObjectMapper();
        AmazonCrawler crawler = new AmazonCrawler(proxyFilePath);

        File adsDataFile = new File(adsDataFilePath);
        // if file does not exists, then create it
        if (!adsDataFile.exists()) {
            adsDataFile.createNewFile();
        }

        FileWriter fileWriterForAds = new FileWriter(adsDataFile.getAbsoluteFile());
        BufferedWriter bufferedWriterForAds = new BufferedWriter(fileWriterForAds);
        try (BufferedReader bufferedReaderForRawQuery = new BufferedReader(new FileReader(rawQueryDataFilePath))) {

            String line;
            while ((line = bufferedReaderForRawQuery.readLine()) != null) {
                if(line.isEmpty())
                    continue;
                //System.out.println(line);
                String[] fields = line.split(",");
                String query = fields[0].trim();
                double bidPrice = Double.parseDouble(fields[1].trim());
                int campaignId = Integer.parseInt(fields[2].trim());
                int queryGroupId = Integer.parseInt(fields[3].trim());

                List<Ad> ads =  crawler.getAdBasicInfoByQuery(query, bidPrice, campaignId, queryGroupId);
                for(Ad ad : ads) {
                    String jsonInString = "= mapper.writeValueAsString(ad);";
                    //System.out.println(jsonInString);
                    bufferedWriterForAds.write(jsonInString);
                    bufferedWriterForAds.newLine();
                }
                Thread.sleep(2000);
            }
            bufferedWriterForAds.close();
        }catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}