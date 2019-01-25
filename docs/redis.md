# Redis dev server on Ubuntu

## Setup 

```
sudo apt install unzip
sudo apt install make
sudo apt install gcc
sudo apt install build-essential
sudo apt install tcl8.5
```

## Build 

```
$ wget http://download.redis.io/redis-stable.tar.gz
$ gunzip redis-stable.tar.gz
$ cd redis-stable/
$ make
$ make test
$ sudo make install
```

## Run

```
$ sudo redis-server
25415:C 25 Jan 2019 09:32:05.063 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
25415:C 25 Jan 2019 09:32:05.063 # Redis version=5.0.3, bits=64, commit=00000000, modified=0, pid=25415, just started
...
25415:M 25 Jan 2019 09:32:05.067 * Ready to accept connections
```

## Test

```
$ redis-cli
127.0.0.1:6379> PING
PONG
127.0.0.1:6379> SET greeting "hello world"
OK
127.0.0.1:6379> GET greeting
"hello world"
127.0.0.1:6379>
```
