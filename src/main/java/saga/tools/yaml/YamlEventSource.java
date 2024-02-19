package saga.tools.yaml;

import java.util.Iterator;
import java.util.Optional;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.ScalarEvent;

public class YamlEventSource {

    private final Iterator<Event> events;
    private Event lastEvent = null;
    public boolean separatorFound;

    public YamlEventSource(Iterator<Event> events) {
        this.events = events;
    }

    public boolean hasNext() {
        return events.hasNext();
    }

    public Event readNext() {
        Event beforeLastEvent = lastEvent;
        lastEvent = events.next();
        //System.out.println("--- " + lastEvent.getStartMark().getColumn() + " " + lastEvent);
        if ((beforeLastEvent instanceof MappingEndEvent) && (lastEvent instanceof MappingEndEvent)) {
            separatorFound = true;
        }
        return lastEvent;
    }

    public Optional<Event> lastEvent() {
        return Optional.of(lastEvent);
    }

    public Optional<ScalarEvent> lastScalar() {
        if (lastEvent instanceof ScalarEvent) {
            return Optional.of((ScalarEvent) lastEvent);
        }
        return Optional.empty();
    }

    public int lastColumnOrZero() {
        return (lastEvent == null ? 0 : lastEvent.getStartMark().getColumn());
    }

    public void nextScalarNotUpwards(int column) {
        //
        //   Three possible results:
        //      - found scalar non-upwards (sibling or child)
        //      - came to upward event (parent)
        //      - no more events left
        //
        while (hasNext()) {
            readNext();
            if (lastEvent.getStartMark().getColumn() < column // never move up the tree
                    || lastEvent instanceof ScalarEvent) {
                return;
            }
        }
    }

}
