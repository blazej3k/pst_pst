import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
				//sciezkaZapisu = "C:\\Users\\Teodor\\Desktop\\ampl.dat";

				//return;
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
				//sciezkaOdczytu = "C:\\Users\\Teodor\\Desktop\\nt3.dat";
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
		
//		for (Edge e: edges) 
//			System.out.println("Sciezka Edge: "+e.getStartVertex()+" "+e.getEndVertex());
		
		System.out.println("Tworze graf.");
		Graf graf = new Graf();
		SimpleGraph<String, DefaultEdge> simpleGraph = graf.grafNieskierowany(ampl.getWierzcholki(), ampl.getKrawedzie());

		System.out.println("Iloœæ demandów: "+demands.size());
		
		Demand dem = demands.get(0);											// wybieram losowo demand
		List<String> sciezkaX = rozwiazanieInicjalne(dem, graf, simpleGraph);		// sciezki w durnych listach stringow - wezlow
		List<String> sciezkaY;
		List<Edge> eX = sciezkaE(sciezkaX, edges);								// zamiana na obiekty Edge - gites
		for (Edge e: eX) 
			System.out.println("Init eX "+e.getStartVertex()+" "+e.getEndVertex()+" instalacja: "+e.getInstallationCost()+" u¿ywanko: "+e.getUnitCost());
		
		List<Edge> eY;
		
		float temperatura = 10;
		int count=0;
		float fkosztX=funkcjaKosztu(dem, eX);
		float fkosztY=0;
		float delta=0;
		float alfa = 0.5f;
		
		System.out.println("Inicjalna funkcja kosztu: "+fkosztX);
		
		while(count < 10) {													// kryterium stopu
			sciezkaY = graf.znajdzLosowaSciezke(dem.getStartVertex(), dem.getEndVertex(), 10, simpleGraph);		// losowa sciezka, = otoczenie punktu (rozwiazania) nalezace do zbioru rozwiazan
			eY = sciezkaE(sciezkaY, edges);
			fkosztY = funkcjaKosztu(dem, eY);
			
			delta = fkosztY - fkosztX;

			if (delta > 0) {										// jesli nowa lepsza lub metro ok to wtedy przyjmij nowe rozwiazanie
				eX = eY;
				fkosztX = fkosztY;
			}	
			else if (getRandom() > Math.exp(-delta/temperatura)) {	// metropolis test - chuk wie cio, Pjura³ke tak ka¿e
				eX = eY;
				fkosztX = fkosztY;
			}
			
			temperatura *= alfa;
			count++;
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Ostateczna funkcja kosztu: "+fkosztX);
		System.out.println("Sciezka: ");
		for (Edge e: eX) {
			System.out.println(e.getStartVertex()+" "+e.getEndVertex()+" instalacja: "+e.getInstallationCost()+" u¿ywanko: "+(e.getUnitCost()*dem.getDemandVal()));
		}
	}

	private float getRandom() {
		return new Random().nextFloat();
	}

	float funkcjaKosztu(Demand dem, List<Edge> sciezka) {		// liczy funkcje KOSZTU, na razie dla jednego demandu
		float wynik=0;
		// zysk - koszt
		float zysk = dem.getDemandProfit();
		float koszt=0;
		float wielkosc = dem.getDemandVal();
		
		for (Edge x: sciezka) {					// sumowanie kosztow instalacji
			if(!x.getCzyZainstalowany()) {		// jesli nie jest jeszcze zainstalowany, to zainstaluj i dodaj koszt montazu
				koszt += x.getInstallationCost();
//				x.setCzyZainstalowany(true);
				System.out.println("Dodaje staly: "+x.getInstallationCost());
			}
			
			float dodaj = x.getUnitCost() * wielkosc;
			System.out.println("Dodaje jednostkowy: "+dodaj);
			koszt += x.getUnitCost() * wielkosc;
		}
		
		System.out.println("Koszt: "+koszt+" zaplata: "+zysk);
		wynik = zysk - koszt;
		System.out.println("F kosztu: "+wynik);
		return wynik;
	}
	
	private List<Edge> sciezkaE (List<String> sciezka, List<Edge> edges) {		// przetwarza sciezke z durnej listy Stringow na liste obiektow Edge
		LinkedList<Edge> sciezkaE = new LinkedList<Edge>();		
		String start, end;
		
		for (int i=0; i < sciezka.size()-1; i++) {
			start = sciezka.get(i);
			end = sciezka.get(i+1);
			
			for (Edge e: edges)						// poszukiwanie krawedzi na pelnej liscie krawedzi i dodawanie jej do listy 
				if (e.getStartVertex().equals(start) && e.getEndVertex().equals(end)) 
					sciezkaE.add(e);
				else if (e.getStartVertex().equals(end) && e.getEndVertex().equals(start)) //tak bylo latwiej zrobic odwrotny warunek niz kombinowac na operatorach logicznych
					sciezkaE.add(e);
		}
		
		System.out.println("Otrzymana scie¿ka: "+ sciezka);
		System.out.println("Sciezka Edge: ");
		for (Edge e: sciezkaE) {
			System.out.println(e.getStartVertex()+" "+e.getEndVertex());
		}
		
		return sciezkaE;
	}
	
	public List<String> rozwiazanieInicjalne(Demand demand, Graf graf, SimpleGraph<String, DefaultEdge> simpleGraph) {
		List<String> sciezkaSlaba = new LinkedList<String>();
		List<String> sciezka = new LinkedList<String>();
		String[] krawedz;
		
		System.out.println("DEMAND: "+demand.getStartVertex()+" "+demand.getEndVertex()+" wielkosc: "+demand.getDemandVal()+" zaplata: "+demand.getDemandProfit());

		try {
				//rozwi¹zanie pocz¹tkowe - tu najkrótsza œcie¿ka, nie najtañsza
				sciezkaSlaba = graf.getSciezki(demand.getStartVertex(), demand.getEndVertex(), 1, simpleGraph);
				String wezelA, wezelB;
				
				System.out.println();
				System.out.println("GENERUJE ROZWIAZANIE POCZATKOWE");
				for (String x: sciezkaSlaba) {
					x = x.replace("(", "");
					x = x.replace(")", ""); // usuniecie nawiasow
					krawedz = x.split(":");
					
					wezelA=krawedz[0].trim();
					wezelB=krawedz[1].trim();
					Boolean dodawajA = true, dodawajB=true;

					for(String d: sciezka) {	// jezeli znajdziesz juz taki wezel to go nie dodawaj
						if (wezelA.equals(d))
							dodawajA = false;
						if (wezelB.equals(d))
							dodawajB = false;
					}
					
					if(dodawajA)
						sciezka.add(wezelA);
					if(dodawajB)
						sciezka.add(wezelB);
				}
			return sciezka;
		} catch (NullPointerException e) {
			System.out
			.println("Nie mo¿na wygenerowaæ ¿adnej œcie¿ki dla jednego z zapotrzebowañ");
			return null;
		} 
	}
}
