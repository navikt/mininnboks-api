module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'browserify', 'phantomjs-shim', 'intl-shim'],

        files: [
            './src/main/resources/no/nav/sbl/dialogarena/mininnboks/**/*Test.js'
        ],

        exclude: [],

        preprocessors: {
            './src/main/resources/no/nav/sbl/dialogarena/mininnboks/**/*Test.js': ['browserify', 'coverage']
        },

        reporters: ['progress', 'junit', 'coverage'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        browsers: ['PhantomJS'],
        captureTimeout: 60000,
        singleRun: false,
        autoWatch: true,

        //For å hinde disconnect melding når det bygges
        browserDisconnectTimeout: 10000, //Default 2000
        browserDisconnectTolerance: 1, //Default 0
        browserNoActivityTimeout: 60000, //Default 10000

        plugins: [
            'karma-phantomjs-launcher',
            'karma-phantomjs-shim',
            'karma-browserify',
            'karma-junit-reporter',
            'karma-coverage',
            'browserify-istanbul',
            'karma-mocha',
            'karma-intl-shim'
        ],

        browserify: {
            debug: true,
            transform: [
                ['babelify', { presets: ['es2015', 'react'] }],
                require('browserify-istanbul')({ ignore: ['**/*-spec.js'] })
            ]
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