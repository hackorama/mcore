# Feature 

```
$ git checkout master
Already on 'master'
Your branch is up to date with 'origin/master'.
$ git fetch origin
$ git reset --hard origin/master
HEAD is now at 7d81e25 Updating source code links
$ git branch
* master
```

```
$ git checkout -b micronaut
Switched to a new branch 'micronaut'
$ git push -u origin micronaut
Total 0 (delta 0), reused 0 (delta 0)
remote:
remote: Create a pull request for 'micronaut' on GitHub by visiting:
remote:      https://github.com/hackorama/mcore/pull/new/micronaut
remote:
To https://github.com/hackorama/mcore.git
 * [new branch]      micronaut -> micronaut
Branch 'micronaut' set up to track remote branch 'micronaut' from 'origin'.
$ $ git branch
  master
* micronaut
$ git branch
  master
* micronaut
```
