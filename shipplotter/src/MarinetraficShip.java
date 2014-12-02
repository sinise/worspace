import java.net.*;
import java.io.*;
import java.awt.Toolkit;
import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.String;
/**
 * fetch content from Marinetrafic. either from a saved html file or from url
 * @param choice 0 to fecch online 1 to fetch from file
 */

public class MarinetraficShip
{
  public ArrayList<String> urlList = new ArrayList<String>();
  private URL url;
  private URLConnection uc;
  private String urlString;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private ArrayList<String> list = new ArrayList<String>();
  private BufferedReader in;
  public int sourceType;
  public String mmsi;
  public String name;
  public String type;
  public String shipid;
  private int lastLine = 0;
  private String file;
  private String cookie;
  private String htmlName;
  int positionsCount = 0;
  int userAgentCount = 0;
  String[] userAgent = {"Mozilla/5.0 (Windows NT 6.0; rv:33.0) Gecko/20100101 Firefox/33.0",
                        "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0",
                        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0)",
                        "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
                        "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.65 Safari/537.36",
                        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 YaBrowser/14.10.2062.12061 Safari/537.36",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.1.25 (KHTML, like Gecko) Version/8.0 Safari/600.1.25",
                        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"};
  private int page = 1;
  private int lastPage;
  private int start;
    /**
     *Constructor for a Marinetrafic ship to fetch from url
     *@param mmsi mmsi of ship
     *@param name name of ship entered exactly as marinetrafic does. is casesentitive
     */
    public MarinetraficShip(String mmsi, String name, String type) {
      try {
        this.mmsi = mmsi;
        this.name = name;
        this.type = type;
        sourceType = 0;
        htmlName = name.replace(" ", "%20");
//        urlString = "http://www.marinetraffic.com/dk/ais/index/positions/all/mmsi:" + mmsi +"/shipname:" + htmlName + "/per_page:50/page:1";
//        url = new URL(urlString);
      }
      catch (Exception e) {
        System.err.println("Error1: " + e.getMessage());
      }
    }

    /**
     *Constructor for a Marinetrafic ship to fetch from url with login
     *@param mmsi mmsi of ship
     *@param name name of ship entered exactly as marinetrafic does. is casesentitive
     *@param file is not used for anything so just type something
     *@param cookie the txt file containing the cookie
     */
    public MarinetraficShip(String mmsi, String name, String type, String start, String cookie) {
      try {
        this.mmsi = mmsi;
        this.name = name;
        this.type = type;
        this.cookie = cookie;
        this.start = Integer.parseInt(start);
        sourceType = 2;
        htmlName = name.replace(" ", "%20");
      }
      catch (Exception e) {
        System.err.println("Error2: " + e.getMessage());
      }
    }

    /**
     *Constructor for a Marinetrafic ship to fetch from html files
     *@param mmsi mmsi of ship
     *@param name name of ship entered exactly as marinetrafic does. is casesentitive
     *@param file file to fetch from
     */
    public MarinetraficShip(String mmsi, String name, String type, String file) {
      this.mmsi = mmsi;
      this.name = name;
      this.type = type;
      this.file = file;
      sourceType = 1;
    }

  public String[] getData() {
    int length = list.size();
    String[] returnArray = new String[length];
    for (int i = 0; i < length; i++) {
      returnArray[i] = list.get(i);
    }
    return returnArray;
  }
  //fetch data from Marinetrafic
  public void fetchData() {
    try {
      if (sourceType == 2 || sourceType == 0) {

        System.out.println("sourcetype er " + sourceType);

        // get last page
        urlString = "http://www.marinetraffic.com/dk/ais/index/positions/all/shipid:" + shipid +"/mmsi:" + mmsi +"/shipname:" + htmlName + "/per_page:50/page:" + page;
        System.out.println(urlString);
        url = new URL(urlString);
        uc = url.openConnection();
        uc.addRequestProperty("User-Agent", userAgent[userAgentCount]);
        if(sourceType == 0){
          uc.setRequestProperty("Cookie", "AUTH=EMAIL=tony.sadownichik@greenpeace.org&CHALLENGE=US1KIfRUfmcsKeERcCip; mt_user[User][ID]=Q2FrZQ%3D%3D.f0rvCaXH");
        }
        uc.connect();
        in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String ch;
        String regEx = "</i></a></span></div>";
        while ((ch = in.readLine()) != null) {
          if (ch.contains(regEx)) {
            int indexEnd = ch.lastIndexOf("type=") - 2;
            int  indexStart = ch.indexOf("max=") + 5;
            lastPage = Integer.parseInt(ch.substring(indexStart, indexEnd));
          }
        }

        //fetch the pages
        page = 1;
        for(int i = 0; page < lastPage + 1; i++) {
          System.out.println("enter to continiu");
          System.in.read();

          try{
            urlString = "http://www.marinetraffic.com/dk/ais/index/positions/all/shipid:" + shipid +"/mmsi:" + mmsi +"/shipname:" + htmlName + "/per_page:50/page:" + page;
            url = new URL(urlString);
            uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:33.0) Gecko/20100101 Firefox/33.0");
            uc.addRequestProperty("User-Agent", userAgent[userAgentCount]);
            uc.setRequestProperty("Cookie", "AUTH=EMAIL=tony.sadownichik@greenpeace.org&CHALLENGE=US1KIfRUfmcsKeERcCip; mt_user[User][ID]=Q2FrZQ%3D%3D.f0rvCaXH");
            uc.connect();
            in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            trimData();
            System.out.println(" " + page + " of " + lastPage);

          }
            catch (Exception e) {
            System.err.println("Error3:     " + e.getMessage());
            page--;

          } finally{
              page++;
            }
        }
      }
      if (sourceType == 1) {
        System.out.printf("feching from file %S\n", name);
        for(int i = 1; i < 23; i++){
          FileInputStream fstream = new FileInputStream(i + file);
          DataInputStream fin = new DataInputStream(fstream);
          in = new BufferedReader(new InputStreamReader(fin));
          trimData();
        }
      }
    }
    catch (Exception e) {
      System.err.println("Error4: " + e.getMessage());
    }
  }

