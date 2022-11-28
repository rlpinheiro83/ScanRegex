/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package legistar;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author rodrigo
 */
public class ZipScrape {


    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("out.txt");
        BufferedReader reader;
        int hits = 0;
        int total = 0;
        try{
            reader = new BufferedReader(new FileReader("./ZIPs.csv"));
            String line = reader.readLine();
            while(line != null){
                //if(total == 10) break;
                try{                                                
                total++;
//                if(line.contains("(PO Box 130036)")){
//                    line=line;
//                }
                System.out.print(total+": "+ line + ",");
                String treated = line.replace("\"", "").trim();
                Pattern pattern = Pattern.compile("^\\d\\d\\d\\d\\d.*");
                Matcher m = pattern.matcher(treated);
                if(m.find()){//match!
                    String county = scrapeZip(treated.substring(0,5));
                    fw.write(line + "," + county.trim() + System.lineSeparator());
                    Thread.sleep(1000);
                    System.out.print(county+System.lineSeparator());
                    hits++;
                }else{
                    fw.write(line + ",n/a" + System.lineSeparator());
                    System.out.print(total+": "+"n/a"+System.lineSeparator());
                }
                fw.flush();
                }catch(Exception e){
                    fw.write(line + ",n/a" + System.lineSeparator());
                    System.out.print(total+": "+e.getMessage()+System.lineSeparator());
                    fw.flush();
                }
                line = reader.readLine();
            }
        }catch(Exception e){
            
        }        
        fw.flush();
        fw.close();
        System.out.println("\n\nTotal entries: "+ total);
        System.out.println("Total hits: "+ hits);
        double suc = ((double)hits)/((double)total)*100.0;
        System.out.println("Success Rate: "+ String.format("%2.1f",suc)+"%");
    }
    
    public static String scrapeZip(String zip) throws Exception{
        HtmlPage page = null;
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(java.util.logging.Level.OFF);
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            page = webClient.getPage("https://www.getzips.com/zip.htm");
            HtmlForm form = page.getFormByName("ZIPLookup");
            HtmlSubmitInput button = form.getInputByName("Submit");
            HtmlTextInput textField = form.getInputByName("Zip");

            // Change the value of the text field
            textField.setValueAttribute(zip);

            // Now submit the form by clicking the button and get back the second page.
            HtmlPage page2 = button.click();
            return page2.asXml().split(zip)[1].split("<p>")[2].split("</p>")[0].trim();
        }catch(Exception e){
            //System.out.print("\n\nCRITICAL ERROR: "+e.getLocalizedMessage());
            throw new Exception("Could not parse results.");
            //return null;
        }        
    }
    
}
