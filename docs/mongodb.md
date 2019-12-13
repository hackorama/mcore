# MongoDB on MacOS

```
$ wget https://fastdl.mongodb.org/osx/mongodb-macos-x86_64-4.2.2.tgz
$ tar -xzvf mongodb-macos-x86_64-4.2.2.tgz
$ cd mongodb-macos-x86_64-4.2.2
$ mkdir -p /data/db
$ sudo chown -R `id -un` /data/db
$ ~/hackorama/platform/mongodb-macos-x86_64-4.2.2/bin/mongod -version
db version v4.2.2

$ ~/mongodb-macos-x86_64-4.2.2/bin/mongod
...
2019-12-11T10:04:59.774-0800 I  NETWORK  [initandlisten] waiting for connections on port 27017
...

$ ~/hackorama/platform/mongodb-macos-x86_64-4.2.2/bin/mongo
...
> db.version()
4.2.2
>

```

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
