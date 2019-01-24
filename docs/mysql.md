# MySQL/MariaDB dev server on Ubuntu

## Install 

```
sudo apt-get update
sudo apt-get install mariadb-server
```

## Set root user password

```
sudo /usr/bin/mysql_secure_installation
```

## Create a new admin user

```
$ sudo mysql -u root -p
Enter password:
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Server version: 10.1.34-MariaDB-0ubuntu0.18.04.1 Ubuntu 18.04

MariaDB [(none)]> CREATE USER 'admin'@'localhost' IDENTIFIED BY 'secret';
Query OK, 0 rows affected (0.01 sec)

MariaDB [(none)]> GRANT ALL PRIVILEGES ON * . * TO 'admin'@'localhost';
Query OK, 0 rows affected (0.00 sec)

MariaDB [(none)]> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)

MariaDB [(none)]> quit
```

## Create a new dev database and dev user 


```
$ mysql -u admin -p
Enter password:
Server version: 10.1.34-MariaDB-0ubuntu0.18.04.1 Ubuntu 18.04

MariaDB [(none)]> CREATE DATABASE 'test';
Query OK, 1 row affected (0.06 sec)

MariaDB [(none)]> CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
Query OK, 0 rows affected (0.05 sec)

MariaDB [(none)]> GRANT ALL PRIVILEGES ON test.* TO 'test'@'localhost';
Query OK, 0 rows affected (0.09 sec)
```

## Set locale and timezone 

```
sudo update-locale LANG=en_US.UTF8
```

```
$ sudo dpkg-reconfigure tzdata

Current default time zone: 'US/Pacific'
Local time is now:      Thu Jan 24 10:07:39 PST 2019.
Universal Time is now:  Thu Jan 24 18:07:39 UTC 2019.
```

```
MariaDB [(none)]> SELECT @@global.time_zone;
+--------------------+
| @@global.time_zone |
+--------------------+
| SYSTEM             |
+--------------------+
```

```
MariaDB [(none)]> SET GLOBAL time_zone = `-8:00`;
Query OK, 0 rows affected (0.00 sec)
MariaDB [(none)]>  SELECT @@GLOBAL.time_zone;
+--------------------+
| @@GLOBAL.time_zone |
+--------------------+
| -08:00             |
+--------------------+
1 row in set (0.00 sec)

MariaDB [(none)]>
```

