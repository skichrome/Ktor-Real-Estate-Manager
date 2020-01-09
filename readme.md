# :sunglasses: Real Estate Manager Ktor Backend [![Build Status](https://jenkins.campeoltoni.fr/buildStatus/icon?job=ktor-real-estate-manager)](https://jenkins.campeoltoni.fr/job/ktor-real-estate-manager/)

This application is developed to be used with the Android app 
[Real Estate Manager](https://github.com/skichrome/RealEstateManager)

* Framework used : [Ktor](https://ktor.io/)

## Database credentials
To successfully build this project, you have to add a Kotlin file in the utils package (create it in com.skichrome package) named `DatabaseCredentials.kt`

### Local compilation configuration
Add these informations with your custom credentials into it :

```Kotlin
/*
 * Database credentials, to be used for deployment on your local machine.
 */

const val DB_PROD_URL = "jdbc:mysql://YOUR_DB_URL:DB_PORT/DB_NAME"
const val DB_USERNAME = "YOUR_DB_USERNAME"
const val DB_PASSWORD = "YOUR_DB_STRONG_PASSWORD"
```

### Jenkins build script configuration
Create a script execution in Jenkins, before any gradle invoke and insert these lines :

```Shell
#!/bin/sh

echo "package com.skichrome.utils" > $WORKSPACE/src/main/kotlin/com/skichrome/utils/DatabaseCredentials.kt
echo "" >> $WORKSPACE/src/main/kotlin/com/skichrome/utils/DatabaseCredentials.kt
echo "const val DB_PROD_URL = \"jdbc:mysql://YOUR_DB_URL:DB_PORT/DB_NAME\"" >> $WORKSPACE/src/main/kotlin/com/skichrome/utils/DatabaseCredentials.kt
echo "const val DB_USERNAME = \"YOUR_DB_USERNAME\"" >> $WORKSPACE/src/main/kotlin/com/skichrome/utils/DatabaseCredentials.kt
echo "const val DB_PASSWORD = \"YOUR_DB_STRONG_PASSWORD\"" >> $WORKSPACE/src/main/kotlin/com/skichrome/utils/DatabaseCredentials.kt
```
