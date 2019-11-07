# PGP

PGP for Maven artifact signing

## Prepare

```
$ gpg --version
gpg (GnuPG) 1.4.20
```

```
$ sudo apt-get install rng-tools
$ sudo rngd -r /dev/urandom
```

## Create

```
$ gpg --gen-key
...
Real name: mCore Project
Email address: hackorama@hackorama.com
Comment: Artifact Signing Key
You selected this USER-ID:
    "mCore Project (Artifact Signing Key) <hackorama@hackorama.com>"
...
gpg: ~/.gnupg/trustdb.gpg: trustdb created
...
pub   2048R/XXXXXXX8 2019-08-19
      Key fingerprint = XXXX XXXX XXXX XXXX XXXX  XXXE XXXX XXXX XXX2 XXXX
uid                  mCore Project (Artifact Signing Key) <hackorama@hackorama.com>
sub   2048R/XXXXXXXX 2019-08-19
```

```
$ gpg --list-keys
/home/hackorama/.gnupg/pubring.gpg
----------------------------------
pub   2048R/XXXXXXX8 2019-08-19
uid                  mCore Project (Artifact Signing Key) <hackorama@hackorama.com>
sub   2048R/XXXXXXXX 2019-08-19
$
```

## Publish

```
$ gpg --keyserver pgp.mit.edu --send-key XXXXXXXX
$ gpg --keyserver pool.sks-keyservers.net --send-key XXXXXXXX
$ gpg --keyserver keys.gnupg.net --send-key XXXXXXXX
$ gpg --keyserver keyserver.ubuntu.com --send-key XXXXXXXX
```

## Search

```
$ gpg --keyserver keys.gnupg.net --search-keys mcore@hackorama.com
gpg: searching for "mcore@hackorama.com" from hkp server keys.gnupg.net
(1)	mCore Project (release signing) <mcore@hackorama.com>
	  2048 bit RSA key 1B93B750, created: 2019-08-22, expires: 2021-08-21
Keys 1-1 of 1 for "mcore@hackorama.com".  Enter number(s), N)ext, or Q)uit > 1
gpg: requesting key 1B93B750 from hkp server keys.gnupg.net
gpg: key 1B93B750: "mCore Project (release signing) <mcore@hackorama.com>" not changed
gpg: Total number processed: 1
gpg:              unchanged: 1
```

## Revocation

```
$ cd ~/.gnupg
$ gpg --output revoke.XXXXXXXX.asc --gen-revoke XXXXXXXX
...
Revocation certificate created.
...
```

## Delete

```
$ gpg --delete-keys XXXXXXXX
$ gpg --delete-secret-keys XXXXXXXX
```

