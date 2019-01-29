# Postgresql 9.5 on Ubuntu

## Install

```
$ sudo apt install postgresql-9.5
```

## Create test user 

```
$ sudo -u postgres createuser --interactive
Enter name of role to add: test
...
$ sudo adduser test
...
```

## Create test database 

```
$ sudo -u postgres createdb test
```

```
$ sudo -u test psql
test=> \conninfo
You are connected to database "test" as user "test" via socket in "/var/run/postgresql" at port "5432".
test=> \q

$ sudo -u postgres psql
postgres=# ALTER ROLE test WITH PASSWORD 'test';
ALTER ROLE
postgres=# \q
```
