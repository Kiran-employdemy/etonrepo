module.exports = {

  'env': {
    'browser': true,
    'node': true,
    'es6': true,
    'jquery': true
  },

  'parserOptions': {
    'ecmaVersion': 8,
    "sourceType": "module",
    'ecmaFeatures': {

    }
  },

  'extends': 'eslint:recommended',

  /**
  * ERROR Level: Severity should be one of the following:
  *  0 = off
  *  1 = warn
  *  2 = error
  */
  'rules': {

    // Rules Based on airbnb JavaScript Style Guide
    // MORE INFO: https://github.com/airbnb/javascript

    'array-bracket-spacing': [2, "never"],
    'arrow-spacing': [2, { "before": true, "after": true }], // ES6
    'camelcase': [2, { "properties": "never" }], // require camelCase for var and property names, disallowing underscore notation
    'comma-dangle': [2, "never"], // require or disallow trailing commas
    'comma-style': [2, "last"], // disallow 'comma first' notation
    'consistent-this': [2, "self"], // force 'self' for context reference - not '_this', 'that', etc.
    'curly': [2], // require curly braces around all control blocks, regardless of length
    'dot-notation': [2, { "allowPattern": "^[a-z]+(_[a-z]+)+$" }],
    'eqeqeq': 2, // require use of explicit equality comparators - "===", "!=="
    'func-names': 0, // require "double function naming" convention for clearer stack traces
    'generator-star-spacing': [2, {"before": true, "after": true}], // ES6: This rule aims to enforce spacing around the * of generator functions.
    'indent': [2, 2, { "SwitchCase": 2 }],
    'key-spacing': [2, { "beforeColon": false, "afterColon": true, "mode": "minimum"  }],
    'keyword-spacing': [2, { "before": true, "after": true, "overrides": {} }], // enforce consistent spacing before and after keywords
    'new-cap': 2, // require capital letter for new constructors
    'no-alert': 2, // This rule is aimed at catching debugging code that should be removed
    'no-array-constructor': 2, // disallow use of the Array constructor - forces literal notation instead
    'no-console': 0,
    'no-const-assign': 2,
    'no-debugger': 1, // This rule disallows debugger statements.
    'no-duplicate-imports': 2, // ES6: disallow duplicate module imports
    'no-empty': 2,
    'no-eq-null': 2,
    'no-inner-declarations': [2, "both"], // disallows function definition outside first level or body of a function
    'no-mixed-spaces-and-tabs': 2, // disallow mix of spaces and tabs in line indentation
    'no-multi-spaces': [2, { "exceptions": { "ImportDeclaration": true, "Property": true } }],
    'no-new-object': 2, // disallow use of the Object constructor - forces object literal notation instead
    'no-shadow-restricted-names': 2, // disallow overwrite of restricted names / reserved kws
    'no-this-before-super': 2,
    'no-trailing-spaces': 2, // disallow trailing whitespace at the end of lines
    'no-unused-vars': [2, { "vars": "local", "args": "none" }],
    'no-useless-rename': 2, // ES6: This rule disallows the renaming of import, export, and destructured assignments to the same name.
    'no-var': 2, // ES6: This rule is aimed at discouraging the use of var and encouraging the use of const or let instead.
    'no-whitespace-before-property': 2,
    'no-with': 2, // The with statement is potentially problematic because it adds members of an object to the current scope
    'one-var': [2, { var: "never", let: "never", const: "never" }], // This rule enforces variables to be declared either together or separately per function ( for var) or block (for let and const) scope.
    'quote-props': [2, "as-needed", { "keywords": true, unnecessary: true }], // require quotes around object literal property names
    'quotes': [2, "single", "avoid-escape"], // require single quotes for string literals, unless a string literal contains single quotes, in which case double quotes will be acceptable.
    'semi': [2, "always"], // force semicolons at line ending
    'space-before-blocks': [2, "always"], // requires space before block curly braces
    'space-infix-ops': 2, // require spaces around operators
    'space-unary-ops': [2, {"words": true, "nonwords": false}],
    'spaced-comment': [2, "always", { "exceptions": ["-", "+", "*"] }],
    'symbol-description': 2, // ES6: Using description promotes easier debugging: when a symbol is logged the description is used:
    'template-curly-spacing': [2, "always"], // ES6: This rule aims to maintain consistency around the spacing inside of template literals.
    'vars-on-top': 2, // allow var definitions anywhere - readability and security have priority over emulating interpreter hoisting
    'max-nesting-depth': 0,
    'selector-max-compound-selectors': 0
  },

  /*
  'globals': [

  ]
  */

}
