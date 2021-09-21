package de.umr.raft.raftlogreplicationdemo.persistence.replication.runners;

import de.umr.raft.raftlogreplicationdemo.config.RaftConfig;
import de.umr.raft.raftlogreplicationdemo.persistence.replication.impl.ClusterManagementMultiRaftServer;
import de.umr.raft.raftlogreplicationdemo.persistence.replication.impl.ClusterManagementServer;
import de.umr.raft.raftlogreplicationdemo.persistence.replication.impl.ClusterMetadataReplicationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class ClusterManagementMultiRaftServerStartupRunner extends MultiRaftReplicationServerStartupRunner<ClusterManagementMultiRaftServer> {

    @Autowired
    public ClusterManagementMultiRaftServerStartupRunner(RaftConfig raftConfig, ClusterMetadataReplicationClient metaDataClient, ClusterManagementMultiRaftServer clusterManagementMultiRaftServer) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // super(raftConfig, metaDataClient, ClusterManagementMultiRaftServer.class);
        super(raftConfig, metaDataClient, clusterManagementMultiRaftServer);
    }
}
