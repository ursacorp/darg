# Darg

Say goodbye to status meetings.

## Development Setup

### Install dependencies

    brew install postgres
    brew install leiningen
    gem install premailer
    npm install

## Development Flow
This is just my (@venantius) flow, but I think it's informative. I have many windows open at once, but the important windows are the following:

1. `lein run server` - the app
2. `grunt watch` - watch for changes to assets and recompile
3. `lein repl :connect` - a REPL, connected to the app
4. `lein with-profile test test-refresh :growl` - watch the backend file system, running tests when things change, and send me [growl](http://growl.info/) notifications. If you don't have growl installed then just leave off that keyword.

## Usage

`lein run server` to start a server running on localhost:8080

## Deployment

We're deploying to AWS. At the moment CircleCI is configured to automatically deploy to AWS on a successful build.

App URL: `http://darg.io`

Do the following to set up the app for local development: `git remote add heroku git@heroku.com:darg.git`

## Scheduled Jobs

We use Heroku to trigger emails every hour through an authenticated request to the AWS server.

## License

Copyright © 2015 UrsaCorp. All rights reserved.
