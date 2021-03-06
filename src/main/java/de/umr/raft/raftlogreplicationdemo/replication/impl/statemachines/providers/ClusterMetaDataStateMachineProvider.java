package de.umr.raft.raftlogreplicationdemo.replication.impl.statemachines.providers;

import de.umr.raft.raftlogreplicationdemo.replication.impl.statemachines.ClusterMetadataStateMachine;
import org.apache.ratis.protocol.RaftPeer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ClusterMetaDataStateMachineProvider extends StateMachineProvider<ClusterMetadataStateMachine> {

    public ClusterMetaDataStateMachineProvider(RaftGroupConfig raftGroupConfig) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        super(ClusterMetadataStateMachine.class, raftGroupConfig);
    }

    public static ClusterMetaDataStateMachineProvider of(String groupName, List<RaftPeer> peers) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return new ClusterMetaDataStateMachineProvider(RaftGroupConfig.of(peers, groupName));
    }
}
