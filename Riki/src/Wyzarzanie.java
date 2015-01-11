import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class Wyzarzanie {

	private List<Demand> demands;
	private List<Edge> edges;
	private SimpleGraph<String, DefaultEdge> simpleGraph;
	private Graf graf;
	private int maxTransit;
	private int maxPrzebieg;
	private List<Float> wyniki;
	private float orygTemp;

	public Wyzarzanie(List<Demand> demands, List<Edge> edges, SimpleGraph<String, DefaultEdge> simpleGraph, Graf graf, int maxTransit, float orygTemp, int maxIter, int maxPrzebieg) {
		this.demands = demands;
		this.edges = edges;
		this.simpleGraph = simpleGraph;
		this.graf = graf;
		this.maxTransit = maxTransit;
		this.maxPrzebieg = maxPrzebieg;
		this.orygTemp = orygTemp;

		System.out.println("Wyzarzanie.");
		System.out.println("Demandow: "+demands.size());
		System.out.println("Krawedzi: "+edges.size());
		System.out.println("Maksymalna liczba wezlow tranzytowych: "+maxTransit);
		System.out.println("Wprowadzona temperatura: "+orygTemp);
		System.out.println("Liczba iteracji w przbiegu: " + maxIter);
		System.out.println("Liczba przebieg�w: " + maxPrzebieg);
		
		wyniki = new LinkedList<Float>();
		
		heurystyka(orygTemp, maxIter);
	}
	
	private void heurystyka(float temperatura, int maxIter) {
		float sKosztX=0, sKosztY=0;
		Demand dem;
		
		System.out.println("Ilo�� demandow: "+demands.size());

		int count=0;
		float delta=0;
		float alfa = 0.95f;				// funkcja chlodzenia, 
		int wybor=0;
		int ilePrzebiegow=0;				// pomaga wyznaczyc optymalne wartosci temperatury i alfy.
		int ileZaru=0;
		List<Edge> tempEdgeList;
		List<Edge> nowaSciezka;
		Boolean tempCzyRealizowany;
		
//		demands.remove(0);
		
		while (ilePrzebiegow < maxPrzebieg) {					// kryterium stopu, do poszukiwania lepszych parametr�w
			count=0;											// przebiegi musza byc niezalezne, resetuj wszystko
			ilePrzebiegow++;
			
			temperatura = orygTemp;
			delta=0;
			wybor=0;
			ileZaru=0;
			rozwiazanieInicjalne();
			sKosztX=sumFunkcjaKosztu();	// inicjalna funkcja kosztu
			sKosztY=0;
			System.out.println("Inicjalna funkcja kosztu: "+ sKosztX);
			
			while(count < maxIter) {					// kryterium stopu, mozna dodac dodatkowe w while obejmujacym ten. np czasowe albo wynik powyzej jakiegos
				wybor = ktoryDemand();
				dem = demands.get(wybor);
				tempEdgeList = dem.getEdgeList();	// zachowaj stara liste sciezek
				tempCzyRealizowany = dem.getCzyRealizowany();
				setCzyRealizowac(wybor);

				System.out.println("Wybra�em demand: "+dem.getStartVertex()+" "+dem.getEndVertex()+" "+dem.getCzyRealizowany());		
				if(dem.getCzyRealizowany()) {
					nowaSciezka = graf.znajdzLosowaSciezke(wybor, maxTransit, simpleGraph, edges, demands);			// losowa sciezka, = otoczenie punktu (rozwiazania) nalezace do zbioru rozwiazan
					ustawSciezke(dem, nowaSciezka);
				}
				
				sKosztY = sumFunkcjaKosztu();
				System.out.println(sKosztY);
				delta = sKosztY - sKosztX;


				if (delta < 0) {										// jesli nowe gorsze, maksymalizuje f kosztu, przywroc stara trase;
//					ustawSciezke(dem, tempEdgeList);
					dem.setEdgeList(tempEdgeList);						// przy przywracaniu musi miec mozliwosc ustawienia z powrotem NULL-a
					dem.setCzyRealizowany(tempCzyRealizowany);
					System.out.println("KosztY="+sKosztY+" KosztX="+sKosztX);

				}	
				else if (getFloatRandom() <= Math.exp(-(delta/temperatura))) {	// metropolis test - chuk wie cio, Pjura�ke tak ka�e, jesli on true, to tez przywroc stara sciezke
//					ustawSciezke(dem, tempEdgeList);							// jesli wypadnie float > exp, to przyjmij gorsze, skoro <= to przywroc stare lepsze
					dem.setCzyRealizowany(tempCzyRealizowany);
//					System.out.println("�ar!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					ileZaru++;
				}
				else {
					sKosztX = sKosztY;												// je�li delta > 0, czyli nowe jest lepsze to zachowaj nowa trase (nie przywracaj starej) i aktualizuj funkcje kosztu
					System.out.println("Mam lepsza opcje, KosztX="+sKosztX+", KosztY="+sKosztY);
				}
				temperatura *= alfa;			// chlodzenie
				count++;
			}
			sKosztX = sumFunkcjaKosztu();			// przeliczanie kontrolne, sprawdza czy sie parametry zachowuja
			wyniki.add(sKosztX);
			
			System.out.println();
			System.out.println("Demandy: ");
			for (Demand d: demands)					// wyswietla wynik biezacego przebiegu
			{
				System.out.println("Demand: "+d.getStartVertex()+" "+d.getEndVertex());
				System.out.println("Realizowany: "+d.getCzyRealizowany());
				System.out.println("Tranzyty: "+d.getTransitNodes());
				String trasa="";	// wiem ze nieoptymalne ;]
				if (d.getEdgeList() != null)
					for (Edge e: d.getEdgeList()) {
						trasa += e.getStartVertex() + " " +e.getEndVertex() + ", ";
					}
				
				System.out.println("Trasa: "+trasa);
			}
			System.out.println();
			System.out.println("Funkcja kosztu przebiegu "+ilePrzebiegow+" wynosi "+sKosztX+" Zar pracowal "+ileZaru+" razy.");
			System.out.println();
			System.out.println();
			System.out.println();
		}

		float suma=0;
		for (Float x: wyniki) {
			suma += x;
		}
		suma /= wyniki.size(); // usrednienie wyniku
		
		System.out.println();
		System.out.println("Ostateczna funkcja kosztu: "+ suma +" znaleziona w "+ilePrzebiegow+" przebiegow,"+" po "+count+" iteracji.");

	}

	private Boolean ustawSciezke(Demand d, List<Edge> nowaSciezka) {

		try {
			d.setEdgeList(nowaSciezka);

		}
		catch (NullPointerException e) {/*
			System.out.println(
					"Nie mo�na wygenerowa� �adnej �cie�ki dla jednego z zapotrzebowa�");
			System.out.println("Wy��czam zapotrzebowanie: "+ d.getStartVertex()+" "+d.getEndVertex());*/
			d.setCzyRealizowany(false);
		}
		return null;


	}
	
	private int ktoryDemand() {			
		float bound = demands.size();								// granica zeby nie przekraczac indeksu
		int wybor = getIntRandom(bound);							// wybieram demand
		
//		System.out.println("Wybieram Dem: "+wybor);
		
		return wybor;
	}
	
	private void setCzyRealizowac(int wybor) {
		Boolean czyRealizowac = getBooleanRandom();
		demands.get(wybor).setCzyRealizowany(czyRealizowac);
	}
	
	private Boolean getBooleanRandom() {
		return new Random().nextBoolean();
	}
	
	private int getIntRandom(float bound) {
		return new Random().nextInt((int) bound);
	}
	
	private float getFloatRandom() {
		return new Random().nextFloat();	
	}
	
	private float sumFunkcjaKosztu() {
		float kosztInstalacji=0;
		float kosztUzycia=0;
		float profit=0;
		float koszt=0;
		float wynik=0;
		
		Set<Edge> wyjatkoweKrawedzie = new TreeSet<Edge>();
		List<Edge> uzyteKrawedzie = new LinkedList<Edge>();
		
		for (Demand d: demands) {
//			if (d.getCzyRealizowany()) {						// jesli ma nie byc realizowany to po prostu nie bedzie do sumowany do funkcji kosztu, nie zostana uzyte jego krawedzie itd
			if (d.getEdgeList() != null) {
				wyjatkoweKrawedzie.addAll(d.getEdgeList());										// powoduje DODANIE wyj�tkowych kraw�dzi
				uzyteKrawedzie = d.getEdgeList();												// tu zostaj� WYBRANE krawedzie demandu do przeliczenia kosztu instalacji
				profit += d.getDemandProfit();													// sumuje profity

				for (Edge e: uzyteKrawedzie) {
					kosztUzycia += e.getUnitCost() * d.getDemandVal();							// sumuje koszty uzycia
				}
			}
		}
		
		for (Edge e: wyjatkoweKrawedzie) {													// koszty instalacji
//			System.out.println("Kraw�d�: "+e.getStartVertex()+" "+e.getEndVertex());
			kosztInstalacji += e.getInstallationCost();
		}
		
		koszt = kosztInstalacji + kosztUzycia;
		wynik = profit - koszt;
		
//		System.out.println("Funkcja kosztu: "+wynik);
		return wynik;
	}

	public void rozwiazanieInicjalne() {
		System.out.println("Inicjalne: ");
		List<Edge> nowaSciezka = new LinkedList<Edge>() ;
			
		for (Demand d: demands) {
			d.setCzyRealizowany(true);
			System.out.println("Demand: "+d.getStartVertex()+" "+d.getEndVertex()+" "+d.getCzyRealizowany());
			nowaSciezka = graf.znajdzNajkrotszaSciezke(demands.indexOf(d), simpleGraph, edges, demands, maxTransit);
			
//			for (Edge e: nowaSciezka)
//				System.out.println("Sciezka: "+e.getStartVertex()+" "+e.getEndVertex());
			
			ustawSciezke(d, nowaSciezka);			
		}
		
		System.out.println();
		System.out.println();
	}
}


