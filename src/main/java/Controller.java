import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;

import java.io.IOException;

public class Controller {

    //private static final String SERVER_URL = "http://localhost:32768/solr/mycore"; //RIAL
    private static final String SERVER_URL = "http://localhost:32769/solr/core_one"; //CLARET

    private final static HttpSolrServer solr = new HttpSolrServer(SERVER_URL);

    public static void main(String[] args) {

        int numberOfCrawlers = 2;

        /*
         * Crawler4J configuration
         */
        CrawlConfig config = new CrawlConfig();
        config.setMaxConnectionsPerHost(10);
        config.setConnectionTimeout(4000);
        config.setSocketTimeout(5000);
        config.setCrawlStorageFolder("tmp");
        config.setIncludeHttpsPages(true);
        config.setIncludeBinaryContentInCrawling(false);

        // minimum 250ms for tests
        //config.setPolitenessDelay(250);
        config.setPolitenessDelay(500);
        config.setUserAgentString("crawler4j/WEM/2019");

        // max 2-3 levels for tests on large website
        config.setMaxDepthOfCrawling(2);

        // -1 for unlimited number of pages
        config.setMaxPagesToFetch(70);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        try {
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            controller.addSeed("https://en.wikipedia.org/wiki/Veganism");
            deleteAllSolrData();
            controller.start(MyCrawler.class, numberOfCrawlers);
            solr.commit(true,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllSolrData() {
        try {
            solr.deleteByQuery("*:*");
            solr.commit();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("Failed to delete data in Solr. " + e.getMessage(), e);
        }
    }
}
