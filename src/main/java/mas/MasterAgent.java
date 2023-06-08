package mas;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

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
                ACLMessage receiveMessage=blockingReceive();
                if(receiveMessage!=null){
                    if(!players.containsKey(receiveMessage.getSender())){
                        players.put(receiveMessage.getSender(),Integer.parseInt(receiveMessage.getContent()));
                    }
                    if(players.size()==(int)getArguments()[0]){
                        AID winner=null;
                        int bestResult=1000000;
                        for (Map.Entry<AID, Integer> p : players.entrySet()) {
                            int v=p.getValue();
                            if(v<bestResult){
                                bestResult=v;
                                System.out.println("result "+v);
                                winner=p.getKey();
                            }
                        }
                        System.out.println("****** winner "+winner.getLocalName());
                        ACLMessage message=new ACLMessage(ACLMessage.INFORM);

                        for (Map.Entry<AID, Integer> p : players.entrySet()){
                            if(!p.getKey().equals(winner)){
                                message.addReceiver(p.getKey());
                                message.setContent(winner.getLocalName()+" is the winner with "+bestResult+" ms");
                            }
                            send(message);
                        }
                        message=new ACLMessage(ACLMessage.INFORM);
                        message.setContent("congrats you are the winner ");
                        message.addReceiver(winner);
                        send(message);
                        myAgent.removeBehaviour(this);
                        doDelete();
                    }
                }else {
                    block();
                }
            }

        });
        addBehaviour(sequentialBehaviour);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
