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
	Graph<String,DefaultWeightedEdge> grafo;
	List<String> vertici;
	List<String> percorsoBest;
	public int pesoBest;
	
	public Model()
	{
		this.dao=new EventsDao();
	}
	
	public void creaGrafo(Integer anno,String categoria)
	{
		this.grafo=new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertici=new ArrayList<String>();
		
		//aggiungo i vertici
		this.vertici=this.dao.getVertici(anno, categoria);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//aggiungo gli archi
		for(Adiacenza a:this.dao.getAdiacenze(anno, categoria, vertici))
		{
			if(this.grafo.vertexSet().contains(a.getId1())&& this.grafo.vertexSet().contains(a.getId2()))
			{
				Graphs.addEdge(this.grafo, a.getId1(), a.getId2(), a.getPeso());
			}
		}
	}
	
	public List<Adiacenza> getElenco(Integer anno,String categoria)
	{
		List<Adiacenza> elencoMax=new ArrayList<Adiacenza>();
		Integer pesoMassimo=getPesoMax(anno,categoria);
		for(Adiacenza a:this.dao.getAdiacenze(anno, categoria, vertici))
		{
			if(this.grafo.vertexSet().contains(a.getId1())&& this.grafo.vertexSet().contains(a.getId2()))
			{
				if(a.peso==pesoMassimo)
				{
					elencoMax.add(a);
				}
			}
		}
		return elencoMax;
	}
	
	public List<String> getPercorsoBest(Adiacenza a)
	{
		String partenza=a.getId1();
		String arrivo=a.getId2();
		this.percorsoBest=new ArrayList<String>();
		List<String> parziale=new ArrayList<String>();
		parziale.add(partenza);
		this.pesoBest=0;
		ricorsione(parziale,arrivo);
		return this.percorsoBest;
	}
	
	private void ricorsione(List<String> parziale, String arrivo) {
		String ultimo=parziale.get(parziale.size()-1);
		//caso terminale
		if(ultimo.equals(arrivo) && parziale.size()==this.grafo.vertexSet().size())
		{
			int pesoParziale=calcolaPeso(parziale);
			if(this.pesoBest==0)
			{
				this.pesoBest=pesoParziale;
				this.percorsoBest=new ArrayList<String>(parziale);
				return;
			}
			else if(pesoParziale<this.pesoBest)
			{
				this.pesoBest=pesoParziale;
				this.percorsoBest=new ArrayList<String>(parziale);
				return;
			}
			else
			{
				return;
			}
		}
		
		//fuori dal caso terminale
		for(String vicini: Graphs.neighborListOf(this.grafo, ultimo))
		{
			if(!parziale.contains(vicini))
			{
				parziale.add(vicini);
				ricorsione(parziale,arrivo);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}

	private int calcolaPeso(List<String> parziale) {
		int pesoTot=0;
		for(int i=1;i<parziale.size();i++)
		{
			String a=parziale.get(i-1);
			String b=parziale.get(i);
			int pesoArco=(int) this.grafo.getEdgeWeight(this.grafo.getEdge(a, b));
			pesoTot+=pesoArco;
		}
		return pesoTot;
	}

	public int getPesoMax(Integer anno,String categoria)
	{
		int pesoMax=0;
		for(DefaultWeightedEdge d:this.grafo.edgeSet())
		{
			if(this.grafo.getEdgeWeight(d)>pesoMax)
			{
				pesoMax=(int) this.grafo.getEdgeWeight(d);
			}
		}
		return pesoMax;
	}
	
	public int getArchi()
	{
		return this.grafo.edgeSet().size();
	}
	public int getVertici()
	{
		return this.grafo.vertexSet().size();
	}
	
	public List<String> getBoxCategorie()
	{
		return this.dao.listBoxCate();
	}
	public List<Integer> getBoxAnno()
	{
		return this.dao.listBoxAnno();
	}
}
