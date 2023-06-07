package mas;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.sdia.sma.sequential.Qlearning;

import java.util.Arrays;

public class PlayerAgent extends Agent {
    Qlearning qlearning=new Qlearning();
    @Override
    protected void setup() {

        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setName("master");
                DFAgentDescription[] agentDescriptions;
                try {
                    agentDescriptions = DFService.search(getAgent(), dfAgentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(" Agents "+Arrays.toString(agentDescriptions));
                ACLMessage aclMessage=new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(agentDescriptions[0].getName());
                aclMessage.setContent(String.valueOf(qlearning.getExecutionTime()));
                send(aclMessage);
            }
        });
        sequentialBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=blockingReceive();
                System.out.println("Master : "+aclMessage.getContent());
            }
        });
        addBehaviour(sequentialBehaviour);
    }
}
