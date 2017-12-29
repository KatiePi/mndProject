import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import java.awt.*;
import java.util.*;

public class ApplicationController {

    //TODO validate if weight is numeric
    @FXML private TextArea graphInput;
    @FXML private SwingNode graphNode;
    @FXML private TextArea theShortestPathOutput;
    @FXML private  TextArea criticalNodesOutput;

    private Graph graph;
    private GraphProcesser gp;
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    ArrayList<GraphNode> nodeList = new ArrayList<GraphNode>();

    @FXML
    protected void add() {
        this.graph = new SingleGraph("Network");
        this.graph.setStrict(false);
        this.graph.setAutoCreate(true);
        this.graph.addAttribute("ui.stylesheet",
            "node { " +
                        "text-alignment: at-right; " +
                        "text-padding: 3px, 2px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #A7CC; " +
                        "text-color: white; " +
                        "text-style: bold-italic; " +
                        "text-offset: 5px, 0px;" +
                        "fill-color: red; " +
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
        createGraphFromSingleRows(rows);

        graphNode.setContent(null);
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
        viewer.enableAutoLayout();
        View view = viewer.addDefaultView(false);
        view.setMinimumSize(new Dimension(249, 200));
        graphNode.setContent(view);

        //here create object which caltulate the shortest/longest paths etc - as an argument get graph
    }

    public String[] splitInputIntoRows() {
        String graphInputString = graphInput.getText();
        String[] rows = graphInputString.split("\n");
        return rows;
    }

    public void createGraphFromSingleRows(String[] rows) {
        String shortestPath = "";
        String longestPath = "";
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
        GraphProcesser.calculateShortestPathInDirectedGraph(nodeList.get(0));
        criticalNodes = gp.findCriticalNodes(nodeList.get(0),nodeList,GraphProcesser.lastNode(nodeList));
        for(GraphNode n : GraphProcesser.lastNode(nodeList).getShortestPath()) {
            shortestPath += n.getName() + " ";
        }
        shortestPath += GraphProcesser.lastNode(nodeList).getName();
        for(GraphNode n : criticalNodes) {
            if(n != GraphProcesser.lastNode(nodeList) && n.getNeighbours().size() == 1)criticalNodesText+=n.getName()
                    + " " + n.getNeighbours().entrySet().iterator().next().getValue() + " "
                    + n.getNeighbours().entrySet().iterator().next().getKey().getName() + ", ";
        }
        if(criticalNodesText == "") criticalNodesText = "No critical nodes";
        theShortestPathOutput.setText(shortestPath);
        criticalNodesOutput.setText(criticalNodesText);

    }



}
