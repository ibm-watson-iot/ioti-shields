const path = require('path');

// the 'transform-runtime' plugin tells babel to require the runtime instead of inlining it.
const babelRule = {
  test: /\.js$/,
  include: [
    path.resolve(__dirname, '.'),
  ],
  exclude: /(node_modules|bower_components)/,
  resolve: {
    modules: [path.resolve(__dirname, 'src'), 'node_modules/'],
    descriptionFiles: ['package.json'],
    extensions: ['.js', '.ts']
  },
  use: {
    loader: 'babel-loader',
    options: {
      cacheDirectory: true,
      presets: [[
        'env', {
          modules: false,
          targets: {
            node: '8.11.3'
          }
        }]
      ],
      plugins: ['transform-runtime']
    }
  }
};

module.exports = [{
  target: 'node',
  entry: {
    'heart-rate-shield': './src/shields/heart-rate-shield.js',
    'panic-button-shield': './src/shields/panic-button-shield.js'
  },
  optimization: {
    minimize: false
  },
  devtool: 'source-map',
  output: {
    filename: '[name].js',
    library: '[name]',
    libraryTarget: 'commonjs2',
  },
  externals: [
    function(context, request, callback) {
      if (/^.*edge-toolkit$/.test(request)) {
        return callback(null, 'root EdgeToolkit');
      }
      return callback();
    }
  ],
  resolve: {
    modules: [
      path.resolve('./src'),
      path.resolve('./node_modules'),
      path.resolve('./node_modules/@iot4i/sw-edge-processing-toolkit')
    ]
  },
  module: {
    rules: [
      babelRule
    ]
  },
}];
