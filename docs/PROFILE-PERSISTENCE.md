# Profile PostgreSQL Persistence

## Table

profiles

## Ownership

user_id is unique so one profile belongs to one authenticated user.

## Persistence Flow

Profile API -> ProfileService -> ProfileRepository -> JPA -> PostgreSQL

## Profile Fields

- id
- user_id
- first_name
- last_name
- phone_number
- profile_photo_url
- organization_name
- organization_description