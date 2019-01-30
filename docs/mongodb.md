# MongoDB on Ubuntu

```
$ lsb_release -a
Distributor ID: Ubuntu
Description:    Ubuntu 18.04.1 LTS
Release:        18.04
Codename:       bionic
```
```
$ sudo apt install mongodb
```

```
$ mongod --version
db version v3.6.3
```

```
$ sudo service mongodb start
 * Starting database mongodb                                                                                     [ OK ]
$
```

```
$ mongo
MongoDB shell version v3.6.3
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.6.3
> db.version()
3.6.3
> db.serverCmdLineOpts()
{
...
                "config" : "/etc/mongodb.conf",
...
                "net" : {
                        "bindIp" : "127.0.0.1"
                },
...
                        "dbPath" : "/var/lib/mongodb",
...
                        "path" : "/var/log/mongodb/mongodb.log"
...
```

```
$ sudo vi /etc/mongodb.conf
port=27017
```

```
$ sudo service mongodb restart
```

```
$ mongo
> db.serverCmdLineOpts()
...
                        "bindIp" : "127.0.0.1",
                        "port" : 27017
...
```
