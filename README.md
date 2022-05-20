# Word Coordinates

We have recently been given a file that maps english words onto cartesian coordinates. As this data is of significant 
interest to use, we would like to import this data into a database, and provide a web service which will allow 
convenient querying.

We would like you to:

 - Read the provided input file (./test.dat) into a relational database. The file may have errors, so some filtering 
 will be required. 
 - Provide a service endpoint which givens the nearest word-coordinate to a given point.
 
The input file is provided. A skeleton project is also provided. This contains a build script with dependencies 
for an H2 database and some Spring support. Some SpringMVC stubs are provided for the service endpoints.
 
## Your Solution

Please implement a clean, production-ready solution for this. 

 - The solution should be correct - we will run an automated acceptance test against it
 - Your solution should be thoroughly unit tested. Integration tests are a plus. JUNIT and Spock are both provided in the build script.
 - Please design the solution to be clean, readable and maintainable

Please provide test support. JUNIT and Spock are both provided in the build script.

The skeleton is provided in SpringMVC, however, you may replace this with another library if you wish, so long as the
service contract is satisfied.

When the solution is complete, please push it back up to the git repo and let us know you are done.

## Input format

The input file contains contiguous binary messages which hold a dictionary word and an associated x/y coordinate.
 
The format of the messages is:

 - A two byte unsigned integer containing the length of the message. This is the _entire_ length of the message 
 ( (length header) + (word) + (x coord) + (y coord) + (checksum) )
 - A variable length string of ASCII characters. There is _no_ null terminator.
 - A 4 byte signed integer representing an X coordinate
 - A 4 byte signed integer representing a Y coordinate
 - A 1-byte unsigned integer containing a checksum for the message.
 
The checksum is calculated like so:

 - The unsigned byte values of all message contents are summed as a positive integer.
 - The resulting sum is divided by 255 and the remainder of the division is the checksum



```
|  2 byte length header   |   N byte string      |      4 byte X coord       |       4 byte Y coord      | checksum |
|     H1    |    H2       |   S0  ... S(N)       |  X1  |  X2  |  X3  |  X4  |  Y1  |  Y2  |  Y3  |  Y4  |   C1     |

```

Integers are in big-endian format. Signed integers are represented using twos compliment.

### Filtering checksum failures

Messages may not have the correct checksum. If a message does not have the correct checksum, it should _not_ be inserted
into the database. Instead, it should be written to a CSV file named _checksum_errors.csv_ in the following format:

```
<word>, <calculated checksum>, <checksum from file>
```

## Nearest Distance API

The nearest distance API should provide point nearest to a given point. "Nearest" is defined as the euclidean distance.
The output should contain the word name, the x and y coordinates of that word, and the distance from the given 
coordinates to the nearest word.

For example:
 
```
curl 'http://localhost:8080/nearestPoint?x=10&y=90' - incorrect parameters supply
curl -d "x=10&y=90" localhost:8080/nearestPoint - correct

```

Should return something like:

```
{
    "name":"aardvark",
    "x":123,
    "y":-87,
    "distance":15.67345
}

```



### Some useful information

Use this information to help you in your development and testing.

```
acclimatized has the coordinate (-570, 549)
accolent has the coordinate (208, -785)
accessit has the coordinate (-247, -152)
accepting has the coordinate (-559, -891)
accelerant has the coordinate (-565, -334)
acarina has the coordinate (305, -721)

There are 17 messages which do not pass checksum validation

acarines does not pass checksum validation
accede does not pass checksum validation

```

## For discussion

The file given is small, what are the considerations if that file were to grow larger (100mb, 1gb, 10gb ?)

What is your test plan for this?



