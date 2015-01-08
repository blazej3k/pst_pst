
public class Demand {

	private String startVertex;
	private String endVertex;
	private int demandVal;
	private int demandProfit;
	
	
	
	public Demand(String startVertex, String endVertex) {
		super();
		this.startVertex = startVertex;
		this.endVertex = endVertex;
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
	
	
	
}
