import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return false;
    }

    @Override
    public void visit(Page page) {
    }
}
