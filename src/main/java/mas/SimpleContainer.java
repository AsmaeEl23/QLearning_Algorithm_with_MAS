package mas;

import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class SimpleContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);
        AgentController mainAgent=null;
        for (int i = 0; i< 4; i++){
            mainAgent = agentContainer.createNewAgent("player"+i, PlayerAgent.class.getName(), new Object[]{});
            mainAgent.start();
        }
        mainAgent = agentContainer.createNewAgent("master",MasterAgent.class.getName(), new Object[]{4});
        mainAgent.start();
    }
}
