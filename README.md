# map-optimization

This is the github repository for Team Senary's GPMS project. This project is a client-server application looking at
optimising the plotting of large data sets using the GoogleMaps JavaScript API (https://developers.google.com/maps/documentation/javascript/)

The system uses tweets as the large data set as tweets provide a variety of insights into events happening in various locations. 
Unfortunately, due to restrictions on the public, free to use APIs, the rate at which tweets could be captured and the number of searches were severely limited.
To overcome this, the streaming API was used over a large period of time to capture a number of tweets > 100K (although hopefully up to 1M) and stored in a local database.
This database could however be periodically refreshed to keep the tweets up to date, or should a user want to, a commercial twitter account could be created to access all of this data without having to store it.

To easily interface with the Twitter API, the twitter4j library was used (https://github.com/yusuke/twitter4j)
To create a database which is easy to distribute (for development purposes) a H2 embedded database was used (http://www.h2database.com/html/main.html) with the JDBC API (http://www.oracle.com/technetwork/java/javase/jdbc/index.html)

One of the visualisation techniques uses GeoJson (http://geojson.org/) 

GeoJson to draw UK boundaries was obtained from here: http://geoportal.statistics.gov.uk/

(Regional Dataset http://geoportal.statistics.gov.uk/datasets/european-electoral-regions-december-2015-ultra-generalised-clipped-boundaries-in-great-britain)

(Constituency Dataset http://geoportal.statistics.gov.uk/datasets/westminster-parliamentary-constituencies-december-2015-ultra-generalised-clipped-boundaries-in-great-britain)

GeoJson to draw UK grids was obtained from here : https://github.com/charlesroper/OSGB_Grids

In order to serialize and de-serialize the GeoJson (to allow for extra attributes such as the number of tweets in a region to be stored in the GeoJson), GeoJson POJOs for Jackson was used (https://github.com/opendatalab-de/geojson-jackson)

To check whether a point (tweet) is within a polygon (GeoJson area) and hence determine what area the tweet was in, the JTS Topology Suite for vector geometry was used (https://github.com/locationtech/jts) 

Communication between the client and server is performed using REST-like services using Servlets.
All responses from the servlet use JSON. This JSON has been serialized using the fastjson library (https://github.com/alibaba/fastjson)

Finally, to ensure that the servlet's can access any local files easily (such as the database or .geojson), these resources are copied into the current user's "home" directory under "gpms_senary_files".
The database and any resources that are stored under src/main/resources are automatically copied by an ANT script (build.xml), so you don't need to worry about copying files anywhere.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need Eclipse IDE for Java EE Developers (with Maven) and Apache Tomcat.

Eclipse can be obtained here : https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/oxygen2
Tomcat can be obtained here : https://tomcat.apache.org/download-90.cgi

If you are not sure how to use git/github, please take a look at these:

https://guides.github.com/activities/hello-world/
https://www.youtube.com/watch?v=1h9_cB9mPT8
https://try.github.io/levels/1/challenges/1


### Installing

1. Clone this repository to your PC (If you are unsure, please look at the links above. If that still doesn't make sense, look here: https://help.github.com/articles/cloning-a-repository/)
2. Open Eclipse and create a new workspace
3. Create a new Server in Eclipse (Open EE perspective, click 'Server' Tab and create new Apache Tomcat x (x being version of Tomcat you have)
4. Import the project in Eclipse by File > Import > General > Existing Projects into Workspace and browse to the directory that you cloned the repository to
5. Run the project by browsing to src/main/webapp and right-clicking "Run on server" on any .jsp file

### Getting Tweet Data
The database that all twitter data is stored in is twitter-data.mv.db.
Please obtain twitter data via the database, don't directly call the API


```
TweetDatabase db = new TweetDatabase();
Tweets tweets = null;
// Getting all tweets from the twitter-data.mv.db database
tweets = getAllTweetsInDefaultDb();

// Getting all tweets from another database (dbName should just be the name. I.e. No file path
// or file extension. Ensure the new database is stored in src/main/resources)
tweets = db.getAllTweets(dbName);
```

Do NOT search for keywords by loading all the tweets into memory. Use this function as it
lets the database engine do all the work without having to load all tweets into memory!

(A new function may needed to be added to search for multiple keywords)
```
TweetDatabase db = new TweetDatabase();
Tweets tweets = null;
// Getting all tweets from the twitter-data.mv.db database with a specified keyword
tweets = getAllTweetsInDefaultDb("keyword");

// Getting all tweets from another database with a specified keyword (dbName should just be the name. I.e. No file path
// or file extension. Ensure the new database is stored in src/main/resources)
tweets = db.getAllTweets(dbName);
```

### Adding Libraries
As this is a Maven project, please DO NOT add .jars manually. 

For example, say you wanted to use the fastjson library:

1. Go to https://mvnrepository.com
2. Search for "fastjson"
3. Select version number
4. Copy the Maven dependency XML 
5. Paste the XML in the pom.xml file in the project (go to the pom.xml tab and place between the <plugins> </plugins> tags)
6. Save the project.
7. You should now see the jar under Java Resources/Libraries/Maven Dependencies 

## Coding style

Please follow the Java Coding Standard specified in YSE.QA.09 (York QA Framework)
Note that most of the code is currently not up to standard. Don't worry about this too much
at this stage as this can easily be changed before submission. 
Please refer to the original documentation provided on the GPMS VLE page for full details.

### Package Names 
All packages should start with gpms.senary

### Identifier Naming Conventions (General)

All identifiers should use common U.S. spelling. This is for consistency with external libraries, including the standard Java library. For example: 
```
Color not Colour 
MyClass.initialize( ) not MyClass.initialise( )
```
* Use predicate clauses or adjectives for boolean objects or functions, e.g. heatingShouldBeOn. 
* Use action verbs for procedures and entries, e.g. removeNode. 
* Use constants rather than variables for constant values. 

### Identifier Naming Conventions (Classes and Interfaces)

Class and interface names start with a capital letter, then use lower-case with capitals separating words (rather than underscores). For example: 
```
public class StateMachine ... 
public class DataManager ... 
```
When the word in a class would be upper-case (such as an abbreviation like UWA), only the first letter should be a capital when used in an identifier. For example: 
```
public class UwaEditor ... 
public class GuiResourceBundle ... 
```
### Identifier Naming Conventions (Methods / Variables)

Method and variable names start with a lower-case letter, and use capitals to separate words (rather than underscores). For example: 
public void buildTree( Node root ); 
The naming of methods should follow the Java Beans convention. This means that properties should have a get<PropertyName>( ) method (or is<PropertyName>( ) for booleans), and read-write prop-erties should also have a set<PropertyName>( ) method. For example:
``` 
// Read-only size property. 
public int getSize( ); 
// Read-write name property. 
public String getName( ); 
public void setName( String name ); 
// Boolean readOnly property. 
public boolean isReadOnly( ); 
public void setReadOnly( boolean b ); 
```
Indexed properties should normally have get and set methods that allow you to access individual values, or an entire array.

### Identifier Naming Conventions (Constants)

Constants (static final variables) must be capitalized with underscores separating words. For example: 
public class File {
public static final String PATH_SEPARATOR = "¨; ... 
} 

### Commenting Code
Each file should have a header:
file header example:
```
/*
 * @(#) SomeClass.java 1.1 2018/02/18
 * 
 * Copyright (c) 2018 University of York.
 * All rights reserved. 
 *
 */
```

Each class/interface should have a javadoc class header
* The description should provide an overview of the class, but need not go into great detail. The description should be separated from the tags by an empty line. 
* An @author tag must be included for each author (except for inner-classes). 
* A @version tag must be included for each version of the ﬁle (except for inner-classes). 
* @see tags should be used to cross-reference related classes. 
* Anonymous classes do not need headers. 
class header example:
```
/** 
 * A class that generates new wibbles. 
 * This class generates new instances that implement the Wibble
 * interface. 
 * The exact class that is returned depends on the current WibbleSystem 
 * that is active. 
 * <p> 
 * Static getFactory( ) method should be used to create new instances
 * of WibbleFactory rather than the constructor, and new wibbles may
 * be obtained through the createNewWibble( ) method. 
 * 
 * @author Alex McManus 
 * @author Richard Joseph 
 * @version 1.1 Initial development. 
 * @version 1.2 BN998: Now works with modified database structure. 
 * @see Wibble * @see WibbleSystem 
 * @see #getFactory( ) 
 * @see #createNewWibble( ) 
 */ 
public class WibbleFactory ... 
```

Each method should have a javadoc method header
* The description should cover the purpose of the method, and any side-effects. 
* All parameters and return values should have @param or @return tags, even if they seem obvi-ous. This helps give the resulting documentation a more complete feel. 
* Tags of the same type should be lined up (e.g. all @param tags). 
* Every type of exception thrown by the method should have an @exception tag (even if there is already a tag for one of the exception’s superclasses). 
* @see tags should be used to cross-reference related methods or classes. 
* Methods in anonymous classes do not always need headers. 
* Methods in skeletal test classes do not always need headers. 

method header example:
```
/** 
 * Create a new instance of the Wibble interface. 
 * The actual class that is created depends on the current WibbleSystem
 * that is active.
 * 
 * @param newName the name to give the new Wibble. 
 * @param mode the mode to give the new Wibble,
 *             one of READY_MODE or STANDBY_MODE. 
 * @return a new Wibble. 
 * @exception LockException if a lock cannot be obtained for the Wibble.
 * @exception SQLException if an SQL error occurs. 
 * @see Wibble 
 * @see WibbleSystem 
 * @see #READY_MODE 
 * @see #STANDBY_MODE 
 */ 

 public Wibble createNewWibble( String newName, int mode ) 
        throws LockException, SQLException 
```

Please use this class as a template showing how to structure a class.


```
/*
 * @(#) SomeClass.java 1.1 2018/02/18
 * 
 * Copyright (c) 2018 University of York.
 * All rights reserved. 
 *
 */
package gpms.senary?.?; 
/** 
 * A class that generates new wibbles. 
 * This class generates new instances that implement the Wibble
 * interface. 
 * The exact class that is returned depends on the current WibbleSystem 
 * that is active. 
 * <p> 
 * Static getFactory( ) method should be used to create new instances
 * of WibbleFactory rather than the constructor, and new wibbles may
 * be obtained through the createNewWibble( ) method. 
 * 
 * @author Alex McManus 
 * @author Richard Joseph 
 * @version 1.1 Initial development. 
 * @version 1.2 BN998: Now works with modified database structure. 
 * @see Wibble * @see WibbleSystem 
 * @see #getFactory( ) 
 * @see #createNewWibble( ) 
 */ 
public class SomeClass extends implements { 
// ////////// // 
// Constants. // 
// ////////// // 
// //////////////// // 
// Class variables. // 
// //////////////// // 
// ////////////// // 
// Class methods. // 
// ////////////// // 
// /////////////////// // 
// Instance variables. // 
// /////////////////// // 
// ///////////// // 
// Constructors. // 
// ///////////// // 
// ////////////////////// // 
// Read/Write properties. // 
// ////////////////////// // 
// ///////////////////// // 
// Read-only properties. // 
// ///////////////////// // 
// //////// // 
// Methods. // 
// //////// // 
/** 
 * Create a new instance of the Wibble method. 
 * The actual class that is created depends on the current WibbleSystem
 * that is active.
 * 
 * @param newName the name to give the new Wibble. 
 * @param mode the mode to give the new Wibble,
 *             one of READY_MODE or STANDBY_MODE. 
 * @return a new Wibble. 
 * @exception LockException if a lock cannot be obtained for the Wibble.
 * @exception SQLException if an SQL error occurs. 
 * @see Wibble 
 * @see WibbleSystem 
 * @see #READY_MODE 
 * @see #STANDBY_MODE 
 */ 

 public Wibble createNewWibble( String newName, int mode ) 
        throws LockException, SQLException 

} 
```

## Running the tests

Tests not yet implemented
```
Give an example
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

Team Senary (University of York)



