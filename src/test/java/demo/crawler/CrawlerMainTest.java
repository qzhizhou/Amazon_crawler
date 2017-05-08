package demo.crawler;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ling on 5/8/17.
 */
public class CrawlerMainTest {
    @Test
    public void getTokensTest() {
        List<String> tokens = CrawlerMain.getTokens("hello world. This is a test for get tokens.");
        Assert.assertArrayEquals(tokens.toArray(), new String[] {"hello", "world", "This", "is", "a", "test", "for", "get", "tokens"});
    }
    @Test
    public void getNGramTest() {
        List<String> tokens = new ArrayList<String>(Arrays.asList("token1", "token2", "token3", "token4", "token5"));
        List<String> biGram = CrawlerMain.getNGramFromTokens(tokens, 2);
        List<String> triGram = CrawlerMain.getNGramFromTokens(tokens, 3);
        List<String> fourGram = CrawlerMain.getNGramFromTokens(tokens, 4);
        Assert.assertArrayEquals(biGram.toArray(), new String[] {"token1 token2", "token2 token3", "token3 token4", "token4 token5"});
        Assert.assertArrayEquals(triGram.toArray(), new String[] {"token1 token2 token3", "token2 token3 token4", "token3 token4 token5"});
        Assert.assertArrayEquals(fourGram.toArray(), new String[] {"token1 token2 token3 token4", "token2 token3 token4 token5"});
    }
    @Test
    public void getSubQueryTest() {
        List<String> subQuery = CrawlerMain.getSubQuery("token1 token2 token3 token4 token5");
        Assert.assertArrayEquals(subQuery.toArray(), new String[] {"token1 token2", "token2 token3", "token3 token4", "token4 token5",
                "token1 token2 token3", "token2 token3 token4", "token3 token4 token5","token1 token2 token3 token4", "token2 token3 token4 token5"});
    }
}
