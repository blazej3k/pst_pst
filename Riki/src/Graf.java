import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Graf {

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
			System.out.println(lst.getEdgeList());
			sciezki.add(lst.getEdgeList().toString());
		}

		return sciezki;
	}

}
