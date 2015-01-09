
public class Edge {

	
	private String startVertex;
	private String endVertex;
	private float unitCost;
	private int installationCost;
	private Boolean czyZainstalowany;
	
	
	
	public Edge(String startVertex, String endVertex) {
		super();
		this.startVertex = startVertex;
		this.endVertex = endVertex;
	}
	public Edge(String startVertex, String endVertex, float unitCost,
			int installationCost) {
		super();
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.unitCost = unitCost;
		this.installationCost = installationCost;
		
		czyZainstalowany=false;				// domyslnie zadna nie jest zainstalowana
	}
	public Boolean getCzyZainstalowany() {
		return czyZainstalowany;
	}
	public void setCzyZainstalowany(Boolean czyZainstalowany) {
		this.czyZainstalowany = czyZainstalowany;
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
	public float getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}
	public int getInstallationCost() {
		return installationCost;
	}
	public void setInstallationCost(int installationCost) {
		this.installationCost = installationCost;
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

	//TODO chyba trzeba poprawic
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (endVertex == null) {
			if (other.endVertex != null)
				return false;
		} else if (!endVertex.equals(other.endVertex) && !endVertex.equals(other.startVertex))
			return false;
		if (startVertex == null) {
			if (other.startVertex != null)
				return false;
		} else if (!startVertex.equals(other.startVertex) && !startVertex.equals(other.endVertex))
			return false;
		return true;
	}
	
	
}