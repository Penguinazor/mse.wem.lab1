import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

  private static final Pattern FILTERS =
      Pattern.compile(
          ".*(\\.(css|js|bmp|gif|jpe?g"
              + "|png|tiff?|mid|mp2|mp3|mp4"
              + "|wav|avi|mov|mpeg|ram|m4v|pdf"
              + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    return !FILTERS.matcher(href).matches() && href.startsWith("https://www.vegan.com/");
  }

  @Override
  public void visit(Page page) {}
}
