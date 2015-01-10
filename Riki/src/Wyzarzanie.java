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

	public Wyzarzanie(List<Demand> demands, List<Edge> edges, SimpleGraph<String, DefaultEdge> simpleGraph, Graf graf) {
		this.demands = demands;
		this.edges = edges;
		this.simpleGraph = simpleGraph;
		this.graf = graf;

		heurystyka();
	}
	
	private void heurystyka() {
		float sKosztX=0, sKosztY=0;
		Demand dem;
		
		System.out.println("Iloœæ demandów: "+demands.size());

		rozwiazanieInicjalne();		// wszystkie demandy otrzymuj¹ rozwi¹zania inicjalne tj. najkrótsze œcie¿ki wzglêdem iloœci krawêdzi
//		sKosztX = sumFunkcjaKosztu();
		System.out.println("Inicjalna funkcja kosztu: "+ sKosztX);
		
		float temperatura = 70000;
		int count=0;
		float delta=0;
		float alfa = 0.95f;				// funkcja chlodzenia, im 
		int wybor=0;
		int ileIteracji=0;				// pomaga wyznaczyc optymalne wartosci temperatury i alfy.
		int ileZaru=0;
		List<Edge> tempEdgeList;
		Boolean tempCzyRealizowany;
//		Demand tempDemand;
		
		while (sKosztX < 7000f) {					// kryterium stopu
			count=0;
			ileIteracji++;
			
			while(count < 200) {					// kryterium stopu, mozna dodac dodatkowe w while obejmujacym ten. np czasowe albo wynik powyzej jakiegos
				wybor = ktoryDemand();
				dem = demands.get(wybor);
				tempEdgeList = dem.getEdgeList();	// zachowaj stara liste sciezek
				tempCzyRealizowany = dem.getCzyRealizowany();
				//			tempDemand = dem;					// zachowaj demand tymczasowo - zachowuje demand po to zeby zachowac zarowno sciezki jak i jego stan czyRealizowac
				setCzyRealizowac(wybor);

				dem.setEdgeList(graf.znajdzLosowaSciezke(dem.getStartVertex(), dem.getEndVertex(), 10, simpleGraph, edges));		// losowa sciezka, = otoczenie punktu (rozwiazania) nalezace do zbioru rozwiazan

				sKosztY = sumFunkcjaKosztu();
				delta = sKosztY - sKosztX;

				if (delta < 0) {										// jesli nowe gorsze, maksymalizuje f kosztu, przywroc stara trase;
					dem.setEdgeList(tempEdgeList);						// i nie zmieniaj KosztX
					dem.setCzyRealizowany(tempCzyRealizowany);
					System.out.println("KosztY="+sKosztY+" KosztX="+sKosztX);
					//				dem = tempDemand;
				}	
				else if (getFloatRandom() <= Math.exp(-(delta/temperatura))) {	// metropolis test - chuk wie cio, Pjura³ke tak ka¿e, jesli on true, to tez przywroc stara sciezke
					dem.setEdgeList(tempEdgeList);									// jesli wypadnie float > exp, to przyjmij gorsze, skoro <= to przywroc stare lepsze
					dem.setCzyRealizowany(tempCzyRealizowany);
					System.out.println("¯ar!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					ileZaru++;
					//				dem = tempDemand;
				}
				else {
					sKosztX = sKosztY;												// jeœli delta > 0, czyli nowe jest lepsze to zachowaj nowa trase (nie przywracaj starej) i aktualizuj funkcje kosztu
					System.out.println("Mam lepsza opcje, KosztX="+sKosztX+", KosztY="+sKosztY);
				}

				temperatura *= alfa;
				count++;
			}
		}

		sKosztX = sumFunkcjaKosztu();			// przeliczanie kontrolne, sprawdza czy sie parametry zachowuja
		System.out.println();
		System.out.println("Demandy: ");
		
		for (Demand d: demands)
		{
			System.out.println("Realizowany: "+d.getCzyRealizowany());
//			for (Edge e: d.getEdgeList()) {
//				System.out.println("KrawêdŸ: "+e.getStartVertex()+" "+e.getEndVertex());
//			}
		}
		System.out.println();
		System.out.println("Ostateczna funkcja kosztu: "+ sKosztX+" znaleziona w "+ileIteracji+" iteracji, Zar pracowal: "+ileZaru+" razy.");

	}

	private int ktoryDemand() {			
		float bound = demands.size()-1;								// granica zeby nie przekraczac indeksu
		int wybor = getIntRandom(bound);							// wybieram demand
		
		System.out.println("Wybieram Dem: "+wybor);
		
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
			if (d.getCzyRealizowany()) {						// jesli ma nie byc realizowany to po prostu nie bedzie do sumowany do funkcji kosztu, nie zostana uzyte jego krawedzie itd
				wyjatkoweKrawedzie.addAll(d.getEdgeList());										// powoduje DODANIE wyj¹tkowych krawêdzi
				uzyteKrawedzie = d.getEdgeList();												// tu zostaj¹ WYBRANE krawedzie demandu do przeliczenia kosztu instalacji
				profit += d.getDemandProfit();													// sumuje profity

				for (Edge e: uzyteKrawedzie) {
					kosztUzycia += e.getUnitCost() * d.getDemandVal();							// sumuje koszty uzycia
				}
			}
		}
		
		for (Edge e: wyjatkoweKrawedzie) {													// koszty instalacji
//			System.out.println("KrawêdŸ: "+e.getStartVertex()+" "+e.getEndVertex());
			kosztInstalacji += e.getInstallationCost();
		}
		
		koszt = kosztInstalacji + kosztUzycia;
		wynik = profit - koszt;
		
		System.out.println("Funkcja kosztu: "+wynik);
		return wynik;
	}

	public void rozwiazanieInicjalne() {
		System.out.println("Inicjalne: ");
		
		try {
			for (Demand d: demands)
			{
				d.setEdgeList(graf.znajdzNajkrotszaSciezke(d.getStartVertex(), d.getEndVertex(), simpleGraph, edges));
				
				for (Edge x: d.getEdgeList()) {
					System.out.println("Demand: "+d.getStartVertex()+" "+d.getEndVertex()+". Trasa: "+x.getStartVertex()+" "+x.getEndVertex());
				}
			}
		} catch (NullPointerException e) {
			System.out.println(
					"Nie mo¿na wygenerowaæ ¿adnej œcie¿ki dla jednego z zapotrzebowañ");
		}
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

System.out.println("Otrzymana scie¿ka: "+ sciezka);
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