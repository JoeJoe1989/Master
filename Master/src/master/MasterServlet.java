package master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MasterServlet extends HttpServlet {
	JSONParser parser;
	String hostName = "localhost:8081";
	HashMap<String, ArrayList<WordInURL>> docInfo;
	HashMap<String, Double> wordInfo;

	@Override
	public void init(ServletConfig config) {
		parser = new JSONParser();
		docInfo = new HashMap<String, ArrayList<WordInURL>>();
		wordInfo = new HashMap<String, Double>();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");

		String query = request.getParameter("word");

		String url = "http://" + hostName + "/SingleNode/single?word=" + query;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		PrintWriter out = response.getWriter();
		// Get Response
		InputStream is = con.getInputStream();
		System.out.println("dddddddddddddd");

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String output = "";
		String line;
		while ((line = rd.readLine()) != null) {
			output += line;
		}
		rd.close();
//		out.println(output);

		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(output);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(jsonObject.get("word"));
		// System.out.println(jsonObject.get("doclist"));
		String word = jsonObject.get("word").toString();
		Double idf = Double.parseDouble(jsonObject.get("idf").toString());
		wordInfo.put(word, idf);

		String docListTemp = jsonObject.get("doclist").toString();
		docListTemp = docListTemp.substring(1, docListTemp.length() - 1);

		String[] docList = docListTemp.split(",");

		for (String doc : docList) {
			System.out.println(doc);
			
			String[] docSplit = doc.split("\\s", 3);
			String urlInfo = docSplit[0];
			double tf = Double.parseDouble(docSplit[1]);
			String positions = docSplit[2];

			WordInURL wordInURL = new WordInURL(word, tf, idf, positions);

			if (!docInfo.containsKey(doc)) {
				ArrayList<WordInURL> temp = new ArrayList<WordInURL>();
				temp.add(wordInURL);
				docInfo.put(doc, temp);
			} else {
				docInfo.get(doc).add(wordInURL);
			}

		}
		
		String docTest = "";
		for (String key : docInfo.keySet()) {
			docTest += docInfo.get(key);
		}
		
		out.println(docTest);
		

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		String res = request.getParameter("word");
		out.println(res);
	}

}
