# aj.ru iPhone price parser #

!!! NOT ACTUAL. The process to develop telegram bot was starting.

Parser for aj.ru

Info stored in $HOME/.app

## Build and run ##

```bash
$ mvn package
$ cd target
$ java -jar apple-price-parcer-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Commands ##

### read ###

```bash
$ java -jar apple-price-parcer-1.0-SNAPSHOT-jar-with-dependencies.jar read
```

Show you now prices on iPhones from aj.ru.

```
iPhone X
iPhone X 64GB —  - 77000
iPhone X 256GB —  - 87000

iPhone 8 / iPhone 8 Plus
iPhone 8 64GB —  - 48900
iPhone 8 256GB —  - 57900
iPhone 8 Plus 64GB —  - 54900
iPhone 8 Plus 256GB —  - 66900
...
```

If was changed from last saved info, then you see on first line:

```
Prices WAS CHANGED. You may use command change for see changes and update!
...
```

### change ##

```bash
$ java -jar apple-price-parcer-1.0-SNAPSHOT-jar-with-dependencies.jar change
```

If was changed then you see exampe:

```
Change WAS FOUND!
iPhone X
iPhone X 256GB:  old - 8700; new - 87000
...
```

else only:

```
Change NOT was found
```

### history ##
```bash
$ java -jar apple-price-parcer-1.0-SNAPSHOT-jar-with-dependencies.jar history
```

If was history by 5 last changes:

```
Change WAS FOUND!
iPhone X
iPhone X 256GB:  old - 8700; new - 87000
...
```

`--only-change`

Specific flag, which show only changed history.

Example: 

```bash
$ java -jar apple-price-parcer-1.0-SNAPSHOT-jar-with-dependencies.jar history --only-change
```