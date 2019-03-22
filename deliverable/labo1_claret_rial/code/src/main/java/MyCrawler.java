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

@SuppressWarnings("Duplicates")
public class MyCrawler extends WebCrawler {

    private List<SolrInputDocument> documentsIndexed = new CopyOnWriteArrayList<>();

    private Integer docCounter = 0;
    private static final Integer untilCommit = 50;

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !Config.FILTERS.matcher(href).matches() && href.startsWith(Config.DOMAIN);
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

            SolrServer solr = new HttpSolrServer(Config.SERVER_URL);

            String page_title = doc.title();
            String page_body = doc.body().text();

            doSolrInputDocument.setField("doc_title_en", page_title);
            doSolrInputDocument.setField("doc_body_en", page_body);

            try {
                solr.add(doSolrInputDocument);

                if (docCounter++ % Config.UNTIL_COMMIT == 0)
                    solr.commit(true, true);
                System.out.println("docCounter: " + docCounter);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
            System.out.println("=================");
        }
    }
}
