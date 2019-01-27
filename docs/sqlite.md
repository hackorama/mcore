# Sqlite for Ubuntu


```
$ lsb_release -a
Distributor ID: Ubuntu
Description:    Ubuntu 18.04.1 LTS
Release:        18.04
Codename:       bionic
```

```
$ sudo apt install sqlite3 libsqlite3-dev
```

```
$ sqlite3 database.test
SQLite version 3.22.0 2018-01-22 18:45:57
Enter ".help" for usage hints.
sqlite> select sqlite_version();
3.22.0
sqlite>
```
