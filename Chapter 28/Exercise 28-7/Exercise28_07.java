/*
 * Author: Joshua Gray
 * Date: 10/27/21
 * 
 * This program adds a new method, getACycle(), to UnweightedGraph.java. The method 
 * returns a List that contains all the vertices in a cycle starting from u. 
 * If the graph doesn't have any cycle, the method returns null.
 */
public class Exercise28_07 {
	public static void main(String[] args) {
		String[] vertices = { "Seattle", "San Francisco", "Los Angeles",
				"Denver", "Kansas City", "Chicago", "Boston", "New York",
				"Atlanta", "Miami", "Dallas", "Houston" };

		int[][] edges = { { 0, 1 }, { 1, 0 }, { 1, 2 }, { 1, 3 }, { 2, 1 },
				{ 2, 3 }, { 2, 4 }, { 2, 10 }, { 3, 1 }, { 3, 2 }, { 3, 4 },
				{ 3, 5 }, { 4, 2 }, { 4, 3 }, { 4, 5 }, { 4, 7 }, { 4, 8 },
				{ 4, 10 }, { 5, 3 }, { 5, 4 }, { 5, 6 }, { 5, 7 }, { 6, 5 },
				{ 6, 7 }, { 7, 4 }, { 7, 5 }, { 7, 6 }, { 7, 8 }, { 8, 4 },
				{ 8, 7 }, { 8, 9 }, { 8, 10 }, { 8, 11 }, { 9, 8 }, { 9, 11 },
				{ 10, 2 }, { 10, 4 }, { 10, 8 }, { 10, 11 }, { 11, 8 },
				{ 11, 9 }, { 11, 10 } };

		UnweightedGraph<String> graph = new UnweightedGraph<>(
				vertices, edges);

		System.out.println("Find a cycle " + graph.getACycle());

		Integer[] vertices1 = { 0, 1, 2, 3 };

		int[][] edges1 = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 0 }, { 1, 3 },
				{ 2, 0 }, { 3, 0 } };

		UnweightedGraph<Integer> graph1 = new UnweightedGraph<>(
				vertices1, edges1);

		System.out.println("Find a cycle " + graph1.getACycle());
	}
}