/*	private List<Edge> sciezkaE (List<String> sciezka, List<Edge> edges) {		// przetwarza sciezke z durnej listy Stringow na liste obiektow Edge
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

System.out.println("Otrzymana scie�ka: "+ sciezka);
System.out.println("Sciezka Edge: ");
for (Edge e: sciezkaE) {
	System.out.println(e.getStartVertex()+" "+e.getEndVertex());
}

return sciezkaE;
}*/
/*	// liczy funkcje kosztu dla pojedynczego demandu
private float funkcjaKosztu(Demand dem) {		// liczy funkcje kosztu dla jednego demandu, jest to tylko porownawczo, nie interesuja ja czy zainstalowany itd
	List<Edge> sciezka = dem.getEdgeList();
	float wynik=0;
	// zysk - koszt
	float zysk = dem.getDemandProfit();
	float koszt=0;
	float wielkosc = dem.getDemandVal();
	
	for (Edge x: sciezka) {						// sumowanie kosztow instalacji
			koszt += x.getInstallationCost();	// funkcje nie interesuje czy zainstalowany
			System.out.println("Dodaje staly: "+x.getInstallationCost());
		
		
		float dodaj = x.getUnitCost() * wielkosc;
		System.out.println("Dodaje jednostkowy: "+dodaj);
		koszt += x.getUnitCost() * wielkosc;
	}
	
	System.out.println("Koszt: "+koszt+" zaplata: "+zysk);
	wynik = zysk - koszt;
	System.out.println("F kosztu: "+wynik);
	return wynik;
}
*/
