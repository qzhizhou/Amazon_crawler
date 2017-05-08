package demo.crawler;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Ling on 5/8/17.
 */
public class AmazonCrawlerTest {
    static final String proxyFilePath = "/home/vagrant/Desktop/proxylist.csv";
    AmazonCrawler crawler = new AmazonCrawler(proxyFilePath);

    @Test
    public void testTokenizeAndClean() {
        try {
            Method cleanAndTokenize = AmazonCrawler.class.getDeclaredMethod("cleanAndTokenize", String.class);
            cleanAndTokenize.setAccessible(true);
            List<String> tokens = (List<String>) cleanAndTokenize.invoke(crawler, "This is a test string for tokenizer. Hello world.");
//            for(String token : tokens) {
//                System.out.println(token);
//            }
            Assert.assertArrayEquals(tokens.toArray(), new String[] {"this", "test", "string", "tokenizer", "hello", "world"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
