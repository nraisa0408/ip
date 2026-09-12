package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void toString_notDoneEvent_showsTypeFromAndTo() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] team meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void toString_doneEvent_showsXBracket() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        event.markAsDone();
        assertEquals("[E][X] team meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void toSaveFormat_producesPipeDelimitedFromAndTo() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        assertEquals("E | 0 | team meeting | Mon 2pm | 4pm", event.toSaveFormat());
    }

    @Test
    void toSaveFormat_doneEvent_recordsOneFlag() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        event.markAsDone();
        assertEquals("E | 1 | team meeting | Mon 2pm | 4pm", event.toSaveFormat());
    }

    @Test
    void isOccurringOn_anyDate_returnsFalse() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        assertFalse(event.isOccurringOn(LocalDate.parse("2019-10-15")));
    }

    @Test
    void getSortDate_event_returnsNull() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");
        assertNull(event.getSortDate());
    }

    @Test
    void equals_sameDescriptionFromAndTo_returnsTrue() {
        assertEquals(new Event("trip", "2019-10-15", "2019-10-20"),
                new Event("trip", "2019-10-15", "2019-10-20"));
    }

    @Test
    void equals_differentFrom_returnsFalse() {
        assertNotEquals(new Event("trip", "2019-10-15", "2019-10-20"),
                new Event("trip", "2019-10-16", "2019-10-20"));
    }

    @Test
    void equals_differentTo_returnsFalse() {
        assertNotEquals(new Event("trip", "2019-10-15", "2019-10-20"),
                new Event("trip", "2019-10-15", "2019-10-21"));
    }

    @Test
    void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Event("trip", "2019-10-15", "2019-10-20"),
                new Event("holiday", "2019-10-15", "2019-10-20"));
    }

    @Test
    void equals_notAnEvent_returnsFalse() {
        assertNotEquals(new Event("trip", "2019-10-15", "2019-10-20"), new Todo("trip"));
    }
}
