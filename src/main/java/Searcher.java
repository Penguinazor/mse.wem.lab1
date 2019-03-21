import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Searcher {

    public static void main(String[] args) {
        // Change logs level to INFO
        // List<String> loggers = new ArrayList<>(Arrays.asList("org.apache.http", "org.apache.sol"));
        Logger logger = (Logger) LoggerFactory.getLogger("org.apache");
        logger.setLevel(Level.INFO);
        logger.setAdditive(false);

        // Initialize the Solr server
        HttpSolrServer solr = new HttpSolrServer(Config.SERVER_URL);

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a query: ");
        String query = reader.nextLine();

        //String query = "Vegan";

        final Map<String, String> queryParamsMap = new HashMap<>();

        /*
        en_doc_title
        en_doc_body
        en_doc_categories
        en_doc_topics
        en_doc_infobox
        en_doc_language
        en_doc_navigations
        en_doc_url
        */


        //https://wiki.apache.org/solr/CommonQueryParameters
        //The q parameter is normally the main query for the request.
        //MAGIC FORMULA (I have no idea what I am doing... just feelings)
        queryParamsMap.put("q", String.format("(en_doc_title:\"%s\")^6 " +
                        "(en_doc_topics:\"%s\")^5 " +
                        "(en_doc_infobox:\"%s\")^4 " +
                        "(en_doc_categories:\"%s\")^3 " +
                        "(en_doc_navigations:\"%s\")^2 " +
                        "(en_doc_body:\"%s\")^1",
                query, query, query, query, query, query));

        //This parameter can be used to specify a set of fields to return
        queryParamsMap.put("fl", "en_doc_title,en_doc_url,score");

        MapSolrParams solarQueryParams = new MapSolrParams(queryParamsMap);


        final QueryResponse response;
        try {
            response = solr.query(solarQueryParams);
            final SolrDocumentList documents = response.getResults();
            final SolrQuery solarQuery = new SolrQuery("*:*").setRows(0);

            System.out.println("=================");
            System.out.println("QUERY: " + query);
            System.out.println("DOCUMENTS FOUND: " + documents.getNumFound());
            System.out.println("TOTAL INDEX: " + solr.query(solarQuery).getResults().getNumFound());
            System.out.println("=================");
            for (SolrDocument document : documents) {
                System.out.println("title: " + document.get("en_doc_title"));
                System.out.println("score: " + document.get("score"));
                System.out.println("url: " + document.get("en_doc_url"));
                System.out.println();
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }
}