var gutil = require('gulp-util');

const OUTPUT_DIRECTORY = require('./constants').OUTPUT_DIRECTORY;
const getFilename = require('./build-js').getFilename;

var isDevelopment = !!gutil.env.dev;
var isProduction = !isDevelopment;
var uniqueName = isProduction ? Math.floor(Date.now() / 1000) : "";

function buildHtml(gulp) {
    return () => {
        return gulp.src('./index.html')
            .pipe(gulp.dest(OUTPUT_DIRECTORY));
    };
}

function addCachebuster(gulp) {
    return () => {
        const replace = require('gulp-replace');
        const cliArgs = require('yargs').argv;
        const versjon = cliArgs.release || 'NA-'+(''+Math.random()).slice(2);
        var rename = require('gulp-rename');

        return gulp.src(OUTPUT_DIRECTORY+'index.html')
            .pipe(replace('{{timestamp}}', uniqueName))
            .pipe(gulp.dest(OUTPUT_DIRECTORY));
    };
}

module.exports = {
    buildHtml: buildHtml,
    addCachebuster: addCachebuster
};
