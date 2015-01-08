
public class Demand {

	private String startVertex;
	private String endVertex;
	private int demandVal;
	private int demandProfit;
	
	
	
	
	public Demand(String startVertex, String endVertex, int demandVal,
			int demandProfit) {
		super();
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.demandVal = demandVal;
		this.demandProfit = demandProfit;
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
	
	
	
	
	
}
