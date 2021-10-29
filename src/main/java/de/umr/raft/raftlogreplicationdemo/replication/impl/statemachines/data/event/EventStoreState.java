package de.umr.raft.raftlogreplicationdemo.replication.impl.statemachines.data.event;

import de.umr.chronicledb.event.store.EventStore;
import de.umr.event.schema.Attribute;
import de.umr.event.schema.EventSchema;
import de.umr.raft.raftlogreplicationdemo.replication.api.statemachines.data.StateMachineState;
import de.umr.raft.raftlogreplicationdemo.replication.impl.MultiRaftReplicationServer;
import lombok.Getter;
import org.apache.ratis.util.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

// TODO some interface?
public class EventStoreState implements StateMachineState {

    protected static final Logger LOG =
            LoggerFactory.getLogger(EventStoreState.class);

    @Getter private StreamIndex eventStore;
//    @Getter private Path basePath;
//    @Getter private String streamName;
    @Getter private StreamIdentifier streamIdentifier;

    @Getter private StateMachineState.Phase phase;

    private final static String EVENT_STORE_DIR_NAME = "event-store";

    @Override
    public void clear() {
        eventStore = null;
    }

    @Override
    public void loadFrom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        streamIdentifier = JavaUtils.cast(in.readObject());
        //Path streamPath = streamIdentifier.getBasePath().resolve(streamIdentifier.getStreamName());
        Path streamPath = streamIdentifier.getBasePath().resolve(EVENT_STORE_DIR_NAME);
        eventStore = StreamIndex.loadStreamIndex(streamPath, streamIdentifier.getStreamName());
    }

    @Override
    public Object createSnapshot() {
        // Only return reference to eventStore (name and path), not full store
        // as we don't want to store it twice
        // TODO this does not add value currently as name and path can be retrieved from state machine
        // TODO may add schema to snapshot in case of future possible schema evolution?
        return streamIdentifier;
    }

    // TODO remove after test
    private EventSchema createSimpleSchema() {
        return new DemoEventSchemaProvider().getSchema("DEMO");
    }

    /**
     * Creates a new stream index event store in the raft group directory.
     * if not already existing
     * @param schema The schema of the event store
     * @return The created stream index event store
     * @throws IOException in case the event store already exist
     */
    private StreamIndex createStreamIndex(EventSchema schema) throws IOException {
        final String normalizedName = streamIdentifier.getStreamName().toUpperCase();
        // TODO also rename streamName to "event-store" as the naming is handled by the raft group.
        // this will ensure stream dirs and files follow the same pattern in every group dir
        final Path path = streamIdentifier.getBasePath().resolve(EVENT_STORE_DIR_NAME);
        // final Path path = streamIdentifier.getBasePath().resolve(normalizedName);
        return StreamIndex.createStreamIndex(path, normalizedName, schema);
    }

    private EventStoreState() {
        this.phase = Phase.UNINITIALIZED;
    }

    public static EventStoreState createUninitializedState() {
        return new EventStoreState();
    }

    private void initStreamWithSchema() throws IOException {
        // TODO Fixed schema for evaluation purposes.
        // TODO Serialize schemas in protobuf
        // TODO Must be somehow been passed on group creation or must be the first command to the state machine
        // TODO if not initialized with schema, do not accept any read or write operations
        EventSchema schema = createSimpleSchema();
        try {
            this.eventStore = createStreamIndex(schema);
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                // store already exists, everything ok
                LOG.info("Event store found");
            } else {
                throw e;
            }
        }
        this.phase = Phase.INITIALIZED;
    }

    public void initState(Path basePath, String streamName) throws IOException {
        final String normalizedName = streamName.toUpperCase();
        this.streamIdentifier = StreamIdentifier.of(basePath, normalizedName);
        // TODO will this overrides existing streams? Test it!
        // TODO do not call on initState, do call on a certain obligatory Message
        initStreamWithSchema();
    }

    @Override
    public void initState(Object... args) throws IOException {
        if (args.length != 2 || !(args[0] instanceof Path) || !(args[1] instanceof String)) {
            throw new IllegalArgumentException();
        }
        initState((Path) args[0], (String) args[2]);
    }
}
