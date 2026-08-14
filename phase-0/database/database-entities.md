# AthletiQ Database Entities

## Identity

### User
- id
- role
- email
- password hash
- verification status
- account status
- timestamps

### Organization
- id
- name
- contact information
- owner relationship
- timestamps

## Recruitment

### Event
- id
- organization reference
- name
- sport
- description
- location
- start date
- end date
- registration deadline
- required player count
- eligibility requirements
- rules
- banner reference
- status
- timestamps

### Form
- id
- event reference
- version
- status
- published timestamp
- timestamps

### FormField
- id
- form reference
- field type
- label
- required flag
- validation configuration
- ordering
- field configuration

### Application
- id
- event reference
- player reference
- application data
- status
- submission timestamp
- validation status
- duplicate status
- timestamps

### Player
- id
- user reference
- name
- contact information
- age
- location
- position
- experience
- sports information
- achievements
- performance records

## Evaluation

### EvaluationCriteria
- id
- event reference
- name
- description
- weight
- scoring configuration

### ObjectiveEvaluation
- id
- application reference
- score
- evaluation metadata
- timestamp

### AIEvaluation
- id
- application reference
- AI score
- recommendation
- strengths
- weaknesses
- explanation
- model/version
- timestamp

### Ranking
- id
- event reference
- application reference
- final score
- rank
- ranking version
- timestamp

## Selection

### Selection
- id
- application reference
- event reference
- decision
- reason
- timestamp

### CandidateResponse
- id
- selection reference
- response
- secure token reference
- expiration
- responded timestamp

### StatusHistory
- id
- application reference
- previous status
- new status
- actor
- reason
- timestamp

## Communication

### EmailMessage
- id
- event reference
- candidate reference
- template
- status
- timestamp
- failure information

## Future

- AuditLog
- Trial
- TrialEvaluation
- Team
- TeamPlayer
- Document
- AnalyticsSnapshot

## Relationship

Organization
→ Users
→ Events
→ Forms
→ Applications
→ Evaluations
→ Rankings
→ Selections
→ Candidate Responses
→ Communications.
