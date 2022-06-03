# Seat Booking
## GET /api/v1/seatbooking
Response: 200
Response body:
```markdown
[
  {
    "seatBookingId": 1,
    "bookedTime": 1649327400000,
    "auditoriumId": 1,
    "seatNumber": 10,
    "rowNumber": "A",
    "accountId": 1,
    "screeningId": 1
  },
  .
  .
  .
]
```
## GET /api/v1/seatbooking/{seatBookingId}
Response: 200
Response body:
```markdown
{
  "seatBookingId": 1,
  "bookedTime": 1649327400000,
  "auditoriumId": 1,
  "seatNumber": 10,
  "rowNumber": "A",
  "accountId": 1,
  "screeningId": 1
}
```
## POST /api/v1/seatbooking
Response: 201
Request body:
```markdown
{
  "bookedTime": 1649327400000, #epoch time
  "auditoriumId": 1,
  "seatNumber": 10,
  "rowNumber": "A",
  "accountId": 1,
  "screeningId": 1
}
```
## PUT /api/v1/seatbooking/{seatBookingId}
Response: 200
Request body:
```markdown
{
  "bookedTime": 1649327400000, #epoch time
  "auditoriumId": 1,
  "seatNumber": 10,
  "rowNumber": "A",
  "accountId": 1,
  "screeningId": 1
}
```
## DELETE /api/v1/seatbooking/{seatBookingId}
Response: 204