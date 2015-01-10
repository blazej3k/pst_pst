import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Demand {

	private String startVertex;
	private String endVertex;
	private int demandVal;
	private int demandProfit;
	private Set<String> transitNodes;
	private List<Edge> edgeList;
	private float fKosztu;
	private Boolean czyRealizowany;
	
	public Demand(String startVertex, String endVertex, int demandVal,
			int demandProfit) {
		super();
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.demandVal = demandVal;
		this.demandProfit = demandProfit;
		
		System.out.println("Tworze demand");
		setCzyRealizowany(true);
	}
	
	public int getTransitIlosc() {
		if(transitNodes != null)
			return transitNodes.size();
		else
			return -1;
	}
	
	public float getfKosztu() {
		return fKosztu;
	}

	public void setfKosztu(float fKosztu) {
		this.fKosztu = fKosztu;
	}
	public List<Edge> getEdgeList() {
		return edgeList;
	}
	public void setEdgeList(List<Edge> edgeList) {		// automatycznie wybiera unikalna liste u¿ytych wêz³ów tranzytowych i ustawia
		this.edgeList = edgeList;
		transitNodes = new TreeSet<String>();
		
		String w1="", w2="";
		for (Edge e: edgeList) {
			w1 = e.getStartVertex();
			w2 = e.getEndVertex();
			
			if (w1.startsWith("T")) 
				transitNodes.add(w1);
			if (w2.startsWith("T"))
				transitNodes.add(w2);
		}
	}
	
	public Set<String> getTransitNodes() {
		return transitNodes;
	}
	public String getStartVertex() {
		return startVertex;
	}
	public void setStartVertex(String startVertex) {
		this.startVertex = startVertex;
	}
	public String getEndVertex() {
		return endVertex;
	}
	public void setEndVertex(String endVertex) {
		this.endVertex = endVertex;
	}
	public int getDemandVal() {
		return demandVal;
	}
	public void setDemandVal(int demandVal) {
		this.demandVal = demandVal;
	}
	public int getDemandProfit() {
		return demandProfit;
	}
	public void setDemandProfit(int demandProfit) {
		this.demandProfit = demandProfit;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endVertex == null) ? 0 : endVertex.hashCode());
		result = prime * result
				+ ((startVertex == null) ? 0 : startVertex.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Demand other = (Demand) obj;
		if (endVertex == null) {
			if (other.endVertex != null)
				return false;
		} else if (!endVertex.equals(other.endVertex))
			return false;
		if (startVertex == null) {
			if (other.startVertex != null)
				return false;
		} else if (!startVertex.equals(other.startVertex))
			return false;
		return true;
	}

	public Boolean getCzyRealizowany() {
		return czyRealizowany;
	}

	public void setCzyRealizowany(Boolean czyRealizowany) {
		this.czyRealizowany = czyRealizowany;
	}
	
	
	
	
	
}
