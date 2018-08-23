

/* ----------------------------------------------------------------------------
 * Name of Software: Stox 
 * Author/Programmer: Abhishek (Abhi) Kapoor 
 * Date: July 16th, 2018 
 * Location: University of Toronto - Canada 
 * Purpose and Description: A Java-based application 
 which utilizes network programming, web scraping 
 with the JSoup library, and regular expressions to retrieve and display 
 financial information about current and historic stock 
 prices based on a user-entered public listed company 
 ----------------------------------------------------------------------------- */

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*; 
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StoxSoftware extends JPanel implements ActionListener 
{
	
	static JFrame frame; 
	JLabel lblNewOne; 
	static JLabel lblLogo; 
	static JLabel lblStockPrice; 
	static JLabel lblTitle; 
	static JLabel progress; 
	static JLabel lblPreviousClose; 
	static JLabel lblDaysRange; 
	static JLabel lblTodaysOpeningPrice; 
	static JLabel lblDate; 
	JLabel lblCopyright; 
	static JLabel lblDisplayGraph; 
	static JTextField txtEnter; 
	JButton btnEnter; 
	static JLabel progressOne; 
	
	/**
	 * This is the Constructor method of the Stox application, and is used to create the GUI JFrame and 
	 * add all the elements, including JTextFields, JButtons, and JLabels 
	 */
	
	public StoxSoftware() 
	{
		frame = new JFrame(" | Welcome to Stox | ");
		
		ImageIcon imgNewOne = new ImageIcon("grrnew.jpg");
	    lblNewOne = new JLabel(imgNewOne); 
	    lblLogo = new JLabel(new ImageIcon("stoxlogo.png"));

	    lblLogo.setBounds(200, 0, 300, 80);   
	    
		progress = new JLabel(" ");
		progress.setBounds(10,490,260,30);
		progress.setVisible(true);
		
		progressOne = new JLabel("");
		progressOne.setBounds(10,320,260,30); 
		
		lblTitle = new JLabel("");
		lblTitle.setBounds(280,40,400,100);
		
		lblStockPrice = new JLabel("");
		lblStockPrice.setBounds(20,120,300,30);
		
		lblPreviousClose = new JLabel("");
		lblPreviousClose.setBounds(20,150,300,30);
		
		lblDaysRange = new JLabel("");
		lblDaysRange.setBounds(330,150,350,30);
		
		lblTodaysOpeningPrice = new JLabel("");
		lblTodaysOpeningPrice.setBounds(330,120,300,30);
		
		lblDisplayGraph = new JLabel();
		lblDisplayGraph.setBounds(10,185,700,300);
		
		txtEnter = new JTextField();
	    txtEnter.setBounds(5,520,600,20);
	    
	    lblDate = new JLabel("");
	    lblDate.setBounds(5,25,340,20);
	    
	    lblCopyright = new JLabel("Stox © Copyright Abhi Kapoor 2018");
	    lblCopyright.setBounds(500,15,300,20);
	    
	    btnEnter = new JButton("Enter -->");
	    btnEnter.setBounds(630,520,90,20);
	    btnEnter.addActionListener(this);
	   
	    frame.setContentPane(lblNewOne);
	    frame.add(btnEnter);
	    frame.add(txtEnter);
	    frame.add(lblLogo);
	    frame.add(progress);
	    frame.add(lblTitle); 
	    frame.add(lblStockPrice);
	    frame.add(lblPreviousClose);
	    frame.add(lblDaysRange);
	    frame.add(lblTodaysOpeningPrice);
	    frame.add(lblDate); 
	    frame.add(lblDisplayGraph);
	    frame.add(lblCopyright); 
	    
	    frame.setResizable(true);
		frame.pack(); 
		frame.setSize(730,575);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 200);
		frame.setVisible(true);
	    
		frame.revalidate();
	}
	
	/**
	 * This method uses the DateFormat library built-in to Java in order to obtain today's date 
	 * and display it in the GUI 
	 */
	
	public static void displayCurrentDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		String[] dateArray = dateFormat.format(date).split("/");
		
		String month = ""; 
		
		int monthInt = Integer.parseInt(dateArray[1].trim());
		
		switch(monthInt)
		{
		case 1: 
			month = "January"; 
			break; 
		case 2: 
			month = "February"; 
			break; 
		case 3: 
			month = "March"; 
			break; 
		case 4: 
			month = "April"; 
			break; 
		case 5: 
			month = "May"; 
			break; 
		case 6: 
			month = "June"; 
			break; 
		case 7: 
			month = "July"; 
			break; 
		case 8: 
			month = "August"; 
			break; 
		case 9: 
			month = "September"; 
			break; 
		case 10: 
			month = "October"; 
			break; 
		case 11: 
			month = "November"; 
			break; 
		case 12: 
			month = "December";
			break; 	
		}
		
		String entireDate = dateArray[2] + " "+month + ", "+ dateArray[0]; 
		lblDate.setText("Today: "+entireDate);
		lblDate.setForeground(Color.BLUE);
	
	}
	
	public static void main(String[] args) throws IOException
	{
		StoxSoftware obj = new StoxSoftware();
		obj.setVisible(true);
	}
	
	/**
	 * This method is used to begin the Stox application 
	 */
	
	public static void beginStox()
	{

		
		displayCurrentDate(); 
		progress.setText("Connecting...");
		String enteredCompany = txtEnter.getText().trim().toUpperCase();
		Document finalDoc = connect(enteredCompany); 
		progress.setText("Connection Successful!");
		lblStockPrice.setText("Current Stock Price: $"+getFinancialInfo(enteredCompany));
		lblPreviousClose.setText("Previous Close: $"+getPreviousClose(enteredCompany));
		lblTodaysOpeningPrice.setText("Today's Opening Price: $"+getDailyOpeningPrice(enteredCompany));
		lblDaysRange.setText("Today's Price Fluctuation: $"+getDailyRange(enteredCompany));
		getName(enteredCompany); 
		downloadGraphImage(enteredCompany); 
		
		
		
	}
	
	public static String getFinancialInfo(String company)
	{
		Document doc = connect(company); 
		String fulltext = doc.text(); 

		Pattern x2 = Pattern.compile("Add to watchlist \\d{1,4}(.)+\\d+(\\+|\\-)+\\d+(.){1}\\d+ \\(");
		
		Matcher m = x2.matcher(fulltext);
		String extractedText = ""; 
		
		while(m.find())
			extractedText = m.group(); 
		
		if(extractedText.contains("-"))
			extractedText = extractedText.substring("add to watchlists".length(), extractedText.indexOf("-"));
		else
			extractedText = extractedText.substring("add to watchlists".length(), extractedText.indexOf("+"));
		
		return extractedText;
	}
	
	public static String getPreviousClose(String company)
	{
		Document doc = connect(company); 
		String fulltext = doc.text();
		
		Pattern x2 = Pattern.compile("Previous Close \\d{1,4}(.)+\\d{1,3} Op");
		Matcher m = x2.matcher(fulltext);
		String extractedText = ""; 
		
		while(m.find())
			extractedText = m.group(); 
		int beginning = "previous closes".length(); 
		int end = extractedText.indexOf("Op");
		extractedText = extractedText.substring(beginning, end-1);
		return extractedText; 

	}
	
	public static String getDailyOpeningPrice(String company)
	{
		Document doc = connect(company); 
		// Open 48.58 Bid 
		String fulltext = doc.text();
		
		Pattern x2 = Pattern.compile("Open \\d{1,4}(.)+\\d{1,2} Bid");
		Matcher m = x2.matcher(fulltext);
		String extractedText = ""; 
		
		while(m.find())
			extractedText = m.group(); 
		int end = extractedText.indexOf("Bid") - 1; 
		int beginning = extractedText.indexOf("Open") + "opens".length();
		extractedText = extractedText.substring(beginning, end).trim();
		
		return extractedText; 
		
	}
	
	public static String getDailyRange(String company)
	{
		//Day's Range 48.37 - 48.64 52
		Document doc = connect(company);  
		String fulltext = doc.text(); 
		
		Pattern x2 = Pattern.compile("Day's Range \\d{1,4}(.)+\\d+ - \\d{1,4}(.)+\\d+ 52");
		
		Matcher m = x2.matcher(fulltext);
		String extractedText = ""; 
		
		while(m.find())
			extractedText = m.group(); 
	
		extractedText = extractedText.substring(0, extractedText.indexOf("52")-1);
		int a = extractedText.indexOf("Day's Range") + "day's range".length(); 
		extractedText = extractedText.substring(a, extractedText.length()); 
		return extractedText; 
		
	}
	
	/**
	 * This method takes a String representing the name of the user-entered company, and connects 
	 * to the StockCharts Server to download the Graph and display it in the GUI 
	 * @param enteredCompany 
	 */
	
	public static void downloadGraphImage(String enteredCompany)
	{
		String imgURLone = "http://stockcharts.com/c-sc/sc?s=";
	    String imgURLtwo = "&p=D&b=5&g=0&i=0&r=1531741599015";
	    String finalImageURL = imgURLone + enteredCompany.trim().toUpperCase() + imgURLtwo; 
	    
	    try 
	    {
			URL imageURL = new URL(finalImageURL);
			BufferedImage c = ImageIO.read(imageURL);
		    ImageIcon image = new ImageIcon(c);
		    lblDisplayGraph.setIcon(image);
		} 
	    catch (MalformedURLException e) 
	    {
			System.out.println("Error Occured with Image URL " + e.toString());
		} 
	    catch (IOException e1) 
	    {
			System.out.println("Error Occured with Image Downloading "+e1.toString());
		}
	    catch(Exception e2)
	    {
	    	lblDisplayGraph.setText("<<< GRAPH UNAVAILABLE >>>");
	    }
	    
	    // create a JLabel to display the graph image 
	}

	
	/**
	 * This method takes the URL of the website to connect to for financial information and 
	 * returns the document object obtained after successful connection to the website 
	 * @param x A String object representing the URL 
	 * @return The Document object obtained after connecting to the website, or null if unable to connect 
	 */
	public static Document connect(String company)
	{
		try 
		{
			Document doc = Jsoup.connect("https://finance.yahoo.com/quote/"+company+"/").get();
			return doc; 
		}
		catch(NumberFormatException nfe)
		{
			System.out.println("Number Format Exception Occured: " + nfe.toString());
		}
		catch(IOException e)
		{
			System.out.println("IOException Occured: "+e.toString());
		}
		catch(Exception e1)
		{
			System.out.println("Exception Occured: "+e1.toString());
		}
		return null;
		
	}
	
	public static void getName(String company) 
	{
		Document doc; 
		String entireText = ""; 
		try 
		{
			doc = Jsoup.connect("https://finance.yahoo.com/quote/"+company+"/").get();
			entireText = doc.text();
		}
		catch(IOException e)
		{
			lblTitle.setText("Unknwon Corporation");
		}
		
		
		Pattern x2 = Pattern.compile(company+" : Summary for .+ - Y");
		
		Matcher m = x2.matcher(entireText);
		String extractedText = ""; 
		while(m.find())
			extractedText = m.group(); 
		
		String t = company+" : Summary for "; 
		String finalText = extractedText.substring(extractedText.indexOf(t) + t.length(), extractedText.indexOf(" - Y")-1);
		lblTitle.setText(finalText.trim());
		lblTitle.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
	
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getActionCommand() == "Enter -->")
		{
			progress.setText("CONNECTING...");
			beginStox(); 
			progressOne.setText("CONNECTION SUCCESSFUL!"); 
		}
	}
	
}

