package de.umr.raft.raftlogreplicationdemo.controllers;

import de.umr.chronicledb.common.query.range.Range;
import de.umr.event.Event;
import de.umr.event.schema.EventSchema;
import de.umr.event.schema.SchemaException;
import de.umr.jepc.v2.api.epa.EPA;
import de.umr.raft.raftlogreplicationdemo.models.eventstore.CreateStreamRequest;
import de.umr.raft.raftlogreplicationdemo.models.eventstore.InsertEventRequest;
import de.umr.raft.raftlogreplicationdemo.models.eventstore.QueryRequest;
import de.umr.raft.raftlogreplicationdemo.models.eventstore.QueryResponse;
import de.umr.raft.raftlogreplicationdemo.replication.impl.facades.eventstore.ReplicatedChronicleEngine;
import de.umr.raft.raftlogreplicationdemo.replication.impl.statemachines.data.event.StreamInfo;
import de.umr.raft.raftlogreplicationdemo.replication.impl.statemachines.data.event.query.QueryDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/event-store")
public class ReplicatedEventStoreController {

    @Autowired ReplicatedChronicleEngine chronicleEngine; // TODO better also wrap engine in service

    // TODO use protobuf+grpc, not json+http, for high throughput
    // TODO frontend with simple query input interface (with validation?)

    @GetMapping("/streams")
    public List<String> getStreams() throws IOException, ExecutionException, InterruptedException {
        return chronicleEngine.getStreamNames();
    }

    @GetMapping(value = "/streams/{streamName}/info")
    public StreamInfo getStreamInfo(@PathVariable String streamName) throws IOException {
        return chronicleEngine.getStreamInfo(streamName);
    }

    // TODO aggregate endpoint

    @PostMapping(value = "/streams/{streamName}/events")
    @ResponseStatus(HttpStatus.OK)
    public void insertEvents(@PathVariable String streamName, @RequestBody InsertEventRequest input) throws IOException {
        for (Event e : input.getEvents())
            chronicleEngine.pushEvent(streamName, e);
    }

    @PostMapping(value = "/streams")
    @ResponseStatus(HttpStatus.OK)
    public void createStream(@RequestBody CreateStreamRequest createRequest) throws SchemaException, IOException {
        chronicleEngine.registerStream(createRequest.getStreamName(), createRequest.getSchema());
    }

    // TODO build query stuff
    /*
    @GetMapping(value = "/schema")
    public EventSchema schema(@RequestBody SchemaRequest schemaQuery) throws ParseException {
        log.info("Received schema request:  {}.", schemaQuery);
        EPA translate = chronicleEngine.translateQuery(schemaQuery.getQueryString());
        return chronicleEngine.computeSchema(translate);
    }
    */

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public QueryResponse query(@RequestBody QueryRequest query)
            throws ParseException, InterruptedException, ExecutionException {
        //LOG.info("Received query request: {}", query);

        return chronicleEngine.runQueryRequest(query);
    }

}
