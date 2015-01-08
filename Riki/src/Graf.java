import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
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
			
			for (DefaultEdge e: lst.getEdgeList()) 
				sciezki.add(e.toString());			// troche wygodniejsza lista i tak dupa.
			
//			sciezki.add(lst.getEdgeList().toString());
		}

		return sciezki;
	}

	public List<String> znajdzLosowaSciezke(String startVertex, String endVertex, int t, SimpleGraph<String, DefaultEdge> graf) {

		List<String> visitedNodes = new ArrayList<>();
		Stack<String> stack = new Stack<>();

		visitedNodes.add(startVertex);
		stack.push(startVertex);

		
//		System.out.println("\n" + startVertex + "   " + endVertex);
		String currentNode = startVertex;

		Iterator<String> iter = stack.iterator();
/*		while (iter.hasNext())
			System.out.println("stos: " + iter.next());*/

		while (visitedNodes.size() <= graf.vertexSet().size()
				&& !currentNode.equals(endVertex)) {

//			System.out.println("current node " + currentNode);
			List<String> notVisitedNeighbors = znajdzNieodwiedzonychSasiadow(
					graf, currentNode, visitedNodes);
			if (notVisitedNeighbors.size() > 0) {
				String neighborNode = wybierzLosowegoSasiada(notVisitedNeighbors);
//				System.out.println("neighbor node " + neighborNode);
				visitedNodes.add(neighborNode);
				stack.push(neighborNode);
				currentNode = neighborNode;

			} else {
//				System.out.println("nie ma sasiadow");
				stack.pop();
				currentNode = stack.peek();
			}

			iter = stack.iterator();
/*			while (iter.hasNext())
				System.out.println("stos: " + iter.next());*/
		}
		
//		System.out.println("Skonczylem \n");

		if (!currentNode.equals(endVertex))
		{
			System.out.println("Nie mozna znalezc sciezki");
			return null;
			
		}
		
		iter = stack.iterator();
		
		List<String> sciezka = new LinkedList<String>();
		while (iter.hasNext())
			sciezka.add(iter.next());
		
//		System.out.println("sciezka: " + sciezka);
		return sciezka;	
	}

	public String wybierzLosowegoSasiada(List<String> sasiedzi) {
		int index = randomGenerator.nextInt(sasiedzi.size());
		return sasiedzi.get(index);
	}

	public List<String> znajdzNieodwiedzonychSasiadow(
			SimpleGraph<String, DefaultEdge> graf, String vertex,
			List<String> visitedNodes) {
		List<String> sasiedzi = new ArrayList<>();
		sasiedzi = Graphs.neighborListOf(graf, vertex);
		sasiedzi.removeAll(visitedNodes);

/*		System.out.println("nieodwiedzeni sasiedzi");
		for (String s : sasiedzi)
			System.out.println(s);*/
		return sasiedzi;
	}

}
