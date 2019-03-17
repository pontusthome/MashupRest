# MashupRest
RESTful API for getting information about artist supplying the artists MBID (Music Brainz ID).
<br><br>
**Endpoints:**
http://localhost:8080/artist/{MBID}<br>
*Example repsonse*<br>
{<br>
&emsp;"mbid":"98371d28-3e5d-4638-a2c1-468a5f3dc993",<br>
&emsp;"description":"<b>Frederick James Karlin</b> (June 16, 1936 – March 26, 2004) was an American composer of more than one hundred scores for feature films and television movies. He also was an accomplished trumpeter adept at playing jazz, blues, classical, rock, and medieval music.",<br>
&emsp;"albums":[<br>
&emsp;&emsp;{<br>
&emsp;&emsp;&emsp;"title":"Lovers and Other Strangers",<br>
&emsp;&emsp;&emsp;"id":"0669259c-6032-4bb9-b069-ac236b903cee",<br>
&emsp;&emsp;&emsp;"image":null<br>
&emsp;&emsp;},<br>
&emsp;&emsp;{<br>
&emsp;&emsp;&emsp;"title":"The Chosen Survivors",<br>
&emsp;&emsp;&emsp;"id":"0b38f7e9-370d-4286-80d9-763ea8adedd8",<br>
&emsp;&emsp;&emsp;"image":"http://coverartarchive.org/release/2efe5af4-2185-40ec-a4d5-0a992f346cbd/1492125575.jpg"<br>
&emsp;&emsp;}<br>
&emsp;]<br>
}
<br>
<hr>
<h3>Run the application:</h3>
<i>Build and run with an executable JAR</i><br>
To build the JAR: ./gradlew build<br>
To run the JAR: java -jar build/libs/gs-rest-service-0.1.0.jar<br><br>
<i>Run directly</i><br>
./gradlew bootrun