// trim the data and put in the array list which can be acceced from other classes
  public void trimData() {
    try{
      String ch;
      String parsedData = "";
      String regEx = "<td>";
      String regExNot = "<a href=";
      String regStartLine = "<time datetime=";
      positionsCount = 0;

      while ((ch = in.readLine()) != null) {
        if (ch.contains(regEx)){
          ch = in.readLine().trim();

          if (ch.contains(regStartLine)) {
            positionsCount++;
            if (parsedData.length() > 20){
              String[] lineSplit = parsedData.split(",");
System.out.println(parsedData);
              //if the position line is not filled correct break
              if(lineSplit.length == 4){break;}
              String time = lineSplit[0];
              String speed = lineSplit[1];
              String lat = lineSplit[2];
              String lon = lineSplit[3];
              String course = lineSplit[4];
              list.add(mmsi + "," + time.substring(0, 10) + ",0," + type +"," + lat + "," + lon + "," + speed + "," + course + ",0,0,0,0," +
                       name + ",0,0,0,0,0,0,0,0,0");
            }

            int indexStartT = ch.indexOf("</time>") - 16;
            int  indexEndT = ch.indexOf("</time");
            Date d = sdf.parse(ch.substring(indexStartT, indexEndT));
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            long timestamp = c.getTimeInMillis();
            parsedData = parsedData.valueOf(timestamp);
          }
          else {
            if(!(ch.contains("href")) && !(ch.contains("AIS"))){
            parsedData = parsedData + ("," + ch.replace("</td>", "").trim().replace(" kn",""));
            }
          }
        }
      }
      if (list.size() < 2){ page--;}

      System.out.print(name);
      System.out.printf("Fetched %d positions", list.size());
      in.close();
    }
    catch (Exception e) {
      System.err.println("Error5: " + e.getMessage());
      System.out.println("-----------------------------------" + lastLine);
      page = page - 1;
    }
  }

  /** Load first positionpage, loadFirst method and return 50 per page link
   * also save the page in the url ArrayList
   * @return urlString
   */
  public String loadFirstPosition(){
    System.out.println("");
    System.out.println("");

    urlString = loadFirst();
    try{
      Thread.sleep(600);
      url = new URL(urlString);
      urlList.add(SaveURL.getURL(url, userAgent[userAgentCount]));
      System.out.print(urlList.get(1));
    } catch (Exception e) {
      System.err.println("Error6: " + e.getMessage());
    }

    boolean linkFound = false;
    String returnUrl = null;
    String ch2;
    Scanner scanner = new Scanner(urlList.get(1));
    while ((ch2 = scanner.nextLine()) != null && !linkFound) {
      if (ch2.contains("class=\"details_data_link\">Latest Positions</a></strong>")) {
        int indexStart = ch2.lastIndexOf("href=") + 6;
        int  indexEnd = ch2.lastIndexOf("class=") - 3;
        returnUrl = "http://marinetraffic.com/" + ch2.substring(indexStart, indexEnd);
        linkFound = true;
      }
    }
    return returnUrl;
  }








  /** Load first page, set useragent and return next link to follow
   * also save the page in the url ArrayList
   * @return urlString
   */
  public String loadFirst(){
    System.out.println("");
    System.out.println("loadFirst ");
    loadFrontPage();

    userAgentCount = -1;
    boolean shipidFound = false;
    String returnUrl = null;
    while(!shipidFound && userAgentCount < userAgent.length){
      urlString = "http://www.marinetraffic.com/en/ais/index/search/all?keyword=" + mmsi;
      System.out.println(urlString);
      userAgentCount++;
      try{

        url = new URL(urlString);
        urlList.add(SaveURL.getURL(url, userAgent[userAgentCount]));
//        System.out.print(urlList.get(0));
      } catch (Exception e) {
        System.err.println("Error6: " + e.getMessage());
      }

      String ch2;
      Scanner scanner = new Scanner(urlList.get(0));
      while ((ch2 = scanner.nextLine()) != null && !shipidFound) {

        if (ch2.contains("<a class=\"search_index_link\" title=")) {

          int indexStart = ch2.lastIndexOf("href=") + 6;
          int  indexEnd = ch2.lastIndexOf(mmsi) - 2;
          returnUrl = "http://marinetraffic.com/" + ch2.substring(indexStart, indexEnd);
          System.out.println("ship found");
          shipidFound = true;
        }
      }
    }
    return returnUrl;
  }

  //load frontpage to trick marinetrafic to belive we are a human user
  public void loadFrontPage() {
    try {
      System.out.println("loading frontpage");
      urlString = "http://www.marinetraffic.com/";
      url = new URL(urlString);
      uc = url.openConnection();
      uc.addRequestProperty("User-Agent", userAgent[userAgentCount]);
      if(sourceType == 0){
        uc.setRequestProperty("Cookie", "AUTH=EMAIL=tony.sadownichik@greenpeace.org&CHALLENGE=US1KIfRUfmcsKeERcCip; mt_user[User][ID]=Q2FrZQ%3D%3D.f0rvCaXH");
      }
      uc.connect();
      in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
      String ch;
      while ((ch = in.readLine()) != null) {
      }
      Thread.sleep(1000);
    } catch (Exception e) {
      System.err.println("Error 7 " + e.getMessage());
    }
  }
}

