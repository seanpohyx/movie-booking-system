# Seat
## GET /api/v1/seat
Response: 200
Response body:
```json
[
  {
    "cost": 10.90,
    "seatNumber": 10,
    "rowNumber": "A"
  },
  .
  .
  .
]
```
## GET /api/v1/seat/{rowId}/{seatNumber}
Response: 200
Response body:
```json
{
    "cost": 10.90,
    "seatNumber": 10,
    "rowNumber": "A"
}
```
## POST /api/v1/seat
Response: 201
Request body:
```json
{
  "cost": 10.90, #double
  "seatNumber": 10, #int
  "rowNumber": "A" #string
}
```
## PUT /api/v1/seat/{rowId}/{seatNumber}
Response: 200
Request body:
```json
{
  "cost": 100.00 #double
}
```
## DELETE /api/v1/seat/{rowId}/{seatNumber}
Response: 204