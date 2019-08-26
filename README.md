[![](https://jitpack.io/v/chintan369/Geo-FireStore-Query.svg)](https://jitpack.io/#chintan369/Geo-FireStore-Query)

# Geo-FireStore-Query
This project is created to give support to search geo-location based items using firestore's native query which also supports pagination and all the kind of condition which firestore query does.

[![](https://miro.medium.com/max/1400/1*a2Da_CQHUsSKTCTRI2tYhQ.png)]()

**Step 1.** Add the JitPack repository to your root (project level) build.gradle

```gradle
allprojects {
  repositories {
	  ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Step 2.** Add the dependency

```gradle
dependencies {
  implementation 'com.github.chintan369:Geo-FireStore-Query:latest-version'
}
```

## Usage in your Firestore Query

### To Set Location Value in your document

```kotlin
val db = FirebaseFirestore.getInstance()

val document = db.collection("users").document("abc")

document.set(data)
	.addOnSuccessListener{ _ ->
		//Set Location After your document created on firestore db
		document.setLocation(latitude, longitude, fieldName)
		//fieldName is optional, if you will not pass it will set location in default field named "g"
		//Also will add field named "geoLocation" as GeoPoint including latitude and longitude
		//to count distance when querying the data
	}
	.addOnFailureListener { exception ->
		//Document write failed
	}
```

### To Retrive the List of items from center location with the given radius

```kotlin
val db = FirebaseFirestore.getInstance()

/*Create two object to pass location and distance for radius*/
val centerLocation = Location(centerLatitude, centerLongitude)
val distanceForRadius = Distance(1.0, DistanceUnit.KILOMETERS) // or you can set unit as DistanceUnit.MILES if you want to find for 1 mile

val geoQuery = GeoQuery()
		.collection("users")
		.whereEqualTo("status","approved")
		.whereEqualTo("country","IN")
		.whereNearToLocation(centerLocation, distanceForRadius, fieldName) 
		//fieldName if you have passed at time of setLocation else it will take default as "g" if you do not pass
		.startAfter(lastDocument) //optinal (for pagination)
		.limit(10) // If you requires only 10 data per query
```

### Listen continues data with real-time changes

```
geoQuery.addSnapshotListener { firebaseFirestoreException, addedOrModifiedDataList, removedList  ->
	...
	//Do your stuff here
	//If exception occurs, firebaseFirestoreException will not be null else
	//In addedOrModifiedDataList, you will get all data that falls within distance and given query 
	//as either ADDED for first time in query or has MODIFIED as was in the query from first.
	//In removedList, you will get data which is not relevent to your query or out of distance
}
```

### Listen data for only once

```
geoQuery.get()
	.addOnCompleteListener { firebaseFirestoreException, addedOrModifiedDataList, removedList  ->
		...
		//Do your stuff here
	}
```

#### OR

```
geoQuery.get()
	.addOnSuccessListener { addedOrModifiedDataList, removedList  ->
		...
		//Do your stuff here
	}
	.addOnFailureListener { exception ->
		//If any exception occured
	}
```
