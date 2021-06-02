# Big Data Management & Analysis
## Assignment 1: MongoDB

### Introduction
IntelliJ is the recommended IDE for development. Clone the sources and open it as a project in the IDE. 
Gradle is used for dependency management and will automatically download the requisite libraries. 

Note that the `mongod` process must be running in order for this application to connect with your local instance of the database. 

### MacOS Installation
Start the server and shell using the following commands:
```
brew services start mongodb-community@4.4
mongo
```
#### Database Creation
Once in the shell, run the following set of commands:
```
use nobel
db.createCollection('prize')
db.createCollection('laureate')
```
#### Data Loading

