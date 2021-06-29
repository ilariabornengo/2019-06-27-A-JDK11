package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	List<String> vertici;
	Graph<String,DefaultWeightedEdge> grafo;
	List<String> listaBest;
	public int pesoMin;
	
	public Model()
	{
		this.dao=new EventsDao();
	}
	
	public void creaGrafo(Integer anno,String categoria)
	{
		this.vertici=new ArrayList<String>();
		this.grafo=new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		this.dao.getVertici(vertici, anno, categoria);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//aggiungo gli archi
		for(Adiacenza a:this.dao.getAdiacenze(vertici, anno, categoria))
		{
			if(this.grafo.vertexSet().contains(a.getS1()) && this.grafo.vertexSet().contains(a.getS2()))
			{
				Graphs.addEdge(this.grafo, a.getS1(), a.getS2(), a.getPeso());
			}
		}
	}
	
	public List<Adiacenza> getArchiL(Integer anno,String categoria)
	{
		return this.dao.getAdiacenze(vertici, anno, categoria);
	}
	
	public List<Adiacenza> massimi(Integer anno,String categoria,int pesoMax)
	{
		List<Adiacenza> ok=new ArrayList<Adiacenza>();
		for(Adiacenza a:this.dao.getAdiacenze(vertici, anno, categoria))
		{
			if(a.getPeso()==pesoMax)
			{
				ok.add(a);
			}
		}
		return ok;
	}
	
	public int getPesoMax()
	{
		int pesoM=0;
		for(DefaultWeightedEdge d:this.grafo.edgeSet())
		{
			int pesoA=(int) this.grafo.getEdgeWeight(d);
			if(pesoA>pesoM)
			{
				pesoM=pesoA;
			}
		}
		return pesoM;
	}
	
	public List<String> getCamminoBest(Adiacenza a)
	{
		String partenza=a.getS1();
		String arrivo=a.getS2();
		this.listaBest=new ArrayList<String>();
		List<String> parziale=new ArrayList<String>();
		this.pesoMin=Integer.MAX_VALUE;
		parziale.add(partenza);
		ricorsione(parziale,arrivo);
		return this.listaBest;
	}
	
	private void ricorsione(List<String> parziale, String arrivo) {
		String ultimo=parziale.get(parziale.size()-1);
		if(parziale.size()==this.grafo.vertexSet().size())
		{
			int peso=calcolaPeso(parziale);
			if(peso<this.pesoMin)
			{
				this.pesoMin=peso;
				this.listaBest=new ArrayList<String>(parziale);
				return;
			}
		}
		//fuori dal caso terminale
		for(String s:Graphs.neighborListOf(this.grafo, ultimo))
		{
			if(!parziale.contains(s))
			{
				parziale.add(s);
				ricorsione(parziale,arrivo);
				parziale.remove(s);
			}
		}
		
		
	}

	private int calcolaPeso(List<String> parziale) {
		int pesoTot=0;
		for(int i=1; i<parziale.size();i++)
		{
			String s1=parziale.get(i-1);
			String s2=parziale.get(i);
			int pesoA=(int) this.grafo.getEdgeWeight(this.grafo.getEdge(s1, s2));
			pesoTot+=pesoA;
		}
		return pesoTot;
	}

	public int getVertici()
	{
		return this.grafo.vertexSet().size();
	}
	public int getArchi()
	{
		return this.grafo.edgeSet().size();
	}
	public List<String> categorie()
	{
		return this.dao.getCategorie();
	}
	public List<Integer> anni()
	{
		return this.dao.getAnni();
	}
}
