# SeatAuditorium
## GET /api/v1/seatAuditorium
Response: 200
Response body:
```json
[
  {
    "auditoriumId": 1,
    "seatNumber": 10,
    "rowNumber": "A"
  },
  .
  .
  .
]
```
## GET /api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}
Response: 200
Response body:
```json
{
  "auditoriumId": 1,
  "seatNumber": 10,
  "rowNumber": "A"
}
```
## POST /api/v1/seatAuditorium
Response: 201
Request body:
```json
{
  "auditoriumId": 1,
  "seatNumber": 10,
  "rowNumber": "A"
}
```
## DELETE /api/v1/seatAuditorium/{rowId}/{seatNumber}/{auditoriumId}
Response: 204