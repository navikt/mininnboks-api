var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var karma = require('karma').server;
var notify = require('gulp-notify');
var less = require('gulp-less');
var concat = require('gulp-concat');

var SRC_DIR = './src/main/resources/no/nav/sbl/dialogarena/mininnboks/';
var BUILD_DIR = './src/main/webapp/build/';
var MODIG_FRONTEND = './node_modules/modig-frontend/modig-frontend-ressurser/src/main/';

function browserifyTask(isDev) {
    console.log('Starting browserify in ' + (isDev ? 'development' : 'production') + ' mode');
    // Our app bundler
    var props = watchify.args;
    props.entries = [SRC_DIR + 'index.js'];
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
    return gulp.src(MODIG_FRONTEND + 'resources/META-INF/resources/img/**/*')
        .pipe(gulp.dest(BUILD_DIR + 'img'));
}

function buildLess() {
    return gulp.src(SRC_DIR + '**/*.less')
        .pipe(less())
        .pipe(concat('bundle.css'))
        .pipe(gulp.dest(BUILD_DIR + 'css'));

}

gulp.task('default', function () {
    browserifyTask(false);
    buildLess();
    copyImg();
});
