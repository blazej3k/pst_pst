import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class AmplText {

	private List<String> wierzcholki;
	private List<String> krawedzie;

	private String[][] demands;

	private String sciezkaOdczytu;
	private String sciezkaZapisu;
	private Scanner odczyt;
	private PrintWriter zapis;

	private Random rand = new Random();

	public AmplText(String sciezkaOdczytu, String sciezkaZapisu) {
		this.sciezkaOdczytu = sciezkaOdczytu;
		this.sciezkaZapisu = sciezkaZapisu;
		wierzcholki = new ArrayList<String>();
		krawedzie = new ArrayList<String>();
	}

	public AmplText(String sciezkaZapisu) {
		this.sciezkaZapisu = sciezkaZapisu;
	}

	public void odczytajPlik() {
		try {
			odczyt = new Scanner(new File(sciezkaOdczytu));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String linia = "";
		Boolean data = false;

		while (odczyt.hasNextLine()) {
			linia = odczyt.nextLine();

			try {
				while (!linia.endsWith(";")) {// wczytuj az napotkasz srednik
					// System.out.println("Linia: "+linia);
					linia += "\n" + odczyt.nextLine().trim();
				}
			} catch (NoSuchElementException e) {
				// e.printStackTrace();
			}

			switch (linia) {
			case "data;":
				data = true;
				break;
			default:
				if (!data)
					break; // sprawdza czy 'data;' na poczatku, w sensie czy
							// dobry plik czytamy

				if ((linia.contains("CLIENTS")) || (linia.contains("TRANSITS"))) {
					linia = linia.replaceAll(";", "");
					dodajElement(linia, true); // wêz³y
					// System.out.println("wierzcho³ek");
				} else if (linia.contains("LINKS1")) {
					linia = linia.replaceAll(";", "");
					linia = linia.replaceAll(",", "");
					linia = linia.replaceAll("\\(", "");
					linia = linia.replaceAll("\n", "");
					dodajElement(linia, false);
					// System.out.println("KrawêdŸ");
				} else if (linia.contains("DEMANDS")) {
					odczytajDemandy(linia);
				} else
					break;
			}
		}
		odczyt.close();
	}

	private void odczytajDemandy(String linia) {
		linia = linia.replaceAll(";", "");
		String[] lista = linia.split(":=")[1].split("\\)");

		demands = new String[lista.length][3];

		for (int i = 0; i < lista.length; i++) {
			String[] demand = lista[i].split(",");
			demand[0] = demand[0].trim();
			String v1 = demand[0].substring(1, demand[0].length());
			String v2 = demand[1].trim();
			demands[i][0] = v1;
			demands[i][1] = v2;
			demands[i][2] = "";	
			// TODO wpisuje pusta wartosc i potem sobie ja modyfikuje w heurystyce, zrob tu zeby sie wczytywalo
		}
	}

	private void dodajElement(String linia, Boolean tryb) {
		List<String> lista = null;
		StringTokenizer pole = new StringTokenizer(linia, ":=");
		String wartosc;

		if (tryb)
			lista = wierzcholki;
		if (!tryb)
			lista = krawedzie;

		if (tryb) {
			String[] vertexy = null;
			while (pole.hasMoreTokens()) {
				pole.nextToken().trim();
				wartosc = pole.nextToken().trim();
				vertexy = wartosc.split(" ");

				for (String x : vertexy)
					lista.add(x);
			}
		} else {
			String[] edgesy = null;
			while (pole.hasMoreTokens()) {
				pole.nextToken().trim();
				wartosc = pole.nextToken().trim();
				edgesy = wartosc.split("\\)");

				for (String x : edgesy)
					lista.add(x.trim());
			}
		}
	}

	public void zapis(List<List<String>> sciezki) {
		try {
			odczyt = new Scanner(new File(sciezkaOdczytu));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			zapis = new PrintWriter(sciezkaZapisu);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String linia = "";

		while (odczyt.hasNextLine()) {
			linia = odczyt.nextLine();
			try {
				while (!linia.endsWith(";")) {// wczytuj az napotkasz srednik
					linia += "\n" + odczyt.nextLine().trim();
				}

				if (!linia.contains("LINKS2") && !linia.contains("unit_cost2")) {
					if (linia.contains("LINKS1"))
						linia = linia.replace("LINKS1", "LINKS");
					if (linia.contains("unit_cost1"))
						linia = linia.replace("unit_cost1", "unit_cost")
								.replace("installation_cost1",
										"installation_cost");
					if (linia.contains("demand_val")) {

						String[] wiersze = linia.split("\n");
						int j = 0;
						while (!wiersze[j].contains("param"))
							j++;

						linia = "param: 		demand_val 	demand_profit	number_of_paths :=";
						for (int i = 0; i < sciezki.size(); i++)
							linia += "\n" + wiersze[j + 1 + i].replace(";", "").trim() + "			"
									+ sciezki.get(i).size();
						linia += ";";
					}

					printLines(zapis, linia);
				}
			} catch (NoSuchElementException e) {
				// e.printStackTrace();
			}
		}

		for (int i = 0; i < sciezki.size(); i++) {
			List<String> sciezkiDemandu = sciezki.get(i);

			for (int j = 0; j < sciezkiDemandu.size(); j++) {
				String sciezka = sciezkiDemandu.get(j);
				linia = "set PATHS[" + demands[i][0] + ", " + demands[i][1]
						+ ", " + Integer.toString(j + 1) + "] := ";
				sciezka = sciezka.replaceAll("\\[", "").replaceAll("\\]", "")
						.replaceAll(", ", "").replaceAll(" :", ",");

				linia += sciezka + ";";

				zapis.println(linia);
			}
		}
		odczyt.close();
		zapis.close();

	}

	public void generujPlik(SimpleGraph<String, DefaultEdge> graf,
			String sciezka, int numOfClients, int numOfTransits,
			int numOfDemands, int transitsLimit) {
		PrintWriter zapis = null;
		try {
			zapis = new PrintWriter(sciezka);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String linia = "data;";
		zapis.println(linia);

		// zbiór wezlow klienckich
		linia = "set CLIENTS :=";
		for (int i = 1; i <= numOfClients; i++)
			linia += " K" + i;
		linia += ";";
		zapis.println(linia);

		// zbiór wezlow tranzytowych
		linia = "set TRANSITS :=";
		for (int i = 1; i <= numOfTransits; i++)
			linia += " T" + i;
		linia += ";";
		zapis.println(linia);

		// zbior ³¹czy (w jedna i w druga strone) i ich parametry
		String text = wygenerujLacza(graf);
		printLines(zapis, text);

		// zbior zapotrzebowan i ich parametry
		text = wygenerujZapotrzebowania(numOfClients, numOfDemands);
		printLines(zapis, text);

		// limit na wez³y tranzytowe
		zapis.println("param transit_nodes_limit := " + transitsLimit + ";");

		zapis.close();

	}

	public String wygenerujLacza(SimpleGraph<String, DefaultEdge> graf) {

		String links1 = "\nset LINKS1 := ";
		String links2 = "\nset LINKS2 := ";

		String param1 = "\nparam:		unit_cost1	installation_cost1 :=";
		String param2 = "\nparam:		unit_cost2	installation_cost2 :=";

		for (DefaultEdge edge : graf.edgeSet()) {
			String e = edge.toString();

			e = e.replaceAll("\\(", "").replaceAll("\\)", "");
			String[] v = e.split(":");
			String v1 = v[0].trim();
			String v2 = v[1].trim();

			links1 += "(" + v1 + ", " + v2 + ")";
			links2 += "(" + v2 + ", " + v1 + ")";

			float unitCost = ((float) randInt(1, 10)) / 10;
			int instCost = randInt(1, 50);

			param1 += "\n" + v1 + " " + v2 + "		" + unitCost + "		" + instCost;
			param2 += "\n" + v2 + " " + v1 + "		" + unitCost + "		" + instCost;

		}

		links1 += ";";
		links2 += ";";
		param1 += ";";
		param2 += ";";

		return links1 + links2 + param1 + param2;

	}

	public String wygenerujZapotrzebowania(int numOfClients, int numOfDemands) {

		String demands = "\nset DEMANDS := ";
		String param = "\nparam: 		demand_val 	demand_profit:=";

		for (int i = 0; i < numOfDemands; i++) {

			int start = randInt(1, numOfClients);
			int end = randInt(1, numOfClients);
			String d = "(" + "K" + start + ", " + "K" + end + ")";
			while (demands.contains(d) || start == end) {
				start = randInt(1, numOfClients);
				end = randInt(1, numOfClients);
				d = "(" + "K" + start + ", " + "K" + end + ")";
			}

			demands += d;

			int demandVal = randInt(1, 100);
			int demandProfit = randInt(100, 1000);

			param += "\nK" + start + " K" + end + "		" + demandVal + "		"
					+ demandProfit;
		}

		demands += ";";
		param += ";";

		return demands + param;

	}

	public int randInt(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public void printLines(PrintWriter zapis, String text) {
		for (String line : text.split("\n"))
			zapis.println(line);
	}

	public List<String> getWierzcholki() {
		return wierzcholki;
	}

	public void setWierzcholki(List<String> wierzcholki) {
		this.wierzcholki = wierzcholki;
	}

	public List<String> getKrawedzie() {
		return krawedzie;
	}

	public void setKrawedzie(List<String> krawedzie) {
		this.krawedzie = krawedzie;
	}

	public String[][] getDemands() {
		return demands;
	}

	public void setDemands(String[][] demands) {
		this.demands = demands;
	}

}
