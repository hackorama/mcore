# JDK 8 on Ubuntu

## Default versions

```
$ java -version
openjdk version "10.0.2" 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```

```
$ lsb_release -a
Distributor ID: Ubuntu
Description:    Ubuntu 18.04.1 LTS
Release:        18.04
Codename:       bionic
```

## Remove existing JDK 

```
$ sudo apt list --installed | grep jdk
default-jdk/bionic,now 2:1.10-63ubuntu1~02 amd64 [installed]
default-jdk-headless/bionic,now 2:1.10-63ubuntu1~02 amd64 [installed,automatic]
openjdk-11-jdk/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.4 amd64 [installed,automatic]
openjdk-11-jdk-headless/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.4 amd64 [installed,automatic]
openjdk-11-jre/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.4 amd64 [installed,automatic]
openjdk-11-jre-headless/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.4 amd64 [installed,automatic]

$ sudo apt purge --auto-remove default-jdk
```

```
$ java -version
bash: /usr/bin/java: No such file or directory
$ sudo apt list --installed | grep -i jdk
$ sudo apt list --installed | grep -i jre
$ sudo apt list --installed | grep -i java
$
```

## Install JDK 8 

```
$ sudo apt install openjdk-8-jdk
```

```
$ java -version
openjdk version "1.8.0_191"
OpenJDK Runtime Environment (build 1.8.0_191-8u191-b12-0ubuntu0.18.04.1-b12)
OpenJDK 64-Bit Server VM (build 25.191-b12, mixed mode)
```

```
$ sudo apt list --installed | grep -i jdk
openjdk-8-jdk/bionic-updates,bionic-security,now 8u191-b12-0ubuntu0.18.04.1 amd64 [installed]
openjdk-8-jdk-headless/bionic-updates,bionic-security,now 8u191-b12-0ubuntu0.18.04.1 amd64 [installed,automatic]
openjdk-8-jre/bionic-updates,bionic-security,now 8u191-b12-0ubuntu0.18.04.1 amd64 [installed,automatic]
openjdk-8-jre-headless/bionic-updates,bionic-security,now 8u191-b12-0ubuntu0.18.04.1 amd64 [installed,automatic]

```
