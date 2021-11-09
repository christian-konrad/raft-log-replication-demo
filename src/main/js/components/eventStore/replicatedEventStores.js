import React, { Component } from 'react';
import ApiClient from "../../api/apiClient";
import Skeleton from '@material-ui/lab/Skeleton';
import {Button, Card, CardContent, List, ListItem, ListItemText, Typography} from "@material-ui/core";
import {withStyles} from '@material-ui/core/styles';
import ErrorBoundary from "../error/errorBoundary";
import { Add20 } from '@carbon/icons-react';
import EventStore from './eventStore';

const useStyles = theme => ({
    addCounterButton: {
        marginTop: 24,
        background: 'white',
        marginLeft: 'auto'
    },
    addButtonContainer: {
        display: 'flex'
    }
});

class ReplicatedEventStores extends Component {
    counterId;

    constructor(props) {
        super(props);
        this.state = { eventStreamNames: [] };
        this.fetchEventStores = this.fetchEventStores.bind(this);
        // this.createEventStore = this.createEventStore.bind(this);
    }

    fetchEventStores() {
        ApiClient.fetchEventStreamNames()
            .then(eventStreamNames => this.setState({ eventStreamNames }));
    }

    // createEventStore(streamName) {
    //     ApiClient.createEventStore(streamName)
    //         .then(() => this.fetchEventStores());
    // }

    componentDidMount() {
        this.fetchEventStores();
        this.interval = setInterval(() => {
            this.fetchEventStores();
        }, 2000); // TODO make configurable
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    render() {
        if (this.state.eventStreamNames == null) return <Skeleton />

        const { classes } = this.props;
        const { eventStreamNames } = this.state;

        return (
            <>
                {eventStreamNames.map(eventStreamName => <EventStore key={eventStreamName} eventStreamName={eventStreamName} />)}
                {/* TODO must add name and schema using a certain dialog */}
                {/*<div className={classes.addButtonContainer}>*/}
                {/*    <Button variant="outlined" startIcon={<Add20 />} onClick={() => this.createEventStore(`stream-${counterIds.length + 1}`)} className={classes.addCounterButton}>Add new stream</Button>*/}
                {/*</div>*/}
            </>
        )
    }
}
export default withStyles(useStyles)(ReplicatedEventStores)
