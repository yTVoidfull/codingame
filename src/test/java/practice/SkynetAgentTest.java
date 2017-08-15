package practice;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static practice.SkynetAgent.*;

/**
 * Created by alplesca on 8/15/2017.
 */
public class SkynetAgentTest {

    @Test
    public void theLinkClosestToGatewayWillBeSevered() throws Exception {
        Node gateway = new Node(1,0,true);
        Node agentNode = new Node(0,1, false);
        nodes.add(gateway);
        nodes.add(agentNode);
        Assert.assertThat(decideLinkToSever(0), is("0 1"));
    }

    @Test
    public void theNodeWithTwoGatewaysWillHaveOneSevered() throws Exception {
        nodes = new ArrayList<Node>();
        Node gateway = new Node(1,0,true);
        Node gateway1 = new Node(2, 0, true);
        Node simpleNode = new Node(0,1, false);
        Node agentNode = new Node(3,0,false);

        simpleNode.addLinkedNode(2);
        simpleNode.addLinkedNode(3);

        nodes.add(gateway);
        nodes.add(gateway1);
        nodes.add(simpleNode);
        nodes.add(agentNode);
        Assert.assertThat(decideLinkToSever(3), is("0 1"));
    }

    @Test
    public void ifAgentIsFurtherAwaySeverAnyNode() throws Exception {
        nodes = new ArrayList<Node>();
        Node gateway = new Node(1,0,true);
        Node simpleNode = new Node(2, 0, false);
        Node simpleNode1 = new Node(0,1, false);
        Node agentNode = new Node(3,0,false);

        simpleNode.addLinkedNode(2);
        simpleNode.addLinkedNode(3);

        nodes.add(gateway);
        nodes.add(simpleNode1);
        nodes.add(simpleNode);
        nodes.add(agentNode);
        Assert.assertThat(decideLinkToSever(3), is("0 1"));
    }

    @Test
    public void ifThereAreTwoNodesWithGatewaysTheOneWithMostWillHaveOneSevered() throws Exception {
        nodes = new ArrayList<Node>();
        Node gateway = new Node(4,2,true);
        Node gateway1= new Node(3, 2,true);
        Node simpleNode = new Node(1, 2, false);
        Node simpleNode1 = new Node(2,1, false);
        Node simpleNode2 = new Node(0, 1, false);
        Node agentNode = new Node(5,0,false);

        simpleNode1.addLinkedNode(4);
        simpleNode1.addLinkedNode(3);

        nodes.add(gateway);
        nodes.add(gateway1);
        nodes.add(simpleNode1);
        nodes.add(simpleNode);
        nodes.add(simpleNode2);
        nodes.add(agentNode);
        Assert.assertThat(decideLinkToSever(0), is("4 2"));
    }
}
