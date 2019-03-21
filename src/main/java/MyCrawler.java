import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private static final Pattern FILTERS =
            Pattern.compile(
                    ".*(\\.(css|js|bmp|gif|jpe?g"
                            + "|png|tiff?|mid|mp2|mp3|mp4"
                            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private List<SolrInputDocument> documentsIndexed = new CopyOnWriteArrayList<>();

    //private static final String SERVER_URL = "http://localhost:32768/solr/mycore"; //RIAL
    private static final String SERVER_URL = "http://localhost:32770/solr/core_one"; //CLARET

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith("https://arxiv.org");
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            // Parsing Tags out of Jsoup
            Document doc = Jsoup.parse(html);
            SolrInputDocument doSolrInputDocument = new SolrInputDocument();
            doSolrInputDocument.setField("id", page.hashCode());

            SolrServer solr = new HttpSolrServer(SERVER_URL);

            /*Elements paragraphList = doc.getElementsByTag("p");
            for (Element parElement : paragraphList) {
                String paragraphText = parElement.text();
                doSolrInputDocument.addField("features", paragraphText);
            }
            */

            String docText = doc.text();
            doSolrInputDocument.addField("features", docText);

            //TODO add feature extraction here for indexation

            if (documentsIndexed.size() > 1) {
                try {
                    solr.add(doSolrInputDocument);

                    solr.commit(true, true);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
    }
}
