import java.util.ArrayList;
import java.util.List;

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
	private static String nazwaPlikuOdczytu = "nt3.dat";
	private static String nazwaPlikuZapisu = "ampl.dat";
	private static String sciezkaOdczytu = sciezka+nazwaPlikuOdczytu;
	private static String sciezkaZapisu = sciezka+nazwaPlikuZapisu;
	private static float temperatura;
	private static int maxIter;
	private static int maxPrzebieg;
	
	public static void main(String[] args) {		
		String s = null;
		Rob rob = new Rob();
		try {
			s = args[0];

		} catch (Exception e) {
			System.out
					.println("g-generuj graf, p-generuj tylko sciezki, h-symulowane wy¿arzanie");
			s = "h";
//			return;
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

				numOfClients = 30;
				numOfTransits = 70;
				percent = 0.15;
				numOfDemands = 20;
				transitsLimit = 5;
				//sciezkaZapisu = "C:\\Users\\Teodor\\Desktop\\ampl.dat";

				return;
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

				return;

			}
			rob.tylkoSciezki();
			break;
		case "h":
			try {
				temperatura = Float.parseFloat(args[1]);
				maxIter = Integer.parseInt(args[2]);
				maxPrzebieg = Integer.parseInt(args[3]);
				sciezkaOdczytu = args[4];
			} catch (Exception e) {
				System.out
				.println("h [temperatura] [max_liczba_iteracji] [liczba_przebiegow] [wejœciowy_plik_dat]");

				temperatura = 1000f;
				maxIter = 50000;
				maxPrzebieg = 5;
				//sciezkaOdczytu = "C:\\Users\\Teodor\\Desktop\\nt3.dat";
//				return;
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
		List<Demand> demands = ampl.getDemands();

		List<List<String>> sciezki = new ArrayList<>();

		try {
			for (Demand d : demands)
				sciezki.add(graf
						.getSciezki(d.getStartVertex(), d.getEndVertex(), numOfPaths, simpleGraph));
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
		
		List<Demand> demands = ampl.getDemands();
		List<Edge> edges = ampl.getEdges();
		
		System.out.println("Tworze graf.");
		Graf graf = new Graf();
		SimpleGraph<String, DefaultEdge> simpleGraph = graf.grafNieskierowany(ampl.getWierzcholki(), ampl.getKrawedzie());
		int maxTransit = ampl.getTransitsLimit();
		
		new Wyzarzanie(demands, edges, simpleGraph, graf, maxTransit, temperatura, maxIter, maxPrzebieg);
	}

}
