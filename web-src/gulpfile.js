var gulp = require('gulp');
var browserSync = require('browser-sync');
var $ = require('gulp-load-plugins')();

// Paths
var npm = './node_modules';
var bower = './bower_components';
var src = './src/';
var dist = './dist/';

var symfonyWeb = '../web/aat/';
var designSrc = '../../design/aat/';

/**
 *   Initialisation de BrowserSync et définition du serveur
 *
 *   baseDir: dossier de base (src ou dist)
 *   browser: Navigateur (IE, chrome ...)
 */
function browserSyncInit(baseDir, browser) {
    browser = browser === undefined ? 'default' : browser;

    var server = {
        baseDir: baseDir
    };

    browserSync.instance = browserSync.init({
        startPath: 'PORTAIL/pse/pse_creer.html',
        server: server,
        browser: browser,
        online: true // ne tentera pas de déterminer le statut du réseau, assume que vous êtes en ligne
    });

}


// css task : Création des fichiers css minifiés à partir  du sass
// sass dependancess npm + sass dependances bower +  sass amelipro + sass du projet

gulp.task('styles', function () {

    var mainScssFile = src + 'scss/aat.scss';

    var scssFiles = [
        npm + '/charte-amelipro/src/scss/mixins/variables.scss',
        npm + '/bootstrap-sass/assets/stylesheets/_bootstrap.scss',
        npm + '/bootstrap-select/sass/bootstrap-select.scss',
        npm + '/easy-autocomplete/src/sass/easy-autocomplete.scss',
        npm + '/bootstrap-slider/src/sass/bootstrap-slider.scss',
        bower + '/bootstrap-sass-datepicker/sass/datepicker.scss',
        npm + '/charte-amelipro/src/scss/amelipro.scss'
    ];

    var injectImportOptions = {
        starttag: '/* inject:imports */',
        endtag: '/* endinject */',
        transform: function (filepath) {
            return '@import ".' + filepath + '";';
        }
    };

    gulp.start('get-fonts');
    gulp.start('get-img');


    return gulp.src(mainScssFile)
            .pipe($.sourcemaps.init({includeContent: false, sourceRoot: './css'}))
            .pipe($.inject(gulp.src(scssFiles), injectImportOptions))
            .pipe($.sass().on('error', $.sass.logError))
            .pipe($.autoprefixer('last 2 version'))
            .pipe($.rename('amelipro.min.css'))
            .pipe($.cleanCss())
            .pipe($.sourcemaps.write('/'))
            .pipe(gulp.dest(symfonyWeb + 'css/'))
            .pipe(gulp.dest(src + 'css/'))
            .pipe(browserSync.stream())
            .on('end', function () {
                $.util.log($.util.colors.yellow('La tache CSS est terminee.'));
            });
});


// Génération des styles css pou rles rapport PDF
gulp.task('styles-pdf', function () {

    var mainScssFile = src + 'scss/rapport-pdf.scss';

    return gulp.src(mainScssFile)
            .pipe($.sass().on('error', $.sass.logError))
            .pipe($.autoprefixer('last 2 version'))
            .pipe($.rename('rapport-pdf.css'))
            .pipe(gulp.dest(symfonyWeb + 'css/'))
            .pipe(gulp.dest(src + 'css/'))
            .pipe(browserSync.stream())
            .on('end', function () {
                $.util.log($.util.colors.yellow('La tache CSS-PDF est terminee.'));
            });
});


// javascript task : Compilation des scripts js
// Il faut désactiver les plugins dont on a pas besoin

gulp.task('scripts', function () {

    return gulp.src([
        npm + '/jquery/dist/jquery.js',
        npm + '/bootstrap-select/js/bootstrap-select.js',
        npm + '/easy-autocomplete/dist/jquery.easy-autocomplete.js',
        npm + '/bootstrap-sass/assets/javascripts/bootstrap.js',
        npm + '/jquery/dist/jquery.js',
        npm + '/jquery-ui-dist/jquery-ui.js',
        npm + '/jquery-mask-plugin/src/jquery.mask.js',
        bower + '/bootstrap-sass-datepicker/js/bootstrap-sass-datepicker.js',
        bower + '/bootstrap-sass-datepicker/js/locales/bootstrap-datepicker.fr.js',
        src + '/scripts/*.js'

    ])
            .pipe($.sourcemaps.init({includeContent: false, sourceRoot: './js'}))
            .pipe($.uglify())
            .pipe($.concat('amelipro.min.js'))
            .pipe($.sourcemaps.write('/'))
            .pipe(gulp.dest(symfonyWeb + 'js/'))
            .pipe(gulp.dest(src + 'js/'))
            .pipe(browserSync.stream())
            .on('end', function () {
                $.util.log($.util.colors.yellow('La tache JavaScript est terminee.'));
            });
});


