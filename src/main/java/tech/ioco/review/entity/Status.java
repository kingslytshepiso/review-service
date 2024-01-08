package tech.ioco.review.entity;

public enum Status {
    CREATED("Created"), REQUEST_SENT("Request Sent"), REMINDER_SENT("Reminder Sent"), COMPLETED("Completed");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
