# Auditorium
## GET / auditorium
Response: 200
Response body:
```markdown
[
  {
    "auditoriumId": 1,
    "numberOfSeats": 15
  },
  .
  .
  .
]
```
## GET / auditorium / {auditoriumId}
Response: 200
Response body:
```markdown
{
    "auditoriumId": 1,
    "numberOfSeats": 15
}
```
## POST / auditorium
Response: 201
Request body:
```markdown
{
  "numberOfSeats": 15 #int
}
```
## PUT / auditorium/ {auditoriumId}
Response: 200
Request body:
```markdown
{
  "numberOfSeats": 10 #int
}
```
## DELETE / auditorium/ {auditoriumId}
Response: 204