# Movies
## GET /api/v1/movie
Response: 200
Response body:
```markdown
[
  {
    "movieId": 1,
    "title": "The batman",
    "description": "When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
    "duration": 172,
    "casts": "Robert Pattison",
    "startDate": "2022-03-02",
    "endDate": "2022-05-02",
    "createdDate": 1654222051,
    "updatedTime": 1654222051
  },
  .
  .
  .
]
```
## GET /api/v1/movie/{movieId}
Response: 200
Response body:
```markdown
{
    "movieId": 1,
    "title": "The batman",
    "description": "When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
    "duration": 172,
    "casts": "Robert Pattison",
    "startDate": "2022-03-02",
    "endDate": "2022-05-02",
    "createdDate": 1654222051,
    "updatedTime": 1654222051
}
```
## GET /api/v1/movie/nowShowing
Response: 200
Response body:
```markdown
[
  {
    "movieId": 1,
    "title": "The batman",
    "description": "When the Riddler, a sadistic serial killer, begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
    "duration": 172,
    "casts": "Robert Pattison",
    "startDate": "2022-03-02",
    "endDate": "2022-05-02",
    "createdDate": 1654222051,
    "updatedTime": 1654222051
  },
  .
  .
  .
]
```
## POST /api/v1/movie
Response: 201
Request body:
```markdown
{
    "title": "String",
    "description": "String",
    "duration": 172 , #mins
    "casts": "String",
    "startDate": "yyyy-mm-dd",
    "endDate": "yyyy-mm-dd"
}
```
## PUT /api/v1/movie/{movieId}
Response: 200
Request body:
```markdown
{
    "title": "String",
    "description": "String",
    "duration": 172 , #mins
    "casts": "String",
    "startDate": "yyyy-mm-dd",
    "endDate": "yyyy-mm-dd"
}

```
## DELETE /api/v1/movie/{movieId}
Response: 204