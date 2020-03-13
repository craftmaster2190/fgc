# Fgc

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 8.3.3.

## ChangeLog

March 11, 2020 Joe separated the client and server code.
    we have a parent package.json to handle the husky tasks
    and a client package.json to build the app

## Up and running

Install yarn
To build the client, change into the client dir and run yarn 
To run the client, change into the client dir and call `npm run start`

Install postgres and setup with username/passowrd `postgress`
To build the server change into the server dir and run `gradlew build` 
To run the server change into thserver dir and run `gradlew runBoot`
you may need to pass `-Dspring.session.jdbc.initialize-schema=always` at least once to setup the database

There is a make file and a start/stop script in the root dir. Your MMV

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).


To Login -> Create User -> Set Name -> Set Family (Insert or Update)
To Login -> Get Users -> Pick User
If Logged In -> Get Self
To Switch User -> Get Users -> Pick User


INIT: Get Self OR Get Users OR Create User
On Switch User: Get Users -> Pick User OR Create User


For admin???

