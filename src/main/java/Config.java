import java.util.regex.Pattern;

public class Config {

    //public final static String SERVER_URL = "http://localhost:32768/solr/mycore"; //RIAL
    public final static String SERVER_URL = "http://localhost:32769/solr/core_one"; //CLARET

    //public final static String DOMAIN = "https://arxiv.org/";
    public final static String DOMAIN = "https://en.wikipedia.org/wiki/";

    public final static int UNTIL_COMMIT = 50;

    public final static Pattern FILTERS =
            Pattern.compile(
                    ".*(\\.(css|js|bmp|gif|jpe?g" +
                    "|png|tiff?|mid|mp2|mp3|mp4" +
                    "|wav|avi|mov|mpeg|ram|m4v|pdf" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
}