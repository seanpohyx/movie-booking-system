# Screening
## GET /api/v1/screening
Response: 200
Response body:
```markdown
[
  {
    "screeningId": 1,
    "showTime": 1649500200000,
    "movieId": 1,
    "auditoriumId": 1
  },
  .
  .
  .
]
```
## GET /api/v1/screening/{screeningId}
Response: 200
Response body:
```markdown
{
  "screeningId": 1,
  "showTime": 1649500200000,
  "movieId": 1,
  "auditoriumId": 1
}
```
## POST /api/v1/screening
Response: 201
Request body:
```markdown
{
  "showTime": 1649500200000, #epoch time
  "movieId": 1, #long
  "auditoriumId": 1 #long 
}
```
## PUT /api/v1/screening/{screeningId}
Response: 200
Request body:
```markdown
{
  "showTime": 1649500200000,
  "movieId": 1,
  "auditoriumId": 1
}
```
## DELETE /api/v1/screening/{screeningId}
Response: 204