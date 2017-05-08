package demo.crawler;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.ad.Ad;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ling on 5/7/17.
 */
public class CrawlerMain {
    static final String rawQueryDataFilePath = "/home/vagrant/Desktop/rawQuery.txt";
    static final String adsDataFilePath = "/home/vagrant/Desktop/output.txt";
    static final String proxyFilePath = "/home/vagrant/Desktop/proxylist.csv";
    public static void main(String[] args) throws IOException {
//        if(args.length < 3)
//        {
//            System.out.println("Usage: Crawler <rawQueryDataFilePath> <adsDataFilePath> <proxyFilePath>");
//            System.exit(0);
//        }
//        String rawQueryDataFilePath = args[0];
//        String adsDataFilePath = args[1];
//        String proxyFilePath = args[2];

        ObjectMapper mapper = new ObjectMapper();
        AmazonCrawler crawler = new AmazonCrawler(proxyFilePath);
        final HashSet<String> querySearched = new HashSet<>();

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

                System.out.println("searching: " + query);
                List<Ad> ads =  crawler.getAdBasicInfoByQuery(query, bidPrice, campaignId, queryGroupId);
                System.out.println(ads.isEmpty() ? "no results" : (ads.size() + " results returned"));
                String category = ads.isEmpty() ? "" : ads.get(0).category;

                //sub query
                querySearched.add(query);
                List<String> subQueryList = getSubQuery(query);
                subQueryList.removeIf(q -> querySearched.contains(q));
                querySearched.addAll(subQueryList);
                System.out.println("size of sub query = " + subQueryList.size());
                for(String subQuery : subQueryList) {
                    System.out.println("searching sub query = " + subQuery);
                    List<Ad> adsForSubQuery = crawler.getAdBasicInfoByQuery(subQuery, bidPrice, campaignId, queryGroupId);
                    if(adsForSubQuery.isEmpty()) {
                        System.out.println("skipped empty sub query");
                        continue;
                    } else if(adsForSubQuery.get(0).category != category) {
                        System.out.println("skipped sub query with wrong category");
                        continue;
                    } else {
                        ads.addAll(adsForSubQuery);
                        System.out.println("sub query " + subQuery + " added " + adsForSubQuery.size() + " ads.");
                    }
                }

                for(Ad ad : ads) {
                    String jsonInString = mapper.writeValueAsString(ad);
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

    static List<String> getSubQuery(String query) {
        List<String> res = new ArrayList<>();

        List<String> tokens = getTokens(query);

        for(int i = 2; i <= tokens.size() - 1; i++) {
            res.addAll(getNGramFromTokens(tokens, i));
        }

        return res;
    }

    static List<String> getTokens(String str){
        List<String> tokens = new ArrayList<>();

        AttributeFactory attributeFactory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
        StandardTokenizer standardTokenizer = new StandardTokenizer(attributeFactory);
        standardTokenizer.setReader(new StringReader(str));
        try {
            standardTokenizer.reset();
            while(standardTokenizer.incrementToken()) {
                tokens.add(standardTokenizer.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokens;
    }

    static List<String> getNGramFromTokens(List<String> tokens, int n){
        List<String> res = new ArrayList<>();
        String curNGram = new String();
        for(int i = 0; i <= tokens.size() - n; i++) {
            for(int j = i; j < i + n; j++) {
                curNGram += tokens.get(j) + ' ';
            }
            res.add(curNGram.trim());
            curNGram = "";
        }
        return res;
    }
}