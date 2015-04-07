module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'browserify'],

        files: [
            './src/test/resources/no/nav/sbl/dialogarena/mininnboks/**/*.js'
        ],

        exclude: [],

        preprocessors: {
            './src/test/resources/no/nav/sbl/dialogarena/mininnboks/**/*.js': ['browserify', 'coverage']
        },

        reporters: ['progress', 'junit', 'coverage'],

        colors: true,

        logLevel: config.LOG_INFO,

        browsers: ['PhantomJS'],

        singleRun: true,
        captureTimeout: 60000,

        plugins: [
            'karma-bro',
            'karma-phantomjs-launcher',
            'karma-junit-reporter',
            'karma-coverage',
            'karma-mocha'
        ],

        browserify: {
            debug: true,
            transform: ['reactify']
        },

        coverageReporter: {
            type: 'cobertura',
            dir: './target/karma-coverage'
        },
        junitReporter: {
            outputFile: './target/surefire-reports/TEST-karma.xml',
            suite: ''
        }
    });

};