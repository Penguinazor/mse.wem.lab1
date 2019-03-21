import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class MyCrawler2 extends WebCrawler {

    private List<SolrInputDocument> documentsIndexed = new CopyOnWriteArrayList<>();

    private Integer docCounter = 0;

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


            //TODO add feature extraction here for indexation


            // Get page_title
            String page_title = doc.getElementById("firstHeading").text();

            String page_body = doc.body().text();

            // Get page_categories
            List<String> page_categories = new ArrayList<>();
            Element category_element = doc.getElementById("mw-normal-catlinks");
            if (category_element != null)
                page_categories = category_element.getElementsByTag("li").eachText();

            // Get page_topics
            List<String> page_topics = new ArrayList<>();
            Elements topics_elements = doc.getElementsByTag("h3");
            if (topics_elements.first() != null)
                page_topics = topics_elements.eachText();

            // Get page_infobox
            String page_infobox = "";
            Element infoboxElement = doc.body().getElementsByClass("infobox").first();
            if(infoboxElement != null)
                page_infobox = infoboxElement.text();

            // Get page_language
            String page_language = doc.select("html").attr("lang");

            // Get page_topics
            List<String> page_navigations = new ArrayList<>();
            Elements navigation_elements = doc.getElementsByClass("navbox");
            if (navigation_elements.first() != null)
                page_navigations = navigation_elements.eachText();

            doSolrInputDocument.setField("en_doc_title", page_title);
            doSolrInputDocument.setField("en_doc_body", page_body);
            doSolrInputDocument.setField("en_doc_categories", page_categories);

            doSolrInputDocument.setField("en_doc_topics", page_topics);
            doSolrInputDocument.setField("en_doc_infobox", page_infobox);

            doSolrInputDocument.setField("en_doc_language", page_language);

            doSolrInputDocument.setField("en_doc_navigations", page_navigations);

            doSolrInputDocument.setField("en_doc_url", page.getWebURL().getURL());

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
