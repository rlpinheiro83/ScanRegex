package legistar;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;

public class ScanRegex {

    public static void main(String[] args) throws Exception {
        String googleQuery;
        String bingQuery;
        String regex;
        int avgWait;

        if(args.length == 4){
            System.out.println("Using parametres...");
            googleQuery = args[0];
            System.out.println("\tGoogle Query: " + googleQuery);
            bingQuery = args[1];
            System.out.println("\tBing Query: " + bingQuery);
            regex = args[2];
            System.out.println("\tRegex: " + regex);
            avgWait = Math.max(Integer.parseInt(args[3]), 16);
            System.out.println("\tAverage Waiting Time: " + avgWait + "s");
        }else{
            System.out.println("Using default for Legistar...");
            googleQuery = "https://www.google.com/search?q=site%3Alegistar.com";
            System.out.println("\tGoogle Query: " + googleQuery);
            bingQuery = "https://www.bing.com/search?q=site%3Alegistar.com";
            System.out.println("\tBing Query: " + bingQuery);
            regex = "[\\w]+[.]legistar.com";
            System.out.println("\tRegex: " + regex);
            avgWait = 45;
            System.out.println("\tAverage Waiting Time: " + avgWait + "s");
        }

        //spoof Google first
         HashSet<String> results = new HashSet<>();
         spoof(googleQuery, regex, avgWait, results);
         spoof(bingQuery, regex, avgWait, results);
         
         System.out.print("\n\nPrinting Results...\n");
        Iterator<String> it = results.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.exit(0);
    }

    //"[\\w]+[.]legistar.com"
    public static HashSet<String> spoof(String query, String regex, int avgWait, HashSet<String> matches) throws Exception {
        HtmlPage currentPage = loadSite(query);
        if(currentPage.getTitleText().toLowerCase().contains("google")){
            System.out.println("Spoofing Google");
        }else{
            System.out.println("Spoofing Bing");
        }
        int i = 0;
        do { //main loop

            System.out.print("Page: " + i++ + " | ");
            find(regex, currentPage.asXml(), matches);
            System.out.print("Matches: " + matches.size() + "\t\t");
            System.out.print("Page Title: " + currentPage.getTitleText() + "\n");
            if (hasNextPage(currentPage)) {
                currentPage = clickNextPage(currentPage);                   
            } else {
                break;
            }
            Thread.sleep(((int)(30000*Math.random())) + (avgWait-15)*1000);
        } while (true);
        return matches;
    }

    public static boolean hasNextPage(HtmlPage page) {
        if (!page.asXml().contains("pnnext") && !page.asXml().contains("Next page")) {
            return false;
        }
        return true;
    }

    public static HtmlPage loadSite(String site) throws Exception {
        HtmlPage page = null;
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(java.util.logging.Level.OFF);
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            page = webClient.getPage(site);
        
            return page;
        }catch(Exception e){
            System.out.print("\n\nCRITICAL ERROR: "+e.getLocalizedMessage());
            return null;
        }
            
    }

    public static HtmlPage clickNextPage(HtmlPage page) throws Exception {
        if(page.getTitleText().toLowerCase().contains("google")){
            final HtmlDivision div = page.getHtmlElementById("botstuff");
            HtmlAnchor anchor = page.getHtmlElementById("pnnext");

            return loadSite("https://google.com" + anchor.getHrefAttribute());
        }else{
            List<HtmlAnchor> anchors = page.getAnchors();
            for(HtmlAnchor anchor : anchors){
                if(anchor.getAttribute("title").toLowerCase().contains("next page")){
                    return loadSite("https://bing.com" + anchor.getHrefAttribute());
                }
            }
        }
        return null;
    }

    
    public static String find(String regex, String site, HashSet<String> matches) {
        String next = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(site);
        while (m.find()) {
            String temp = m.group().replaceAll("^x22", "");
            //String temp = m.group();
            if (!matches.contains(temp)) {
                matches.add(temp);
            }
        }
        String[] temp = site.split("d6cvqb BBwThe");
        return next;
    }

}
