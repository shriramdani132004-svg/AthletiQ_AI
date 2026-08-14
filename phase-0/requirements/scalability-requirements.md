# Scalability Requirements

## Target Growth

100 applicants
→ 500 applicants
→ 5,000 applicants
→ 50,000+ applicants

## Required Optimization

- Database indexing.
- Query optimization.
- Connection pooling.
- Server-side pagination.
- Server-side filtering.
- Server-side sorting.
- Caching.
- Background jobs.
- Queue processing.
- Asynchronous email.
- Asynchronous AI evaluation.
- Object storage.
- WebSocket scaling.
- Rate limiting.

## Processing Rule

Large AI workloads must not execute inside one synchronous HTTP request.

Use:

Applications
→ Queue
→ Workers
→ AI Evaluation
→ Database.

## Database

Large application lists require:
- Pagination.
- Filtering.
- Sorting.
- Appropriate indexes.

Thousands of candidates must not be loaded into one browser response.
