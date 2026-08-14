# AthletiQ Real-Time Event Contract

## Transport

WebSocket or Server-Sent Events.

## Event Envelope

{
  eventType,
  eventId,
  eventTimestamp,
  eventVersion,
  eventData
}

## Events

### APPLICATION_CREATED
Valid application stored.

### APPLICATION_UPDATED
Application information/status changed.

### EVALUATION_STARTED
Evaluation begins.

### EVALUATION_COMPLETED
Evaluation completes.

### RANKING_UPDATED
Candidate rankings change.

### CANDIDATE_SHORTLISTED
Candidate shortlisted.

### CANDIDATE_SELECTED
Candidate selected.

### EMAIL_SENT
Communication queued/sent.

### CANDIDATE_ACCEPTED
Candidate accepts.

### CANDIDATE_DECLINED
Candidate declines.

### CANDIDATE_REMOVED
Candidate removed.

### STATUS_CHANGED
Generic status notification.

## Source of Truth

Database state is authoritative.

Real-time events notify clients about state changes.

## Reconnection

After reconnecting, clients retrieve current state through REST APIs.
