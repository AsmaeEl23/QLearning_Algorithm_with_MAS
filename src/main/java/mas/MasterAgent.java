package mas;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.sdia.sma.sequential.Qlearning;

import java.util.HashMap;
import java.util.Map;

public class MasterAgent extends Agent {
    HashMap<AID, Integer> players=new HashMap<>();

    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("QLearning");
        serviceDescription.setName("master");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this,dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage receiveMessage=receive();
                if(receiveMessage!=null){
                    if(!players.containsKey(receiveMessage.getSender())){
                        players.put(receiveMessage.getSender(),Integer.parseInt(receiveMessage.getContent()));
                    }
                    if(players.size()==(int)getArguments()[0]){
                        myAgent.removeBehaviour(this);
                        doDelete();
                    }
                }else {
                    block();
                }
            }

        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            boolean finished=false;
            @Override
            public void action() {
                AID winner=null;
                int bestResult=0;
                for (Map.Entry<AID, Integer> p : players.entrySet()) {
                    System.out.println("Player "+p.getKey()+" score "+p.getValue());
                    if(p.getValue()>bestResult){
                        bestResult=p.getValue();
                        winner=p.getKey();
                    }
                }
                ACLMessage message=new ACLMessage(ACLMessage.INFORM);
                message.setContent(winner.getName()+" is the winner with "+bestResult+" iteration");
                for (Map.Entry<AID, Integer> p : players.entrySet()){
                    message.addReceiver(p.getKey());
                    if(p.getKey().equals(winner))
                        message.setContent("congrats you are the winner ");
                    send(message);
                }
                finished=true;
            }

            @Override
            public boolean done() {
                return finished;
            }
        });
        addBehaviour(sequentialBehaviour);
    }
}