/* Compiler les fichiers html: composer des fichiers html à partir  des partials*/
gulp.task('html', function () {

    //var options = {"indent_size": 4};
    var options = {
        "indent_size": 2,
        "indent_char": " "
    };

    return gulp.src([src + '/html/**/*.*html', '!' + src + '/html/partials/**/*.*'])
            .pipe($.fileInclude({
                prefix: '@@'
            }))
            .pipe($.htmlBeautify(options))
            .pipe(browserSync.stream())
            .pipe(gulp.dest(src));

});


/* Ecouter les modifications sur les fichiers sass et js  */

gulp.task('watch', function () {

    gulp.watch(src + 'scss/**/*.scss', ['styles']);
    gulp.watch(src + 'scripts/**/*.js', ['scripts']);
    gulp.watch(src + 'html/**/*.html', ['html']);

    browserSync.reload();
});


/* Lancer un serveur locale à partir  du dossier src */
gulp.task('serve', ['watch'], function () {

   /* gulp.start('styles');
    gulp.start('scripts');
    gulp.start('html'); */

    browserSyncInit([
        src]);
});


/* Lancer un serveur locale à partir  du dossier dist*/
gulp.task('serve-dist', function () {
    gulp.start('dist');
    browserSyncInit([
        dist]);
});

// default task ==> Compilation du sass et des js
gulp.task('default', function () {
    gulp.start('styles', 'scripts');
});


/* Création d'une nouvelle page à partir  la charte amelipro */
gulp.task('new-page', function () {
    return gulp.src(npm + '/charte-amelipro/src/exemple-page.html*')
            .pipe(gulp.dest(src + 'html/'));
});


/* Récupération d'une copie des fonts disponibles dans la charte amelipro*/
gulp.task('get-fonts', function () {
    return gulp.src(npm + '/charte-amelipro/src/fonts/*')
            .pipe(gulp.dest(symfonyWeb + 'fonts/'))
            .pipe(gulp.dest(src + 'fonts/'));
});

/* Récupération d'une copie des images disponibles dans la charte amelipro*/
gulp.task('get-img', function () {
    return gulp.src(npm + '/charte-amelipro/src/img/*')
            .pipe(gulp.dest(symfonyWeb + 'img/'))
            .pipe(gulp.dest(src + 'img/'));
});


/* Copier les fichiers html vers le dist*/
gulp.task('copy-html', function () {
    return gulp.src([src + '**/*.html', '!' + src + '/html/**/*.*'])
            .pipe(gulp.dest(dist));
});

/* Copier les fonts vers le dist*/
gulp.task('copy-font', function () {
    return gulp.src(src + 'fonts/*')
            .pipe(gulp.dest(dist + 'fonts/'));
});

/* Copier les images vers le dist*/
gulp.task('copy-img', function () {
    return gulp.src(src + 'img/*')
            .pipe(gulp.dest(dist + 'img/'));
});

/* Copier les fichiers js minifiés vers le dist*/
gulp.task('copy-js', function () {
    return gulp.src(src + 'js/*.min.js')
            .pipe(gulp.dest(dist + 'js/'));
});

/* Copier les fichiers css minifiées vers le dist*/
gulp.task('copy-css', function () {
    return gulp.src(src + 'css/*')
            .pipe(gulp.dest(dist + 'css/'));
});

gulp.task('copy-ressources', function () {
    return gulp.src(src + 'ressources/*')
            .pipe(gulp.dest(dist + 'ressources/'));
});



/* Compilation des Styles et scripts puis génération du dist*/
gulp.task('dist', function () {
    gulp.start('styles');
    gulp.start('scripts');
    gulp.start('html');


    gulp.start('copy-font');
    gulp.start('copy-img');
    gulp.start('copy-js');
    gulp.start('copy-css');
    gulp.start('copy-html');
    gulp.start('copy-ressources');
});


/* Copier les assets à partir  du dist vers le dossier web de symmfony */

gulp.task('copy-to-web', function () {

    return gulp.src([src + "js/**.*", src + "fonts/**.*", src + "css/**.*", src + "img/**.*"], {"base": src})
            .pipe(gulp.dest(symfonyWeb));
});


/* Copier le contenu du dossier dist vers le dossier desing du projet */

gulp.task('copy-to-design', function () {

    gulp.start('dist');

    return gulp.src(dist + "**/*.*", {"base": dist})
            .pipe(gulp.dest(designSrc));
});
