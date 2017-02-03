package wise_meow;

import wise.meow.display.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WiseMeow {

    public String chat(String query) throws Exception {
        String response = null;
        query = query.toLowerCase();

        if (query.contains("weather") && !query.contains("what")) {
            try {
                return parse_accu_weather(query);
            } catch (IOException ex) {
                Logger.getLogger(WiseMeow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (query.contains("bye") == true && query.endsWith("bye") && query.startsWith("good")) {

            response = "Good Bye :D Have a nice day :D";
        } else {
            try {
                response = parse_google(query);
            } catch (IOException ex) {
                try {
                    response = parse_bing(query);
                } catch (IOException ex1) {
                    Logger.getLogger(WiseMeow.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (Exception ex) {
                Logger.getLogger(WiseMeow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (response == "0" || response.length() == 0) {
            try {
                response = from_chat(query);
            }
            catch(FileNotFoundException f)
            {
                Files.write(Paths.get("newChat.txt"), "".getBytes(), StandardOpenOption.CREATE_NEW);
                throw new Exception();
            }
            catch (IOException ex) {
                Logger.getLogger(WiseMeow.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex) {
                throw ex;
                //addToChat(query);
            }
        }

        return response;
    }

    String parse_google(String query) throws IOException, Exception {

        final URL url;
        url = new URL("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8"));//https://www.youtube.com/results?search_query=meow

        final URLConnection connection = url.openConnection();

        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Google Chrome/55");//put the browser name/version

        try (Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")) //scanning a buffer from object returned by http request
        {
            while (reader.hasNextLine()) {                                                   //for each line in buffer
                String line = reader.nextLine();

                char[] c = line.toCharArray();
                char[] res = new char[100000];

                int i, k, j;

                if (line.contains("wiki")) {
                    for (i = 0; i < c.length - 4; i = i + 1) {
                        if (c[i] == 'w' && c[i + 1] == 'i' && c[i + 2] == 'k' && c[i + 3] == 'i') {
                            break;
                        }
                    }

                    for (;; i--) {
                        if (c[i] == 'h' && c[i + 1] == 't' && c[i + 2] == 't' && c[i + 3] == 'p') {
                            break;
                        }
                    }

                    for (j = 0; c[i] != '&'; i++, j++) {
                        res[j] = c[i];
                    }

                    String link = new String(res);
                    link = link.trim();
                    //System.out.println(link);
                    link = parse_wiki(link);

                    // System.out.println(link);
                    return link;

                }
            }
            return "0";
        }
    }

    String search_weather_link(String query) throws IOException {

        final URL url;
        url = new URL("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8"));

        final URLConnection connection = url.openConnection();

        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Google Chrome/55");//put the browser name/version

        try (Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")) //scanning a buffer from object returned by http request
        {
            while (reader.hasNextLine()) {
                //for each line in buffer
                String line = reader.nextLine();

                //System.out.println(line);
                char[] c = line.toCharArray();
                char[] res = new char[100000];

                int i, k, j;

                if (line.contains("accuweather")) {
                    //System.out.println(line);
                    for (i = 0; i < c.length - 4; i = i + 1) {
                        if (c[i] == 'h' && c[i + 1] == 't' && c[i + 2] == 't' && c[i + 3] == 'p' && c[i + 4] == ':' && c[i + 5] == '/' && c[i + 6] == '/' && c[i + 7] == 'w' && c[i + 8] == 'w' && c[i + 9] == 'w' && c[i + 10] == '.' && c[i + 11] == 'a') {
                            break;
                        }
                    }
                    for (j = 0; i < c.length && c[i] != '&'; i++, j++) {
                        res[j] = c[i];
                    }

                    String link = new String(res);
                    link = link.trim();

                    return link;
                }
            }
        }
        return "0";
    }

    String parse_accu_weather(String query) throws IOException {

        query = search_weather_link(query);

        if (query == "0") {
            return "Sorry , I am not abling to tell you  about the weather.";
        }

        final URL url;
        url = new URL(query);

        final URLConnection connection = url.openConnection();

        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Google Chrome/55");//put the browser name/version

        int flag = 0;
        String ret = "";
        String temp = "";
        String com = "";

        try (Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")) //scanning a buffer from object returned by http request
        {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                char[] c = line.toCharArray();
                char[] res = new char[100000];

                int i, k, j;

                if (line.contains("Current Weather</a></h3>")) {
                    break;
                }
            }

            while (reader.hasNextLine()) {
                //for each line in buffer
                String line = reader.nextLine();

                //System.out.println(line);
                char[] c = line.toCharArray();
                char[] res = new char[100000];

                int i, k, j;

                if (line.contains("large-temp") && flag == 0) {

                    for (i = 0; i < c.length; i = i + 1) {
                        if (c[i] == '>') {
                            i++;
                            break;
                        }
                    }
                    for (j = 0; i < c.length && c[i] != '&'; i++, j++) {
                        res[j] = c[i];
                    }

                    temp = new String(res);
                    flag++;
                    temp = temp.trim();
                }

                if (line.contains("cond") && flag == 1) {
                    for (i = 0; i < c.length; i = i + 1) {
                        if (c[i] == 'c' && c[i + 1] == 'o' && c[i + 2] == 'n' && c[i + 3] == 'd') {
                            i += 6;
                            break;
                        }
                    }
                    for (j = 0; i < c.length && c[i] != '<'; i++, j++) {
                        res[j] = c[i];
                    }

                    com = new String(res);
                    flag++;
                    com = com.trim();
                    //System.out.print(com);
                }

                if (flag == 2) {
                    ret = String.format("\nIt's %s " + "\ndegree celsius and " + "\n%s" + "\nthere." + "\ndo you live there?", temp, com);
                    return ret;
                }
            }
            return "0";
        }
    }

    String parse_bing(String query) throws IOException {

        query = "wikipedia ".concat(query);
        query = query.replace(' ', '+');
        final URL url;
        url = new URL("https://www.bing.com/search?q=" + query);//https://www.youtube.com/results?search_query=meow

        final URLConnection connection = url.openConnection();

        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Google Chrome/55");//put the browser name/version

        try (Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")) //scanning a buffer from object returned by http request
        {
            while (reader.hasNextLine()) {                                                   //for each line in buffer
                String line = reader.nextLine();

                char[] c = line.toCharArray();
                char[] res = new char[100000];

                int i, k, j;

                if (line.contains("https://en.wikipedia.org")) {
                    for (i = 0; i < c.length - 3; i = i + 1) {
                        if (c[i] == 'w' && c[i + 1] == 'i' && c[i + 2] == 'k' && c[i + 3] == 'i') {
                            break;
                        }
                    }

                    for (;; i--) {
                        if (c[i] == 'h' && c[i + 1] == 't' && c[i + 2] == 't' && c[i + 3] == 'p') {
                            break;
                        }
                    }

                    for (j = 0; c[i] != '"'; i++, j++) {
                        res[j] = c[i];
                    }

                    String link = new String(res);
                    link = link.trim();

                    //System.out.println(link);
                    String response = parse_wiki(link);

                    // System.out.println(link);
                    if (response != "0") {
                        return response;
                    }

                }
            }
            return "0";
        }
    }

    String parse_wiki(final String query) throws IOException {
        final URL url;
        url = new URL(query);//https://www.youtube.com/results?search_query=meow

        final URLConnection connection = url.openConnection();

        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Google Chrome/55");//put the browser name/version

        try (Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")) //scanning a buffer from object returned by http request
        {
            while (reader.hasNextLine()) {                                                   //for each line in buffer
                String line = reader.nextLine();
                int i, j, k, flag;

                if (line.contains("<p>")) {
                    char[] c = line.toCharArray();
                    char[] res = new char[1000];

                    for (i = 3; i < c.length; i++) {
                        if (c[i - 3] == '<' && c[i - 2] == 'p' && c[i - 1] == '>') {
                            break;
                        }
                    }

                    flag = 1;
                    int t = i;
                    int dot = 0;
                    for (j = 0; i < c.length; i++) {
                        if (c[i] == '<' && c[i + 1] == '/' && c[i + 2] == 'p' && c[i + 3] == '>') {
                            break;
                        }
                        if (dot > 1) {
                            break;
                        }

                        if (c[i] == '<' && flag == 1) {
                            flag = 0;
                            continue;
                        }
                        if (c[i] == '>' && flag == 0) {
                            flag = 1;
                            continue;
                        }

                        if (flag == 1) {
                            res[j++] = c[i];
                            //if(c[i]== '.') res[j++] = '\n';
                        }

                        if (c[i] == '.') {
                            dot++;
                        }
                    }

                    res[j++] = '\0';

                    String response = new String(res);
                    response = response.trim();

                    if (response.length() > 30 && !response.contains("may refer to:")) {
                        return response;
                    }

                    continue;
                }

                continue;
            }
            return "0";
        }
    }

    String from_chat(String query) throws FileNotFoundException, IOException, Exception {
        FileInputStream fstream = new FileInputStream("newChat.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String str;

        query = query.toLowerCase();
        query = query.trim();

        while ((str = br.readLine()) != null) {
            //System.out.println(str);
            str = str.toLowerCase();
            str = str.trim();

            if (query.contains(str)) {
                str = br.readLine();
                if (!str.contains("#############")) {
                    //System.out.println(str);
                    break;
                } else {
                    str = null;
                }
            } else {
                str = null;
            }
        }

        br.close();
        if (str == null || str.length() == 0) {
            throw new Exception();
        }

        return str;
    }

    void addToFile(String st, String file) throws IOException {
        Files.write(Paths.get("file"), st.getBytes(), StandardOpenOption.APPEND);
    }

    void addToChat(String query) throws IOException {

        Scanner s1 = new Scanner(System.in);
        String str = null;

        Files.write(Paths.get("newChat.txt"), query.getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("newChat.txt"), "\n".getBytes(), StandardOpenOption.APPEND);

        System.out.println("I didn't understand what you told me. Please teach me what to respond in the asked question.Thanks :D \nYou :");
        
        
        str = s1.nextLine();
        Files.write(Paths.get("newChat.txt"), str.getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("newChat.txt"), "\n".getBytes(), StandardOpenOption.APPEND);

        Files.write(Paths.get("newChat.txt"), "######################################".getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("newChat.txt"), "\n".getBytes(), StandardOpenOption.APPEND);

        System.out.println("MEOW : \n Thank you wise human :D");
    }
    
    
}
