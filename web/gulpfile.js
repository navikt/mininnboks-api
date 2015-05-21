var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
//var karma = require('karma').server;
var notify = require('gulp-notify');
var less = require('gulp-less');
var concat = require('gulp-concat');
var gulpif = require('gulp-if');
var uglifycss = require('gulp-uglifycss');
var uglify = require('gulp-uglify');
var streamify = require('gulp-streamify');

var SRC_DIR = './src/main/resources/no/nav/sbl/dialogarena/mininnboks/';
var BUILD_DIR = './src/main/webapp/build/';
var MODIG_FRONTEND = './node_modules/modig-frontend/modig-frontend-ressurser/src/main/resources/';

function browserifyTask(isDev) {
    console.log('Starting browserify in ' + (isDev ? 'development' : 'production') + ' mode. NODE_ENV: ' + process.env.NODE_ENV);
    // Our app bundler
    var props = watchify.args;
    props.entries = [
        MODIG_FRONTEND + 'js/modig/AjaxLoader.js',
        MODIG_FRONTEND + 'js/modig/dropdown.js',
        MODIG_FRONTEND + 'js/modig/felles.js',
        MODIG_FRONTEND + 'js/modig/tooltip.js',
        MODIG_FRONTEND + 'js/modig/transitions.js',
        SRC_DIR + 'index.js'
    ];
    props.debug = isDev;
    props.cache = {};
    props.packageCache = {};
    props.fullPaths = isDev;

    var bundler = isDev ? watchify(browserify(props)) : browserify(props);
    bundler.transform(reactify);

    function rebundle() {
        var stream = bundler.bundle();
        return stream.on('error', notify.onError({
            title: 'Compile error',
            message: '<%= error.message %>'
        }))
            .pipe(source('mininnboks.js'))
            .pipe(gulpif(!isDev, streamify(uglify())))
            .pipe(gulp.dest(BUILD_DIR + 'js'));
    }

    bundler.on('update', function () {
        var start = new Date();
        console.log('Rebundling');
        rebundle();
        console.log('Rebundled in ' + (new Date() - start) + 'ms');
    });

    return rebundle();
}

function copyImg() {
    console.log('Copying images');
    return gulp.src(MODIG_FRONTEND + 'META-INF/resources/img/**/*')
        .pipe(gulp.dest(BUILD_DIR + 'img'));
}

var buildLess = function(isDev) {
    console.log('Building less');
    return gulp.src(SRC_DIR + '**/*.less')
        .pipe(less())
        .pipe(concat('bundle.css'))
        .pipe(gulpif(!isDev, uglifycss()))
        .pipe(gulp.dest(BUILD_DIR + 'css'));

};

gulp.task('default', function () {
    process.env.NODE_ENV = 'production';

    browserifyTask(false);
    buildLess(false);
    copyImg();
});

gulp.task('dev', function() {
    process.env.NODE_ENV = 'development';

    buildLess(true);
    copyImg();
    browserifyTask(true);
    gulp.watch(SRC_DIR + '**/*.less', buildLess.bind(this, true));
});

gulp.task('test', function () {
    //karma.start({
    //    configFile: __dirname + '/karma.conf.js',
    //    isSingleRun: true
    //});
});