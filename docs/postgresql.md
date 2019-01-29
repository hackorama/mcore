# Postgresql 9.5 on Ubuntu

## Install

```
$ sudo apt install postgresql-9.5
```

## Create test DB and user 

```
$ sudo -u postgres createuser --interactive
$ sudo -u postgres createdb test
Enter name of role to add: test
...
$ sudo adduser test
...

$ sudo -u test psql
test=> \conninfo
You are connected to database "test" as user "test" via socket in "/var/run/postgresql" at port "5432".
test=> \q

$ sudo -u postgres psql
postgres=# ALTER ROLE test WITH PASSWORD 'test';
ALTER ROLE
postgres=# \q
```
