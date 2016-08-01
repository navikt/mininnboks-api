const OUTPUT_DIRECTORY = require('./constants').OUTPUT_DIRECTORY;

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

        return gulp.src(OUTPUT_DIRECTORY+'index.html')
            .pipe(replace(/\.(css|js)\?rev=@@versjon/g, '.$1?rev='+versjon))
            .pipe(gulp.dest(OUTPUT_DIRECTORY));
    };
}

module.exports = {
    buildHtml: buildHtml,
    addCachebuster: addCachebuster
};