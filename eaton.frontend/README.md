8# Eaton: Front-end Environment

## Installation

- Install **XCode Command Line Tools** (OSX)
  - Type `xcode-select --install` in terminal (OSX Mavericks and newer)
  - Xcode may or may not be necessary for the above command if it fails, Download & Install Xcode, and try again.
    - <https://developer.apple.com/xcode/>
    - <https://developer.apple.com/download/more/>


- Install **Node.js 6.11.4 LTS**
  - Install NVM (Is strongly recommend to install NVM to handle Node.js versions between projects)
  - NVM Info: <https://github.com/creationix/nvm>
  - Install NVM on macOS and generic Unix environments:
    - `curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash`
  - Install NVM on Windows:
    - <https://github.com/coreybutler/nvm-windows>
  - Install NODE.js. (Latest LTS Version):
    - `nvm install 6.11.4`
  - If you want to set *node 6.11.4* as the default node.js version in your system:
    - `nvm alias default 6.11.4`
  - If not, you can set node v6.11.4 as the temporary active version:
    - `nvm use 6.11.4`
  - Update **npm** to the latest version
    - `npm install -g npm`


- Install all Front-End dependencies:
  - Go to the folder `/eaton.frontend/` in the console / terminal app
  - `cd /eaton.frontend/`
  - run `npm install`




## Development Process:

- Start the gulp process that compiles SASS to CSS:
  - `cd /eaton.frontend/`
  - run `npm start`
- Run a full maven build
  - Start AEM
  - Open a sencond tab / console window
  - `cd /eaton.frontend/`
  - and run `npm run maven`
- After running a maven build, open other tab in your console an start aemsync
  - `cd /eaton.frontend/`
  - `npm run aemsync`


### Gulp Tasks Available
  - List all the npm scripts available: `npm run`
  - `npm start`: Starts Gulp Development tasks with the Gulp Watcher enabled.
  - `npm run aemdev`: It compiles all JS-ES6 and SCSS to JS-ES5 / CSS files, and it saves the files under aem/clientlibs folder.
  - `npm run aemsync`: It syncs HTML, CSS, JS file changes with the aem instance that is currently running (port:4502)
  - `npm run maven`: Shortcut for `mvn -PautoInstallPackage clean install`


### Notes
---
1. If `aemsync` and `npm start` are running in the background, everytime you save an HTML, SCSS or JS file, these files will be synchronized with AEM, that means you can just save your files using the code editor of your preference, then reload the browser to see you updates.
2. You might want to run `npm run maven` every time you merge a different branch into your branch.
3. This project uses **ESLint** and **StyleLint** as a tool to identify and report patterns/code style in Javascript and SCSS Files. **Please Fix Lint errors in the components you are working on before pushing new commits**.
4. For module-product-tabs.css, currently we are not mainaining any scss file and only for static css it is being maintained. To make chananges to this file, we have to amend          /etc/designs/eaton/clientlib/shared/module-product-tabs/css/module-product-tabs.css directly. In future, we will move it to scss as well.
