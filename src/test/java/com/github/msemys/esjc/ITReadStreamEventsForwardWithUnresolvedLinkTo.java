package com.github.msemys.esjc;

import com.github.msemys.esjc.system.SystemEventType;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.*;

public class ITReadStreamEventsForwardWithUnresolvedLinkTo extends AbstractIntegrationTest {

    @Override
    protected EventStore createEventStore() {
        return eventstoreSupplier.get();
    }

    @Test
    public void readsEventsWithUnresolvedLinkTo() {
        final String deletedStreamName = generateStreamName();
        final String linkedStreamName = generateStreamName();

        eventstore.appendToStream(deletedStreamName, ExpectedVersion.noStream(), newTestEvents()).join();
        eventstore.appendToStream(linkedStreamName, ExpectedVersion.noStream(), asList(newLinkEvent(deletedStreamName, 0))).join();
        eventstore.deleteStream(deletedStreamName, ExpectedVersion.any()).join();

        StreamEventsSlice deletedStreamSlice = eventstore.readStreamEventsForward(deletedStreamName, 0, 100, false).join();
        assertEquals(SliceReadStatus.StreamNotFound, deletedStreamSlice.status);
        assertTrue(deletedStreamSlice.events.isEmpty());

        StreamEventsSlice linkedStreamSlice = eventstore.readStreamEventsForward(linkedStreamName, 0, 1, true).join();
        assertEquals(1, linkedStreamSlice.events.size());
        assertNull(linkedStreamSlice.events.get(0).event);
        assertNotNull(linkedStreamSlice.events.get(0).link);
    }

    private static List<EventData> newTestEvents() {
        return range(0, 20).mapToObj(i -> newTestEvent()).collect(toList());
    }

    private static EventData newLinkEvent(String stream, int eventNumber) {
        return EventData.newBuilder()
            .type(SystemEventType.LINK_TO.value)
            .data(eventNumber + "@" + stream)
            .build();
    }

}
