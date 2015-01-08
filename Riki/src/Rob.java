import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Rob {

	private static int numOfClients;
	private static int numOfTransits;
	private static double percent;
	private static int numOfDemands;
	private static int transitsLimit;
	private static int numOfPaths;
	private static String sciezka = "res/";
	private static String nazwaPlikuOdczytu = "ampl.dat";
	private static String nazwaPlikuZapisu = "ampl.dat";
	private static String sciezkaOdczytu = sciezka+nazwaPlikuOdczytu;
	private static String sciezkaZapisu = sciezka+nazwaPlikuZapisu;

	public static void main(String[] args) {
		String s = null;
		Rob rob = new Rob();
		try {
			s = args[0];

		} catch (Exception e) {
			System.out
					.println("g-generuj graf, p-generuj tylko sciezki, h-symulowane wy¿arzanie");
			s = "h";
			// return;
		}
		switch (s) {
		case "g":
			try {
				numOfClients = Integer.parseInt(args[1]);
				numOfTransits = Integer.parseInt(args[2]);
				percent = Float.parseFloat(args[3]);
				numOfDemands = Integer.parseInt(args[4]);
				transitsLimit = Integer.parseInt(args[5]);
				sciezkaZapisu = args[6];

			} catch (Exception e) {
				System.out
						.println("g [liczba_K] [liczba_T] [wypelnienie] [liczba_D] [max_liczba_T] [wyjsciowy_plik_dat]");

				numOfClients = 3;
				numOfTransits = 7;
				percent = 0.3;
				numOfDemands = 3;
				transitsLimit = 5;
//				sciezkaZapisu = "C:\\Users\\Teodor\\Desktop\\ampl.dat";

//				return;
			}
			rob.nowyGraf();
			break;

		case "p":
			try {
				sciezkaOdczytu = args[1];
				sciezkaZapisu = args[2];
				numOfPaths = Integer.parseInt(args[3]);
			} catch (Exception e) {
				System.out
						.println("p [wejsciowy_plik_dat] [wyjsciowy_plik_dat] [liczba_sciezek]");

				numOfPaths = 10;
				sciezkaZapisu = "C:\\Users\\Teodor\\Desktop\\ampl1.dat";
				sciezkaOdczytu = "C:\\Users\\Teodor\\Desktop\\t1.dat";

				//return;

			}
			rob.tylkoSciezki();
			break;
		case "h":
			try {
				sciezkaOdczytu = args[1];
			} catch (Exception e) {
//				sciezkaOdczytu = "C:\\Users\\Teodor\\Desktop\\nt3.dat";
			}
			rob.heurystyka();
			break;
		default:
			System.out
					.println("g-generuj graf, p-generuj œcie¿ki, h-symulowane wy¿arzanie");
		}

	}

	public void nowyGraf() {

		Graf graf = new Graf(numOfClients, numOfTransits, percent);
		SimpleGraph<String, DefaultEdge> simpleGraph = graf.generujGraf();

		AmplText ampl = new AmplText(sciezkaZapisu);
		ampl.generujPlik(simpleGraph, sciezkaZapisu, numOfClients,
				numOfTransits, numOfDemands, transitsLimit);

	}

	public void tylkoSciezki() {
		AmplText ampl = new AmplText(sciezkaOdczytu, sciezkaZapisu);
		ampl.odczytajPlik();

		System.out.println("Tworze graf.");
		Graf graf = new Graf();
		SimpleGraph<String, DefaultEdge> simpleGraph = graf.grafNieskierowany(
				ampl.getWierzcholki(), ampl.getKrawedzie());
		String[][] demands = ampl.getDemands();

		List<List<String>> sciezki = new ArrayList<>();

		try {
			for (String[] d : demands)
				sciezki.add(graf
						.getSciezki(d[0], d[1], numOfPaths, simpleGraph));
		} catch (NullPointerException e) {
			System.out
					.println("Nie mo¿na wygenerowaæ ¿adnej œcie¿ki dla jednego z zapotrzebowañ");
			return;
		}

		ampl.zapis(sciezki);
	}

	private void heurystyka() {
		AmplText ampl = new AmplText(sciezkaOdczytu, null);
		ampl.odczytajPlik();

		System.out.println("Tworze graf.");
		Graf graf = new Graf();
		SimpleGraph<String, DefaultEdge> simpleGraph = graf.grafNieskierowany(ampl.getWierzcholki(), ampl.getKrawedzie());
		String[][] demands = ampl.getDemands();
		System.out.println("Iloœæ demandów: "+demands.length);
		
		
		int temperatura = 10;
		int count=0;
		List<List<String>> rozwiazania = rozwiazanieInicjalne(demands, graf, simpleGraph);
		String[] dem = demands[0];											// wybieram losowo demand
		dem[2] = "50";														// wpisuje wielkosc demandu, TODO odczyt z pliku
		
		int wielkoscDem = Integer.parseInt(dem[2]);
		String nowaY="";
		
		while(count < 20) {													// kryterium stopu
			nowaY = graf.znajdzLosowaSciezke(dem[0], dem[1], 10, simpleGraph);		// losowa sciezka, = otoczenie punktu (rozwiazania) nalezace do zbioru rozwiazan

			
			
			count++;
		}
	}

	private int funkcjaKosztu() {
		int koszt=0;
		
		
		
		return koszt;
	}
	
	public List<List<String>> rozwiazanieInicjalne(String[][] demands, Graf graf, SimpleGraph<String, DefaultEdge> simpleGraph) {
		List<List<String>> rozwiazania = new LinkedList<List<String>>();
		
		for (String[] d: demands)
			for (String s: d)
				System.out.println("DEMAND: "+d+" zawartosc: "+s);

		try {
			for (String[] d : demands) {
				//rozwi¹zanie pocz¹tkowe - tu najkrótsza œcie¿ka, nie najtañsza
				rozwiazania.add(graf.getSciezki(d[0], d[1], 1, simpleGraph));
				
				return rozwiazania;
			}
		} catch (NullPointerException e) {
			System.out
			.println("Nie mo¿na wygenerowaæ ¿adnej œcie¿ki dla jednego z zapotrzebowañ");
			return null;
		} 
		
		return null;
	}

}
