import java.util.List;


public class Wyzarzanie {

	public int countTransits(List<Edge> sciezka)
	{
		int licznik=0;
		for (Edge edge : sciezka)
		{
			String v = edge.getEndVertex();
			if (v.startsWith("T"))
				licznik++;
		}
		return licznik;
	}
}
