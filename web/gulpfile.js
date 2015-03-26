var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var karma = require('karma').server;
var notify = require('gulp-notify');

var browserifyTask = function (isDev) {
    console.log('Starting browserify in ' + (isDev ? 'development' : 'production') + ' mode');
    // Our app bundler
    var props = watchify.args;
    props.entries = ['./src/main/resources/no/nav/sbl/dialogarena/mininnboks/index.js'];
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
            .pipe(gulp.dest('./target/classes/no/nav/sbl/dialogarena/mininnboks/build/'));
    }

    bundler.on('update', function () {
        var start = new Date();
        console.log('Rebundling');
        rebundle();
        console.log('Rebundled in ' + (new Date() - start) + 'ms');
    });

    return rebundle();
};


gulp.task('dev', function () {
    browserifyTask(true);
});

gulp.task('default', function () {
    browserifyTask(false);
});
