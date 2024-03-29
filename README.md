# Big Data Management & Analysis

## Assignment 1: MongoDB

### Introduction

IntelliJ is the recommended IDE for development. Clone the sources and open it as a project in the IDE. Gradle is used
for dependency management and will automatically download the requisite libraries.

Note that the `mongod` process must be running in order for this application to connect with your local instance of the
database.

---

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

Run the following set of commands to execute the JavaScript scripts to insert the documents. These scripts are available
in the `scripts` directory.

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

---

### Example Operations

1. Count of Nobel Laureates in the Field of Medicine

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

---

### Operations

#### Insaaf

1. Percentage of Male and Female Nobel Laureates

```
db.laureate.aggregate([
    {
        $group: {
            _id: null,
            male: { $sum: { $cond: [ { $eq: [ "$gender", "male" ] }, 1, 0 ] } },
            female: { $sum: { $cond: [ { $eq: [ "$gender", "female" ] }, 1, 0 ] } },
            total: { $sum: 1 },
        }
    },
    {
        $project: {
            percentMale: {
                $multiply: [
                    100, { $divide: [ "$male", "$total" ] }
                ]
            },
            percentFemale: {
                $multiply: [
                    100, { $divide: [ "$female", "$total" ] }
                ]
            }

        }
    }
])
```

2. Update Laureate Death Data

```
db.laureate.update(
    { firstname : "Chen Ning", surname : "Yang" },
    { $set: { died : "2021-06-04", diedCountry : "South Africa", diedCountryCode : "ZA", diedCity : "Cape Town" } } )
```

3. Count of South African Nobel Laureates

```
db.laureate.find( { bornCountry : "South Africa" } ).count()
```

4. Average Age of South African Laureates

```
db.laureate.aggregate([
    { $match: { $and: [ { bornCountry : "South Africa" }, { died : "0000-00-00" } ] } },
    {
        $project: {
            date: "$born",
            laureateAge: { $divide: [ { $subtract: [ new Date(), { $toDate : "$born" }] }, (365*24*60*60*1000) ] }
        }
    },
    {
        $group: {
            _id: null,
            ages: { $sum: "$laureateAge" },
            total: { $sum: 1 },
        }
    },
    {
        $project: {
            averageAge: {
                $divide: [
                    "$ages", "$total"
                ]
            }
        }
    }
])
```

---

### Operations

#### Soo

1. Count of laureates who received prizes in chemistry or physics

```
db.prize.find(
    { $or : [ {category:'physics'} , {category:'chemistry'} ] },
    { "laureates.firstname" : 1, "laureates.surname" : 1, category : 1}
).count()
```   

2. Log-odds of a laureate being South African
```
db.laureate.aggregate([
    {
        $group: {
            _id: null,
            sa: { $sum: { $cond: [ { $eq: [ "$bornCountry", "South Africa" ] }, 1, 0 ] } },
            other: { $sum: { $cond: [ { $ne: [ "$bornCountry", "South Africa" ] }, 1, 0 ] } },
            total: { $sum: 1 },
        }
    },
    {
        $project: {
            log_odds : {
                $ln : { $divide: [ "$sa", "$other" ] }
            }
        }
    }
])
```   

3. Organizations that were awarded Nobel Prizes
```
db.laureate.distinct(
    "firstname",
    { gender : "org" } 
)
```

4. Laureates who won 2 or more prizes
```
db.laureate.find(
    {
        $expr: {
            $gte:[ {$size : "$prizes"}, 2 ]
        }
    },
    {
        firstname : 1, surname : 1, count : {$size : "$prizes"}, _id : 0    
    }
)
```
---

### Operations

#### Roscoe

1. Creating a new laureate

```
db.laureate.insertOne( {
      firstname: "Jack",
      surname: "Sparrow",
      born: "1963-06-09",
      died: "0000-00-00",
      bornCountry: "USA",
      bornCountryCode: "US",
      bornCity: "Owensboro",
      gender: "male",
      prizes: [
        {
          year: 2003,
          category: "movies",
          share: 1,
          motivation:
            '"This is the day you will always remember as the day you almost caught Captain Jack Sparrow."',
        },
      ],
      affiliations: [
        {
          name: "Carribean University",
          city: "Atlantis",
          country: "United Carribean Seas",
        },
      ],
    })
```

2. Count number of Nobel Prizes awarded since 2015 (inclusive)

```
db.prize.find({year:{$gte:2015}}).count()
```

3. Count number of living Laureates worldwide

```
db.laureate.aggregate( [
    { $match: { died : "0000-00-00" } }, 
    { $count:"laureates_alive" }
] )
```

4. Delete a laureate

```
db.laureate.deleteOne( 
    {
      firstname: "Jack",
      surname: "Sparrow",
    } 
)
```
