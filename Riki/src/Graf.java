import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Graf {

	private Random randomGenerator = new Random();

	private int Ki;
	private int Ti;

	private int numOfClients;
	private int numOfTransits;
	private int numOfEdges;

	public Graf(int numOfClients, int numOfTransits, double percent) {
		this.numOfClients = numOfClients;
		this.numOfTransits = numOfTransits;

		int v = numOfClients + numOfTransits;
		if (percent > 1)
			percent = 1;
		this.numOfEdges = (int) Math.round(percent * v * (v - 1) / 2);

	}

	public Graf() {
		// TODO Auto-generated constructor stub
	}

	public SimpleGraph<String, DefaultEdge> generujGraf() {
		int numOfVertexes = numOfClients + numOfTransits;
		Ki = 0;
		Ti = 0;
		SimpleGraph<String, DefaultEdge> simpleGraph = new SimpleGraph<String, DefaultEdge>(
				DefaultEdge.class);
		RandomGraphGenerator<String, DefaultEdge> randomGraph = new RandomGraphGenerator<>(
				numOfVertexes, numOfEdges);

		VertexFactory<String> vertexFactory = new VertexFactory<String>() {
			@Override
			public String createVertex() {
				if (Ki < numOfClients) {
					Ki++;
					return "K" + Ki;
				} else if (Ti < numOfTransits) {
					Ti++;
					return "T" + Ti;
				}
				return null;
			}
		};

		randomGraph.generateGraph(simpleGraph, vertexFactory, null);
		// System.out.println(simpleGraph.toString());
		return simpleGraph;

	}

	public SimpleGraph<String, DefaultEdge> grafNieskierowany(
			List<String> wierzcholki, List<String> krawedzie) {
		SimpleGraph<String, DefaultEdge> graf = new SimpleGraph<String, DefaultEdge>(
				DefaultEdge.class);

		for (String vertex : wierzcholki)
			graf.addVertex(vertex);

		String[] para;
		for (String edge : krawedzie) {
			edge.trim();
			para = edge.split(" ");
			graf.addEdge(para[0], para[1]);
		}
		// System.out.println(graf);
		return graf;
	}

	public List<String> getSciezki(String startVertex, String endVertex, int k,
			SimpleGraph<String, DefaultEdge> graf) throws NullPointerException {
		KShortestPaths<String, DefaultEdge> kpath = new KShortestPaths<String, DefaultEdge>(
				graf, startVertex, k);

		List<GraphPath<String, DefaultEdge>> sciezkiOdDo = kpath
				.getPaths(endVertex); // generuje sciezki i zwraca liste

		if (sciezkiOdDo.size() == 0)
			throw new NullPointerException();

		List<String> sciezki = new ArrayList<String>();

		System.out.println("K-Shortest Paths:");

		for (GraphPath<String, DefaultEdge> lst : sciezkiOdDo) {
			System.out.println(lst.getEdgeList().toString());

			for (DefaultEdge e : lst.getEdgeList())
				sciezki.add(e.toString()); // troche wygodniejsza lista i tak
											// dupa.

			// sciezki.add(lst.getEdgeList().toString());
		}

		return sciezki;
	}

	//jesli nie moze znalezc sciezki rzuca nullPointerException
	public List<Edge> znajdzNajkrotszaSciezke(int aktualnyDemand,
			SimpleGraph<String, DefaultEdge> graf, List<Edge> edges,
			List<Demand> demands, int maxTransit) throws NullPointerException {
		Demand currentDemand = demands.get(aktualnyDemand);
		String startVertex = currentDemand.getStartVertex();
		String endVertex = currentDemand.getEndVertex();

		TreeSet<String> wezlyTranzytowe = new TreeSet<>();
		DijkstraShortestPath<String, DefaultEdge> path = new DijkstraShortestPath<String, DefaultEdge>(
				graf, startVertex, endVertex);
		GraphPath<String, DefaultEdge> sciezkaOdDo = path.getPath();															// sciezki

		System.out.println("Szukam najkr�tszej.");
		if (sciezkaOdDo == null)
			throw new NullPointerException();

		// zliczenie unikalnych wezlow tranzytowych wykorzystywanych przez inne
		// demandy
		for (Demand d : demands) {
			if (!d.equals(currentDemand)) {
				if (d.getTransitNodes() != null)
					wezlyTranzytowe.addAll(d.getTransitNodes());
			}
		}

		List<Edge> sciezka = new ArrayList<>();
		// System.out.println("Shortest path:");
		for (DefaultEdge e : sciezkaOdDo.getEdgeList()) {
			String x = e.toString();

			x = x.replace("(", "");
			x = x.replace(")", ""); // usuniecie nawiasow
			String[] krawedz = x.split(":");

			String wezelA = krawedz[0].trim();
			String wezelB = krawedz[1].trim();

			// System.out.println(wezelA + "  " + wezelB);
			Edge edge = new Edge(wezelA, wezelB);
			int index = edges.indexOf(edge);
			// System.out.println(index);
			sciezka.add(edges.get(index));

			if (wezelA.startsWith("T"))
				wezlyTranzytowe.add(wezelA);
			if (wezelB.startsWith("T"))
				wezlyTranzytowe.add(wezelB);

		}
//		System.out.println("SIZE: " + wezlyTranzytowe.size());
		for (String transit : wezlyTranzytowe)
//			System.out.println(transit);

//		System.out.println("MAX: " + maxTransit);

		if (wezlyTranzytowe.size() > maxTransit)
			return null;

		return sciezka;

	}

	// jesli nie moze znalezc sciezki zwraca null
	public List<Edge> znajdzLosowaSciezke(int aktualnyDemand, int maxTransit,
			SimpleGraph<String, DefaultEdge> graf, List<Edge> edges,
			List<Demand> demands) {

		Demand currentDemand = demands.get(aktualnyDemand);
		String startVertex = currentDemand.getStartVertex();
		String endVertex = currentDemand.getEndVertex();

		List<String> visitedNodes = new ArrayList<>();
		Stack<String> stack = new Stack<>();
		TreeSet<String> wezlyTranzytowe = new TreeSet<>();

		System.out.println("Szukam losowej.");
		// zliczenie unikalnych wezlow tranzytowych wykorzystywanych przez inne
		// demandy
		for (Demand d : demands) {
			
			if (!d.equals(currentDemand) && !d.getCzyRealizowany()) {
				
				if (d.getTransitNodes() != null)
				{
					System.out.println("demand " + d.getStartVertex() + "   " + d.getEndVertex());
					wezlyTranzytowe.addAll(d.getTransitNodes());
				}
			}
		}
		System.out.println("aktualny " + aktualnyDemand);
		System.out.println("wezly tranzytowe pozostalych dem");
		for (String s: wezlyTranzytowe)
			System.out.println(s);
		visitedNodes.add(startVertex);
		stack.push(startVertex);

		// System.out.println("\n" + startVertex + "   " + endVertex);
		String currentNode = startVertex;

		Iterator<String> iter = stack.iterator();
		TreeSet<String> wezlyTranzytoweNowe = wezlyTranzytowe;
		while (visitedNodes.size() <= graf.vertexSet().size()
				&& !currentNode.equals(endVertex)) {

			 System.out.println("current node " + currentNode);
			List<String> notVisitedNeighbors = znajdzNieodwiedzonychSasiadow(
					graf, currentNode, visitedNodes, wezlyTranzytoweNowe,
					maxTransit);
			if (notVisitedNeighbors.size() > 0) {
				String neighborNode = wybierzLosowegoSasiada(notVisitedNeighbors);
				System.out.println("neighbor node " + neighborNode);
				visitedNodes.add(neighborNode);
				stack.push(neighborNode);
				currentNode = neighborNode;
				if (neighborNode.startsWith("T"))
					wezlyTranzytoweNowe.add(neighborNode);

			} else {
				System.out.println("nie ma sasiadow");
				stack.pop();
				if (currentNode.startsWith("T")
						&& !wezlyTranzytowe.contains(currentNode))
					wezlyTranzytoweNowe.remove(currentNode);
				if (stack.size() == 0)
				{
					System.out.println("Nie mozna znalezc sciezki");
					return null;
				}
				currentNode = stack.peek();
			}

			iter = stack.iterator();

		}

		if (!currentNode.equals(endVertex)) {
			System.out.println("Nie mozna znalezc sciezki");
			return null;
		}

		iter = stack.iterator();

		List<Edge> sciezka = new ArrayList<>();
		String poprzedni = null;
		String obecny;
		// System.out.println("SCIEZKA:");
		while (iter.hasNext()) {
			obecny = iter.next();
			if (poprzedni != null) {
				Edge e = new Edge(poprzedni, obecny);
				int index = edges.indexOf(e);
				sciezka.add(edges.get(index));
				// System.out.println(e.getStartVertex() + "  " +
				// e.getEndVertex() + "  " +index);
			}
			poprzedni = obecny;
		}

		return sciezka;
	}

	public String wybierzLosowegoSasiada(List<String> sasiedzi) {
		int index = randomGenerator.nextInt(sasiedzi.size());
		return sasiedzi.get(index);
	}

	public List<String> znajdzNieodwiedzonychSasiadow(
			SimpleGraph<String, DefaultEdge> graf, String vertex,
			List<String> visitedNodes, TreeSet<String> usedTransits,
			int maxTransits) {
		List<String> sasiedzi = new ArrayList<>();
		sasiedzi = Graphs.neighborListOf(graf, vertex);
		sasiedzi.removeAll(visitedNodes);

		// jesli liczba wykorzystanych wezlow tranzytowych = dopuszczalnej , to
		// nie mozna przejsc do NOWEGO tranzytowego
		if (usedTransits.size() == maxTransits) {
			for (int i = 0; i < sasiedzi.size(); i++) {
				String sasiad = sasiedzi.get(i);
				if (sasiad.startsWith("T") && !usedTransits.contains(sasiad))
				{
					System.out.println("R " + sasiad);
					sasiedzi.remove(i);
					
				}
			}
		}

		
		 System.out.println("nieodwiedzeni sasiedzi"); for (String s :
		 sasiedzi) System.out.println(s);
		 
		return sasiedzi;
	}

}
