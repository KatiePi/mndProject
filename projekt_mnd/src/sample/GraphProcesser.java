import java.util.*;

public class GraphProcesser {

    private boolean isFound;

    public static void calculateShortestPathInDirectedGraph(GraphNode source) {
        source.setDistance(0);

        Set<GraphNode> settledNodes = new HashSet<>();
        Set<GraphNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            GraphNode currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<GraphNode, Integer> adjacencyPair:
                    currentNode.getNeighbours().entrySet()) {
                GraphNode adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
    }

    private static GraphNode getLowestDistanceNode(Set <GraphNode> unsettledNodes) {
        GraphNode lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (GraphNode node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(GraphNode evaluationNode, int edgeWeight, GraphNode sourceNode) {
        int sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<GraphNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    public ArrayList<GraphNode> findCriticalNodes(GraphNode source, ArrayList<GraphNode> allNodes, GraphNode target) {
        ArrayList<GraphNode> criticalNodes = new ArrayList<>();
        try {
            for(GraphNode node: allNodes.subList(1,allNodes.size()-1)) {
                isFound = false;
                findLastNode(source,node,target);
                if(!isFound)criticalNodes.add(node);
            }
            System.out.println("CRITICAL NODES IN GRAPH: ");
            return criticalNodes;
        } catch (StackOverflowError e) {
            return null;
        }

    }

    public void findLastNode(GraphNode source, GraphNode deletedNode, GraphNode target) {
        for(Map.Entry<GraphNode, Integer> nodeIntegerEntry : source.getNeighbours().entrySet()) {
            if(nodeIntegerEntry.getKey() != deletedNode) {
                if(nodeIntegerEntry.getKey() == target) {
                    isFound = true;
                } else {
                    findLastNode(nodeIntegerEntry.getKey(), deletedNode,target);
                }
            }
        }
    }
}
