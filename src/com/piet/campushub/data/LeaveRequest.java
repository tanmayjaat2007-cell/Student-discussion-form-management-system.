package com.piet.campushub.data;

import java.time.LocalDate;

/** A leave request. Status is Approved, Pending or Rejected. */
public record LeaveRequest(long id, String type, LocalDate from, LocalDate to,
                           String reason, String status, boolean sample) {

    public boolean isPending() {
        return "Pending".equals(status);
    }
}
