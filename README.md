# Notes In Short 

Notes In Short Web is a webapp for creating summary and notes, sharing it with friends and family. Built using firebase platform. s




## Initial setup, build tools and dependencies

Notes In Short is built using Javascript, Firebase and jQuery. Javascript dependencies are managed using [bower](http://bower.io/) and Build/Deploy tools dependencies are managed using [npm](https://www.npmjs.com/). Also Notes In Short is written in ES2015 so for wide browser support we'll transpile the code to ES5 using [BabelJs](http://babeljs.io).

Install all Build/Deploy tools dependencies by running:

```bash
$> npm install
```

## Start a local development server

You need to have installed the Firebase CLI by running `npm install`.

You can start a local development server by running:

```bash
$> npm run serve
```

This will start `firebase serve` and make sure your Javascript files are transpiled automatically to ES5.

Then open [http://localhost:5000](http://localhost:5000)


## Deploy the app

Deploy to Firebase using the following command:

```bash
$> npm run build
$> firebase deploy --project <PROJECT_ID>
```

This will install all runtime dependencies and transpile the Javascript code to ES5.
Then this deploys a new version of your code that will be served from `https://<PROJECT_ID>.firebaseapp.com`


## Contributing

We'd love that you contribute to the project. Before doing so please read our [Contributor guide](../CONTRIBUTING.md).

