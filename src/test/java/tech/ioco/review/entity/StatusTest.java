package tech.ioco.review.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {

    @Test
    void testToStringWhenCreatedThenReturnCreated() {
        // Arrange
        Status status = Status.CREATED;
        String expected = "Created";

        // Act
        String actual = status.toString();

        // Assert
        assertEquals(expected, actual, "The toString method should return 'Created' for CREATED enum value.");
    }

    @Test
    void testToStringWhenRequestSentThenReturnRequestSent() {
        // Arrange
        Status status = Status.REQUEST_SENT;
        String expected = "Request Sent";

        // Act
        String actual = status.toString();

        // Assert
        assertEquals(expected, actual, "The toString method should return 'Request Sent' for REQUEST_SENT enum value.");
    }

    @Test
    void testToStringWhenReminderSentThenReturnReminderSent() {
        // Arrange
        Status status = Status.REMINDER_SENT;
        String expected = "Reminder Sent";

        // Act
        String actual = status.toString();

        // Assert
        assertEquals(expected, actual, "The toString method should return 'Reminder Sent' for REMINDER_SENT enum value.");
    }

    @Test
    void testToStringWhenCompletedThenReturnCompleted() {
        // Arrange
        Status status = Status.COMPLETED;
        String expected = "Completed";

        // Act
        String actual = status.toString();

        // Assert
        assertEquals(expected, actual, "The toString method should return 'Completed' for COMPLETED enum value.");
    }
}
