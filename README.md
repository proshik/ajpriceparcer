# ApplePricesBot #

[![Travis](https://travis-ci.org/proshik/applepricesbot.svg?branch=master)](https://travis-ci.org/proshik/ajpriceparcer.svg?branch=master)
[![codecov](https://codecov.io/gh/proshik/applepricesbot/branch/master/graph/badge.svg)](https://codecov.io/gh/proshik/applepricesbot)
[![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/proshik/applepricesbot/issues)

[Telegram bot](https://t.me/ApplePrices_bot) for check prices on apple products in several shops SpB and Moscow

## Build and Run ##

1.Prepare environment variable

```bash
APPLEPRICEPARCER_TELEGRAMUSERNAME - telegram bot name username;
APPLEPRICEPARCER_TELEGRAMTOKEN - telegram bot token;
APPLEPRICEPARCER_DBPATH - path to DB file.
```

2.Print docker commands

```docker
$ docker build -t applepricesbot:latest .
```

Do not forget insert values for APPLEPRICEPARCER_TELEGRAMTOKEN and APPLEPRICEPARCER_DBPATH:

```docker
$ docker run --rm \
-e APPLEPRICEPARCER_TELEGRAMUSERNAME='' \
-e APPLEPRICEPARCER_TELEGRAMTOKEN='' \
-e APPLEPRICEPARCER_DBPATH='/app/data/database.db' \
--mount=type=bind,source="$(pwd)"/data,target=/app/data \
--name applepricesbot applepricesbot:latest
```

## Usage

Print /start command after running bot service.

## TODO

- tests;
- internalization;
- more providers;
- concurrent screening.

## Patch 

Welcome!
