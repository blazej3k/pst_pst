
public class Edge {

	
	private String startVertex;
	private String endVertex;
	private int unitCost;
	private int installationCost;
	
	
	public Edge(String startVertex, String endVertex) {
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
	public int getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(int unitCost) {
		this.unitCost = unitCost;
	}
	public int getInstallationCost() {
		return installationCost;
	}
	public void setInstallationCost(int installationCost) {
		this.installationCost = installationCost;
	}
	
	
}
