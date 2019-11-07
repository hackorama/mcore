# Build all

Build all and test

```
$ ./gradlew check
```

# Build separately

```
$ cd demo
$ ../gradlew build
```

```
$ cd samples
$ ../gradlew build
```

# Publish

First set up PGP keys `~/.gnupg/secring.gpg` and publish keys to key servers.

Before publishing to Bintray/jCenter set the correct version to use in `gradle.properties` or as `-Pmcore.version=1.2.3`

```
$ cat gradle.properties
mcore.version=1.2.3
```

Versions with `DEV` in the name will only gets published locally to `~/.m2`

```
version=1.2.3               : publish to external maven
version=1.2.3-SNAPSHOT      : publish to external maven
version=1.2.3-DEV-SNAPSHOT  : publish to local maven
```

## Local publish

```
$ ./gradlew publishToMavenLocal
```

```
$ ls ~/.m2/repository/com/hackorama/m/core/mcore/
0.1.0-DEV-SNAPSHOT/       maven-metadata-local.xml
```

## Publish Bintray/jCenter

Publish to Bintray and then from Bintray web interface promote to jCenter.

> Upload is broken for .asc signature files for bintray plugin.
> So we do `bintrayUpload` once on the first upload to create the package,
> then 'publish' is used for updates with signatures.
>
> [Bintray Plugin Issue #255](https://github.com/bintray/gradle-bintray-plugin/issues/255)

```
$ ./gradlew -Psigning.password=<password> -Pmcore.version=1.2.3 -Pbintray.apiKey=<key> bintrayUpload
```

```
$ ./gradlew -Psigning.password=<password> -Pmcore.version=1.2.3 -Pbintray.apiKey=<key> publish
```

Or provide the key and password through `~/.gradle/gradle.properties`

```
$ vi ~/.gradle/gradle.properties
# PGP
signing.secretKeyRingFile=~/.gnupg/secring.gpg
signing.keyId=
signing.password=

# BINTRAY
bintray.user=hackorama
bintray.apiKey=

# MCORE
mcore.version=1.2.3
```

```
$ ./gradlew bintrayUpload
$ ./gradlew publish
```
