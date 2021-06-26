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
	
	public List<Integer> getAnno()
	{
		return this.dao.getAnni();
	}
	public List<String> getCategorie()
	{
		return this.dao.getCategorie();
	}
	public void creaGrafo(Integer anno,String categoria)
	{
		this.vertici=new ArrayList<String>();
		this.grafo=new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		this.dao.getVertici(anno, categoria, vertici);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//aggiungo gli archi
		for(Adiacenza a:this.dao.getAdiacenze(anno, categoria, vertici))
		{
			if(this.grafo.vertexSet().contains(a.getId1()) && this.grafo.vertexSet().contains(a.getId2()))
			{
				Graphs.addEdge(this.grafo, a.getId1(), a.getId2(), a.getPeso());
			}
		}
	}
	public List<Adiacenza> getMax(Integer anno,String categoria, int pesoMax)
	{
		List<Adiacenza> pesoM=new ArrayList<Adiacenza>();
		for(Adiacenza a:this.dao.getAdiacenze(anno, categoria, vertici))
		{
			if(a.getPeso()==pesoMax)
			{
				pesoM.add(a);
			}
		}
		return pesoM;
	}
	public int getPesoMax()
	{
		int pesoMax=0;
		for(DefaultWeightedEdge d:this.grafo.edgeSet())
		{
			int pesoA=(int) this.grafo.getEdgeWeight(d);
			if(pesoA>pesoMax)
			{
				pesoMax=pesoA;
			}
		}
		return pesoMax;
	}
	public List<Adiacenza> getAdiac(Integer anno,String categoria, int pesoMax)
	{
		return this.dao.getAdiacenze(anno, categoria, vertici);
	}
	
	public List<String> getBest(Adiacenza a)
	{
		String partenza=a.getId1();
		String arrivo=a.getId2();
		this.listaBest=new ArrayList<String>();
		List<String> parziale=new ArrayList<String>();
		this.pesoMin=Integer.MAX_VALUE;
		parziale.add(partenza);
		ricorsione(parziale,arrivo);
		return this.listaBest;
	}
	
	private void ricorsione(List<String> parziale, String arrivo) {
		String ultimo=parziale.get(parziale.size()-1);
		if(ultimo.equals(arrivo) && parziale.size()==this.grafo.vertexSet().size())
		{
			int pesoP=calcolaPeso(parziale);
			if(pesoP<this.pesoMin)
			{
				this.listaBest=new ArrayList<String>(parziale);
				this.pesoMin=pesoP;
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
		int peso=0;
		for(int i=1; i<parziale.size();i++)
		{
			String s1=parziale.get(i-1);
			String s2=parziale.get(i);
			int pesoU=(int) this.grafo.getEdgeWeight(this.grafo.getEdge(s1, s2));
			peso+=pesoU;
		}
		return peso;
	}

	public int getVertci()
	{
		return this.grafo.vertexSet().size();
	}
	public int getArchi()
	{
		return this.grafo.edgeSet().size();
	}
	
}
