const path = require('path');
module.exports = {
  settings: {
    'import/resolver': {
      node: {
        paths: [path.resolve(__dirname, './src/'), path.resolve(__dirname, './node_modules/@iot4i/sw-edge-processing-toolkit')]
      }
    }
  },
  root: true,
  parserOptions: {
    ecmaVersion: 2017,
    sourceType: 'module'
  },
  env: {
    "es6": true,
    mocha: true,
    browser: false
  },
  "extends": ["airbnb-base", "plugin:node/recommended"],
  "plugins": [
    "import",
    "json",
    "node"
  ],
  "rules": {
    "import/extensions": "off",
    "node/no-unsupported-features": "off",
    "arrow-body-style": "off",
    "comma-dangle": "off",
    "func-names": "off",
    "global-require": "off",
    "keyword-spacing": ["error"],
    "no-console": "off",
    "no-param-reassign": "off",
    "no-plusplus": ["error", {
      "allowForLoopAfterthoughts": true
    }],
    "no-underscore-dangle": "off",
    "no-unused-vars": ["error", {
      "args": "none"
    }],
    "no-use-before-define": ["error", {
      "functions": false
    }],
    "max-len": ["error", 120],
    "object-shorthand": "off",
    "prefer-arrow-callback": "off",
    "prefer-rest-params": "off",
    "spaced-comment": "off",
    "space-before-function-paren": ["error", "never"],
    "strict": "error",
    "vars-on-top": "off",

    "import/no-dynamic-require": "off",
    "import/no-extraneous-dependencies": "off",
    "node/no-unpublished-require": "off",

    "prefer-template": "off",
    "prefer-spread": "off",
    "space-in-parens": ["error", "never"],
    "space-before-blocks": ["error", "always"],
    "padded-blocks": "off",
    "no-else-return": "off",
    "no-empty": "off",
    "prefer-const": ["error", {
      "destructuring": "all"
    }],
    "class-methods-use-this": "off"
  }
};
