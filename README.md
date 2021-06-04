# Big Data Management & Analysis
## Assignment 1: MongoDB

### Introduction
IntelliJ is the recommended IDE for development. Clone the sources and open it as a project in the IDE. 
Gradle is used for dependency management and will automatically download the requisite libraries. 

Note that the `mongod` process must be running in order for this application to connect with your local instance of the database. 

- - - -
### Installation
Start the server and shell using the following commands.
```
brew services start mongodb-community@4.4
mongo
```
#### Database Creation
Once in the shell, run the following set of commands to create the database and collections.
```
use nobel
db.createCollection('prize')
db.createCollection('laureate')
```
Use these commands to test that the operations have succeeded.
```
show dbs
show collections
```
#### Data Loading
Run the following set of commands to execute the JavaScript scripts to insert the documents.
These scripts are available in the `scripts` directory.
```
load("/absolute/path/to/insert_prize_documents.js")
load("/absolute/path/to/insert_laureate_documents.js")
```
Alternatively, you can use the `db.collection.insertMany()` operation. This approach is not recommended. 
```
db.prize.insertMany([...])
db.laureate.insertMany([...])
```
Use these commands to test that the insert operations have succeeded.
```
db.prize.count()
db.laureate.count()
```
- - - -
### Example Operations
1. Number of Nobel Laureates in the Field of Medicine
```
db.prize.aggregate([ 
        { $match : {category : "medicine"} }, 
        { $unwind: "$laureates" }, 
        { $group : { _id : null, count: { $sum : 1} } }
])
```
2. Percentage of Alive and Dead Nobel Laureates
```
db.laureate.aggregate([
    {
        $group: {
            _id: null,
            alive: { $sum: { $cond: [ { $eq: [ "$died", "0000-00-00" ] }, 1, 0 ] } },
            dead: { $sum: { $cond: [ { $eq: [ "$died", "0000-00-00" ] }, 0, 1 ] } },
            total: { $sum: 1 },
        }
    },
    {
        $project: {
            percentAlive: {
                $multiply: [
                    100, { $divide: [ "$alive", "$total" ] }
                ]
            },
            percentDead: {
                $multiply: [
                    100, { $divide: [ "$dead", "$total" ] }
                ]
            }

        }
    }
])
```
3. Full Names and Country of Birth of Multiple Laureates
```
db.laureate.find( 
    { "prizes.1": { $exists: true } }, 
    { firstname: 1, surname: 1, bornCountry: 1, _id: 0 } 
).pretty()
```
4. Ranking of Nobel Laureates by Country of Birth
```
db.prize.aggregate( [ 
    { $unwind : "$laureates" }, 
    { $sortByCount: "$laureates.bornCountry" } 
])
```
