/* eslint-disable quote-props */
module.exports = {

  'defaultSeverity': 'warning',
  'extends': 'stylelint-config-sass-guidelines',

  'plugins': [
    'stylelint-selector-bem-pattern',
    'stylelint-scss',
    './config/stylelint/empty-line-between-blocks.js'
  ],

  'rules': {

    /**
    * RULES Info: https://stylelint.io/user-guide/rules/#rules
    */
    'block-closing-brace-newline-after': 'always',
    'block-closing-brace-newline-before': 'always-multi-line',
    'block-closing-brace-space-before': 'always-single-line',
    'block-opening-brace-newline-after': 'always-multi-line',
    'block-opening-brace-space-after': 'always-single-line',
    'color-named': 'never',
    'color-no-invalid-hex': true,
    'declaration-block-no-duplicate-properties': [ true, {
      // Exclude these properties since sometimes they are used as fallback for older browsers
      'ignoreProperties': ['background-color', 'border-color', 'color', 'font-size', 'display']
    }],
    'declaration-block-semicolon-newline-after': 'always-multi-line',
    'declaration-block-single-line-max-declarations': 1,
    'declaration-no-important': true,
    'indentation': [2],
    'max-empty-lines': 5,
    'max-nesting-depth': 5,
    'media-feature-name-no-vendor-prefix': true,
    'no-duplicate-selectors': true,
    'no-eol-whitespace': true,
    'no-missing-end-of-source-newline': true,
    'property-no-vendor-prefix': true,
    'rule-empty-line-before': ['always-multi-line', {
      'except': ['first-nested'],
      'ignore': ['after-comment', 'inside-block']
    }],
    'selector-class-pattern': ['^([a-z][a-z0-9]*)((-|--|__)?[a-z0-9]+)*$', {
      'message': 'Selectors should be written in lowercase. BEM naming convention.'
    }],
    'selector-max-id': 0,
    'selector-no-qualifying-type': [ true, {
      'ignore': ['attribute', 'class']
    }],
    'string-no-newline': true,
    'selector-max-compound-selectors': 4,


    // Disabled Rules
    //--------------
    'block-no-empty': null,
    'color-hex-case': null,
    'media-feature-parentheses-space-inside': null,
    'number-no-trailing-zeros': null,
    'number-leading-zero': null,
    'length-zero-no-unit': null,
    'shorthand-property-no-redundant-values': null,
    'string-quotes': null,


    // Plugins
    //--------------
    'rules/empty-line-between-blocks': true,
    'order/properties-alphabetical-order': null,
    'scss/at-import-partial-extension-blacklist': null, // Disabled since we are using SASS Globbings when importing entire folders
    'scss/dollar-variable-colon-space-after': null,
    'scss/dollar-variable-pattern': null,
    'scss/percent-placeholder-pattern': null,
    'scss/at-mixin-pattern': null,
    'scss/operator-no-unspaced': true,
    'plugin/selector-bem-pattern': {
      'componentName': '(([a-z0-9]+(?!-$)-?)+)',
      'componentSelectors': {
        'initial': "\\.{componentName}(((__|--)(([a-z0-9\\[\\]'=]+(?!-$)-?)+))+)?$"
      },
      'ignoreSelectors': [
        '.*\\.no-js.*',
        '.*\\.js-.*',
        '.*\\.lt-ie.*'
      ],
      'utilitySelectors': '^\\.u-[a-z]+$'
    }
  }
}
