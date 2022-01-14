import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

//reading SSL statistics
//https://www.ssl.se/statistik/spelare?season=2021&gameType=regular&series=qRl-8B5kOFjKL
public class SSL {
	private static final String[] YEARS = {"2021", "2020", "2019", "2018", "2017"};
	//kOFjKL = men, wsw8lj = women
	private static final String[] GENDER = {"kOFjKL", "wsw8lj"};
	
	//method to read a given web page
	private static String read(String webPage) throws IOException {
		URL url = new URL(webPage);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
	}
	
	//method to read out statistics from the loaded web page for SSL
	private static void handleHtml(String html) {
		Scanner sc = new Scanner(html);
		String line = null;
		
		String player = null;
		String team = null;
		
		//decide whether I have already read out everything I wanted to
		boolean canOut = false;
		//remember that you already have team read out and you go for statistics now
		boolean teamRead = false;
		//statistic == 2 is games, 3 is goals, 4 is assists, 5 is total points, 6 is penalty minutes
		int statistic = 0;
		int games = -1;
		int goals = -1;
		int assists = -1;
		int tp = -1;
		int pim = -1;
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.contains("rmss_t--pinned-hide")) {
				//System.out.println(line);
				//if it is player, then remember player
				//otherwise remember team
				if (line.contains("spelare")) {
					player = handleLine(line);
				} else {
					team = handleLine(line);
					//what follows are the statistics
					teamRead = true;
				}	
			} else if (teamRead && line.contains("rmss_t-stat-table__row-item")) {
				statistic++;
				int st = statistic % 6;
				if (st == 2)
					games = Integer.parseInt(handleLine(line));
				else if (st == 3)
					goals = Integer.parseInt(handleLine(line));
				else if (st == 4)
					assists = Integer.parseInt(handleLine(line));
				else if (st == 5)
					tp = Integer.parseInt(handleLine(line));
				else if (st == 0 && statistic > 0) {
					pim = Integer.parseInt(handleLine(line));
					teamRead = false;
				}
				
				//you set teamRead to be false once you finish reading statistic for a player
				if (teamRead == false)
					System.out.println(player + "  " + team + "  " + games + "  " + goals + " " + assists + " " + tp + "  " + pim);
			}
		}
		sc.close();
	}
	
	//method to read name, team or concrete statistics from a line from html
	private static String handleLine(String line) {
		//find where the name of player begins through checking the parenthesis
		int par = 0;
		//check only after the first <
		boolean start = false;
		for (int j = 0; j < line.length(); j++) {
			Character c = line.charAt(j);
			if (line.charAt(j) == '<') {
				par++;
				start = true;
				continue;
			}
			if (line.charAt(j) == '>') {
				par--;
				continue;
			}
			if (par == 0 && start) {
				StringBuilder sb = new StringBuilder();
				while (line.charAt(j) != '<') {
					sb.append(line.charAt(j));
					j++;
				}
				return sb.toString();
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		Locale.setDefault(new Locale("SWEDISH"));
		String link = null;
		String htmlSSL = null;
		for (int j = 0; j < GENDER.length; j++) {
			if (GENDER[j].equals("wsw8lj")) {
				System.out.println("WOMEN");
				for (int i = 0; i < YEARS.length; i++) {
					link = "https://www.ssl.se/statistik/spelare?season=" + YEARS[i] + "&team=All&gameType=regular&series=qRl-8B5" + GENDER[j];
					System.out.println(YEARS[i]);
					htmlSSL = read(link);
					handleHtml(htmlSSL);
					System.out.println("----------------------------------------");
				}
			} else {
				System.out.println("MEN");
				for (int i = 0; i < YEARS.length; i++) {
					link = "https://www.ssl.se/statistik/spelare?season=" + YEARS[i] + "&team=All&gameType=regular&series=qRl-8B5" + GENDER[j];
					System.out.println(YEARS[i]);
					htmlSSL = read(link);
					handleHtml(htmlSSL);
					System.out.println("----------------------------------------");
				}
				System.out.println("----------------------------------------");
				System.out.println();
				System.out.println("----------------------------------------");
			}
		}

	}
}
