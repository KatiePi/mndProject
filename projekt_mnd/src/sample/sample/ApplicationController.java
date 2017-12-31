package sample;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationController {

    //TODO validate if weight is numeric
    @FXML private TextArea graphInput;
    @FXML private SwingNode graphNode;
    @FXML private TextArea theShortestPathOutput;
    @FXML private TextArea criticalNodesOutput;
    @FXML private Label syntaxErrorLabel;
    @FXML private Label warning;
    @FXML private TextArea necessaryNodes;
    @FXML private Label necessaryNodesWarning;
    @FXML private TextArea shortestPathWithNecessaryActivitiesOutput;

    private Graph graph;
    private GraphProcesser gp;
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    ArrayList<GraphNode> nodeList = new ArrayList<GraphNode>();

    @FXML
    protected void add() {
        necessaryNodesWarning.setVisible(false);
        necessaryNodesWarning.setStyle("-fx-text-fill: red;");
        shortestPathWithNecessaryActivitiesOutput.setText("");
        warning.setVisible(false);
        warning.setStyle("-fx-text-fill: red;");
        this.graph = new SingleGraph("Network");
        this.graph.setStrict(false);
        this.graph.setAutoCreate(true);
        this.graph.addAttribute("ui.stylesheet",
            "node { " +
                        "text-alignment: at-right; " +
                        "text-padding: 3px, 2px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #A7CC; " +
                        "text-color: black; " +
                        "text-style: bold-italic; " +
                        "text-offset: 5px, 5px;" +
                        "fill-color: black; " +
                        "size: 10px, 10px;" +
                    "}" +
                    "edge { " +
                        "fill-color: blue; " +
                        "text-alignment: along;" +
                        "text-style: bold-italic;" +
                        "text-padding: 3px, 2px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: grey; " +
                        "text-color: white; " +
                    "}");
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");

        String[] rows = splitInputIntoRows();
        if(areRowsValidated(rows)) {
            syntaxErrorLabel.setText("");
            graphNode.setVisible(true);
            createGraphFromSingleRows(rows);
            graphNode.setContent(null);
            Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
            viewer.enableAutoLayout();
            View view = viewer.addDefaultView(false);
            view.setMinimumSize(new Dimension(249, 200));
            graphNode.setContent(view);
        }
        else {
            graphNode.setVisible(false);
            syntaxErrorLabel.setText("Wrong syntax!");
        }
    }

    public String[] splitInputIntoRows() {
        String graphInputString = graphInput.getText();
        String[] rows = graphInputString.split("\n");
        return rows;
    }

    public void createGraphFromSingleRows(String[] rows) {
        String shortestPath = "";
        String criticalNodesText = "";
        ArrayList<GraphNode> criticalNodes;
        Map<String, GraphNode> nodesHelper = new TreeMap<String, GraphNode>();
        for ( String row : rows) {
            String[] letters = row.split(" ");
            this.graph.addEdge( row, letters[0], letters[2], true);
            graph.getEdge(row).setAttribute("weight", Integer.parseInt(letters[1]));
            graph.getEdge(row).setAttribute("ui.label", Integer.parseInt(letters[1]));

            nodesHelper.put(letters[0], new GraphNode(letters[0]));
            nodesHelper.put(letters[2], new GraphNode(letters[2]));
        }
        for (String row : rows) {
            String[] letters = row.split(" ");
            nodesHelper.get(letters[0]).addNeighbour(nodesHelper.get(letters[2]),Integer.parseInt(letters[1]));
        }
        for (Node node : graph.getNodeSet()) {
            node.setAttribute("ui.label", node.getId());
        }

        nodeList = new ArrayList<GraphNode>(nodesHelper.values());

        GraphNode source = GraphProcesser.firstNode(nodeList);
        gp = new GraphProcesser();

        if(gp.graphAcyclic(nodeList)) {
            GraphProcesser.calculateShortestPathInDirectedGraph(nodeList.get(0));
            criticalNodes = gp.findCriticalNodes(nodeList.get(0), nodeList, GraphProcesser.lastNode(nodeList));
            for (GraphNode n : GraphProcesser.lastNode(nodeList).getShortestPath()) {
                graph.getNode(n.getName()).setAttribute("ui.style",
                        "fill-color: green; ");
                shortestPath += n.getName() + " ";
            }
            shortestPath += GraphProcesser.lastNode(nodeList).getName();
            graph.getNode(GraphProcesser.lastNode(nodeList).getName()).setAttribute("ui.style",
                    "fill-color: green; ");

            for (GraphNode n : criticalNodes) {
                if (n != GraphProcesser.lastNode(nodeList) && n.getNeighbours().size() == 1) {
                    String nextCriticalNode = n.getName()
                            + " " + n.getNeighbours().entrySet().iterator().next().getValue() + " "
                            + n.getNeighbours().entrySet().iterator().next().getKey().getName();
                    criticalNodesText += nextCriticalNode + ", ";

                    graph.getEdge(nextCriticalNode).setAttribute("ui.style",
                            "text-background-color: red; ");
                }
            }
            if (criticalNodesText == "") criticalNodesText = "No critical activities";
            theShortestPathOutput.setText(shortestPath);


            criticalNodesOutput.setText(criticalNodesText);
        } else {
            warning.setVisible(true);
            criticalNodesOutput.setText("");
            theShortestPathOutput.setText("");
        }

        if(GraphProcesser.validateNecessaryActivitiesTextField(necessaryNodes, nodeList)) {
            ArrayList<GraphNode> nodeTempList = new ArrayList<GraphNode>(nodeList);
            LinkedHashMap<GraphNode, GraphNode> neededNodesWithNeighbours = GraphProcesser.getListOfNecessaryNodes(necessaryNodes, nodeTempList);
            for(Map.Entry<GraphNode,GraphNode> node: neededNodesWithNeighbours.entrySet()) {
                System.out.println("neededNodesWithNeighbours NODE :: " + node.getKey().getName());
            }
            ArrayList<GraphNode> shortestPathWithNecessaryNodes = new ArrayList<>();

            for(Map.Entry<GraphNode, GraphNode> nodeGraphNodeEntry: neededNodesWithNeighbours.entrySet()) {
                nodeTempList = new ArrayList<>(nodeList);
                shortestPathWithNecessaryNodes.addAll(nodeGraphNodeEntry.getKey().getShortestPath());
                shortestPathWithNecessaryNodes.add(nodeGraphNodeEntry.getKey());
                //shortestPathWithNecessaryNodes.add(nodeGraphNodeEntry.getValue());
                for(GraphNode node: nodeList) {
                    node.clearShortestPath();
                    node.setDistance(Integer.MAX_VALUE);
                }
                GraphProcesser.calculateShortestPathInDirectedGraph(nodeGraphNodeEntry.getValue());
                for(GraphNode node: GraphProcesser.lastNode(nodeList).getShortestPath()) {
                    System.out.println("PATH ::::: " + node.getName());
                }
            }

            for(GraphNode node: nodeList) {
                node.clearShortestPath();
                node.setDistance(Integer.MAX_VALUE);
            }
            Map.Entry<GraphNode, GraphNode> lastMapEntry = (Map.Entry<GraphNode, GraphNode>) neededNodesWithNeighbours.entrySet().toArray()[neededNodesWithNeighbours.size() -1];
            GraphProcesser.calculateShortestPathInDirectedGraph(lastMapEntry.getValue());
            shortestPathWithNecessaryNodes.addAll(GraphProcesser.lastNode(nodeList).getShortestPath());
            shortestPathWithNecessaryNodes.add(GraphProcesser.lastNode(nodeList));

            for(GraphNode node: shortestPathWithNecessaryNodes) {
                shortestPathWithNecessaryActivitiesOutput.setText(shortestPathWithNecessaryActivitiesOutput.getText() + " " + node.getName());
            }
        } else {
            if(necessaryNodes.getText().length() > 1) necessaryNodesWarning.setVisible(true);
        }
    }

    public static boolean areRowsValidated(String[] rows) {
        boolean validated = true;
        Pattern letterPattern = Pattern.compile("[a-zA-Z]*");
        Pattern numberPattern = Pattern.compile("[0-9]*");
        Matcher nodeFrom, edgeWeight, nodeTo;

        for ( String row : rows) {
            String[] letters = row.split(" ");
            if(letters.length != 3)
                return validated = false;
            nodeFrom = letterPattern.matcher(letters[0]);
            boolean nodeFromIsValidated = nodeFrom.matches();
            edgeWeight = numberPattern.matcher(letters[1]);
            boolean edgeWeightIsValidated = edgeWeight.matches();
            nodeTo = letterPattern.matcher(letters[2]);
            boolean nodeToIsValidated = nodeTo.matches();
            if(!(nodeFromIsValidated && edgeWeightIsValidated && nodeToIsValidated)) {
                return validated = false;
            }
        }
        return validated;
    }
}
