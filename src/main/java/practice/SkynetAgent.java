package practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by alplesca on 8/15/2017.
 */
public class SkynetAgent {

    public static List<Node> nodes = new ArrayList<Node>();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        for (int i = 0; i < N; i++) {
            nodes.add(new Node(i));
        }
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            findNodeById(N1).addLinkedNode(N2);
            findNodeById(N2).addLinkedNode(N1);
        }
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            findNodeById(EI).setGateway(true);
        }

        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // Example: 3 4 are the indices of the nodes you wish to sever the link between
            System.out.println(decideLinkToSever(SI));
        }
    }

    public static String decideLinkToSever(int agentNode) {

        // severing the closest links
        System.err.println("agent on node " + agentNode);
        List<Node> gatewaysLinkedToAgentNode = getGatewaysConnectedTo(agentNode);
        System.err.println("gateways connected to current node " + gatewaysLinkedToAgentNode.size());
        if (gatewaysLinkedToAgentNode.size() == 1) {
            findNodeById(agentNode).removeLinkedNode(gatewaysLinkedToAgentNode.get(0).getId());
            findNodeById(gatewaysLinkedToAgentNode.get(0).getId()).removeLinkedNode(agentNode);
            return agentNode + " " + gatewaysLinkedToAgentNode.get(0).getId();
        }

        // severing future jumps after an aggressive search
        String severeDoubleAfterAggressiveSearch = severeFirstDoublelinkAfterAggresiveSearch(agentNode);
        if(severeDoubleAfterAggressiveSearch != null){
            return severeDoubleAfterAggressiveSearch;
        }

        return severeFirstLinkFound(agentNode);
    }

    public static List<Node> getGatewaysConnectedTo(int nodeId) {
        List<Node> output = new ArrayList<Node>();

        for (int nodeNextToAgent : findNodeById(nodeId).getLinkedNodes()) {
            Node n = findNodeById(nodeNextToAgent);
            if (n.isGateway()) {
                output.add(n);
            }
        }

        return output;
    }

    public static List<Node> getNodesConnectedTo(int nodeId) {
        List<Node> output = new ArrayList<Node>();

        for (int nodeNextToAgent : findNodeById(nodeId).getLinkedNodes()) {
            output.add(findNodeById(nodeNextToAgent));
        }

        return output;
    }

    public static String severeFirstLinkFound(int nodeId) {

        // severing a double node
        for (Node n : nodes) {
            List<Node> gatewaysNear = getGatewaysConnectedTo(n.getId());
            if(gatewaysNear.size() == 2){
                int firstNode = gatewaysNear.get(0).getId();
                n.removeLinkedNode(firstNode);
                findNodeById(firstNode).removeLinkedNode(n.getId());
                return firstNode + " " + n.getId();
            }
        }

        // severing first ever link found
        for (Node n : nodes) {
            if (n.isGateway()) {
                if(n.getLinkedNodes().size() > 0){
                    int firstNode = n.getLinkedNodes().get(0);
                    n.removeLinkedNode(firstNode);
                    findNodeById(firstNode).removeLinkedNode(n.getId());
                    return firstNode + " " + n.getId();
                }
            }
        }
        return null;
    }

    public static String severeFirstDoublelinkAfterAggresiveSearch(int nodeId){
        System.err.println("looking to severe a double link");
        List<Node> nodesLinkedToAgentNode = getNodesConnectedTo(nodeId);

        for (Node n : nodesLinkedToAgentNode) {
            List<Node> gatewaysLinked = getGatewaysConnectedTo(n.getId());
            if (gatewaysLinked.size() == 1) {
                severeFirstDoublelinkAfterAggresiveSearch(n.getId());
            }else if(gatewaysLinked.size() == 2){
                n.removeLinkedNode(gatewaysLinked.get(0).getId());
                findNodeById(gatewaysLinked.get(0).getId()).removeLinkedNode(n.getId());
                return n.getId() + " " + gatewaysLinked.get(0).getId();
            }
        }
        return null;
    }

    public static Node findNodeById(int id) {
        for (Node n : nodes) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }

    public static class Node {
        private int id;
        private List<Integer> linkedNodes = new ArrayList<Integer>();
        private boolean gateway;

        public Node(int id) {
            this.id = id;
        }

        public Node(int id, int otherNode, boolean gateway) {
            this.id = id;
            linkedNodes.add(otherNode);
            this.gateway = gateway;
        }

        public void addLinkedNode(int otherNode) {
            linkedNodes.add(otherNode);
        }

        public int getId() {
            return id;
        }

        public List<Integer> getLinkedNodes() {
            return linkedNodes;
        }

        public boolean isGateway() {
            return gateway;
        }

        public void setGateway(boolean gateway) {
            this.gateway = gateway;
        }

        public void removeLinkedNode(int otherNode) {
            linkedNodes.remove(linkedNodes.indexOf(otherNode));
        }
    }

}
