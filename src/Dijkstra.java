import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;


public class Dijkstra {

	/**
	 * Klasa do przechowywania wezla grafu
	 * 
	 * @author Adrian Czarniecki
	 */
	public static class Node {
		
		//identyfikator od 0 do n-1 wezla w grafie
		public int id;
		//krawedzie wychodzace z danego wezla przechowywane po id wezla, do ktorego prowadza
		public Map<Integer, Edge> edgesOut = new HashMap<>();
		
		//zmienne potrzebne do algorytmu Dijkstry
		private double d;		//dlugosci najkrotszych sciezek do tego wezla
		public Node v;			//poprzednik tego wezla w drzewie najkrotszych sciezek
		
		public Node(int id) {
			super();
			this.id = id;
		}

		public double getD() {
			return d;
		}

		public void setD(double d) {
			this.d = d;
		}

		//relaksacja sciezki do tego wezla przy pomocy krawedzi edge wychodzacej z wezla peek, a prowadzacej do tego wezla
		public void relaxation(Node peek, Edge edge) {
			//jesli wartosc D jest rowna -nieskonczonosc (wtedy d jest < 0) to znalezlismy pierwsza sciezke do tego wezla
			if(this.getD() < 0) {
				this.setD(peek.getD() + edge.weight);
				this.v = peek;
			} else {
				//jesli D >= 0 i mamy krotsza sciezke przez wezel peek, to aktualizujemy d i v:
				if(peek.getD() + edge.weight < this.getD()) {
					this.setD(peek.getD() + edge.weight);
					this.v = peek;
				}
			}
			
		}
		
	}
	
	/*
	 * Klasa do przechowywania wagi krawedzi i id wezla, do ktorego krawedz prowadzi.
	 */
	public static class Edge {
		
		public int targetNode;
		public double weight;
		
		public Edge(int targetNode, double weight) {
			super();
			this.targetNode = targetNode;
			this.weight = weight;
		}
		
	}
	
	//mapa do przechowywania wszystkich wierzcholkow grafu po ich identyfikatorach
	public static Map<Integer, Node> nodes = new LinkedHashMap<>();

	/*
	 * Metoda zawierajaca wlasciwy algorytm szukania najkroszych sciezek poczawszy od wezla o indeksie start.
	 */
	private static void DijkstraAlgorithm(int start) {
		
		//najpierw inicjujemy wszystkie wartosci v na nulli d na -nieskonczonosc przechowywana jako -1
		for(Node node : nodes.values()) {
			node.v = null;
			node.setD(-1);
			//z wyjatkiem wezla startowego, ktory ma d = 0
			if(node.id == start) {
				node.setD(0);
			}
		}
		
		//tworzymy kolejke priorytetowa z priorytemem bedacym wartoscia d danego wezla
		PriorityQueue<Node> nodeQueue = new PriorityQueue<>(nodes.size(), (x,y) -> {
			if(x.getD() < 0 && y.getD() < 0) return 0;
			if( x.getD() < 0 ) return 1;
			if( y.getD() < 0 ) return -1;
			if( x.getD() < y.getD() ) return -1;
			if( x.getD() > y.getD() ) return 1;
			return 0;
		});
		
		//i wstawiamy do tej kolejki wszystkie wezly
		for(Node node : nodes.values()) {
			nodeQueue.add(node);
		}
		
		//wlasciwy algorytm Dijkstry - dopoki kolejka jest niepusta
		while(nodeQueue.isEmpty() == false) {
			//pobieramy z kolejki element najmniejszy
			Node peek = nodeQueue.poll();
			if(peek.getD() < 0) continue;
			//RELAKSACJA WSZYSTKICH KRAWEDZI WYCHODZACYCH Z TEGO ELEMENTU
			for(Edge edge : peek.edgesOut.values()) {
				nodeQueue.remove(nodes.get(edge.targetNode));
				nodes.get(edge.targetNode).relaxation(peek, edge);
				nodeQueue.add(nodes.get(edge.targetNode));
			}
		}
		
		//wypisanie wartosci dlugosci znalezionych sciezek
		for(Node node : nodes.values()) {
			System.out.format("D[%d]::%.2f ", node.id, node.d);
		}
		System.out.println();
		
		//wypisanie drzewa najkrotszych sciezek
		for(Node node : nodes.values()) {
			System.out.format("V[%d]::%d ", node.id, (node.v != null) ? node.v.id : -1);
		}
		System.out.println();
		
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		
		Scanner scr = new Scanner(new File("graf.txt"));
		scr.useLocale(Locale.US);
		
		//wczytanie ilosci wezlow z pliku
		int n = scr.nextInt();
		
		//stworzenie wezlow odpowiadajacych identyfikatorom
		for(int i = 0; i < n; ++i) {
			nodes.put(i, new Node(i));
		}
		
		//wczytywanie krawedzi grafu
		while(scr.hasNext()) {
			int from = scr.nextInt();
			int to = scr.nextInt();
			double weigth = scr.nextDouble();
			
			//i dodawanie ich do struktury danych
			nodes.get(from).edgesOut.put(to, new Edge(to, weigth));
			
			System.out.format("%d %d %.2f\n", from, to, weigth);
		}
		
		scr.close();
		
		System.out.println("We wczytanym grafie jest " + n + " wierzcholkow.");
		
		System.out.print("Podaj wierzcholek startowy (numerowanie od 0 do " + (n-1) + ") : ");
		Scanner scrIn = new Scanner(System.in);
		int start = scrIn.nextInt();
		scrIn.close();
		
		//WIADOMO
		DijkstraAlgorithm(start);
		
	}

}
