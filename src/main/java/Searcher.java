import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Searcher {

    private final static HttpSolrServer solr = new HttpSolrServer(Config.SERVER_URL);

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a query: ");
        String query = reader.next();

        //String query = "Vegan";

        final Map<String, String> queryParamsMap = new HashMap<String, String>();

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
        queryParamsMap.put("q", String.format("(en_doc_title:%s)^6 " +
                                            "(en_doc_topics:%s)^5 " +
                                            "(en_doc_infobox:%s)^4 " +
                                            "(en_doc_categories:%s)^3 " +
                                            "(en_doc_navigations:%s)^2 " +
                                            "(en_doc_body:%s)^1",
                query, query, query, query, query, query, query));

        //This parameter can be used to specify a set of fields to return
        queryParamsMap.put("fl", "en_doc_title," +
                                "en_doc_url," +
                                "score");

        MapSolrParams solarQueryParams = new MapSolrParams(queryParamsMap);


        final QueryResponse response;
        try {
            response = solr.query(solarQueryParams);
            final SolrDocumentList documents = response.getResults();
            final SolrQuery solarQuery = new SolrQuery("*:*").setRows(0);

            System.out.println("=================");
            System.out.println("QUERY: " + query);
            System.out.println("NUMBER OF DOCUMENTS FOUND: " + documents.getNumFound());
            System.out.println("TOTAL DOCUMENTS IN INDEX: " + solr.query(solarQuery).getResults().getNumFound());
            System.out.println("=================");
            for (SolrDocument document : documents) {
                System.out.println("title: " + document.get("en_doc_title"));
                System.out.println("score: " + document.get("score"));
                System.out.println("url: " + document.get("en_doc_url"));
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }
}