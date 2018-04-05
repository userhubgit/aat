$(document).ready(function () {

    // Scripts charte-amelipro
    // Initialise les popover
    $('[data-toggle="popover"]').popover();

    // Initialiser la personnalisation des selects
    $('select').selectpicker();

    // Initialise les input radio stylés
    $('.checkbox, .radio').click(function () {
        $(this).find('label').removeClass('active');
        $(this).find('input:checked').parent('label').addClass('active');
    });
    $('.checkbox, .radio').each(function () {
        $(this).find('label').removeClass('active');
        $(this).find('input:checked').parent('label').addClass('active');
    });

    // Initialisation du menu
    $('.btn-menu, .overlay').click(
            function () {
                if ($('#main-menu').hasClass('open')) {
                    $('#main-menu, .overlay').removeClass('open');
                } else {
                    $('#main-menu, .overlay').addClass('open');
                }
            }
    );

    // Initialisation des datesPicker
    $('.datepicker, .input-group.date').attr({'readonly': 'readonly'}).datepicker({todayHighlight: true, clearBtn: true, language: "fr", autoclose: true});


    $(document).keyup(function (e) {
        if (e.keyCode == 27) { // escape key maps to keycode `27`
            $('#main-menu, .overlay').removeClass('open');
        }
    });

    // show-hide-btn
    $('.show-hide-btn').each(function () {
        $('.show-hide-btn').find('span').addClass($(this).attr('data-hide-icon'));
    });

    $('.show-hide-btn').click(function () {
        $(this).find('span').addClass($(this).attr('data-show-icon'));
    });

    $('.show-hide').click(function () {

        var toHide = $(this).attr('data-hide');
        var toShow = $(this).attr('data-show');
        var reverseShowHide = $(this).attr('data-reverse-show-hide');
        if (toHide != '') {
            $(toHide).hide();
            $(this).find(".open-close").removeClass('active');
        }

        if (toShow != '') {
            $(toShow).removeClass('hidden');
            $(toShow).fadeOut().fadeIn();
            $(this).find(".open-close").addClass('active');
        }

        if (reverseShowHide) {
            $(this).attr('data-hide', toShow);
            $(this).attr('data-show', toHide);
        }

        $('#options-maquette li').removeClass('active');
        $(this).addClass('active');

    });

    // Cacher le loader ajax après le chargement de la page

    $('.ajax-loader').hide();

    /*$('.panel-heading').has('[data-toggle=collapse]').addClass('bg-gris-6');*/

    $('.panel-heading[data-toggle=collapse]').click(function () {
        $('.panel-heading').removeClass('active');
        $(this).addClass('active');
    });


    /****************************** AAT *********************************/

    $('.alphabet .lettre.active').click(function () {
        var position = $('#' + $(this).text()).offset().top - $('.liste-motifs').offset().top + $('.liste-motifs').scrollTop();
        $('.liste-motifs').animate({scrollTop: position}, 50);
        
        $('.alphabet .lettre.active').removeClass('selected'),
        $(this).addClass('selected');
    });

    $('#complement-info-motif').on('keyup change', function () {
        var text = $(this).val();
        max = 500;
        textLength = text.length;

        if (textLength < max) {
            $($(this).attr('data-compteur')).html((textLength + '/' + max));
        } else {
            $(this).val(text.substring(0, max));
            $($(this).attr('data-compteur')).html((max + '/' + max));
        }
    });

    var optionsAutocompleteMotifs = {
        url: "ressources/motifs.json",
        getValue: "motif",
        adjustWidth: false,
        list: {
            match: {
                enabled: true
            },
            onChooseEvent: function () {
                var idMotif = $("#motif-aat-input").getSelectedItemData().id;
                var nomMotif = $("#motif-aat-input").getSelectedItemData().motif;
                selectMotif(idMotif, nomMotif);
            }
        }
    };

    $("#motif-aat-input").easyAutocomplete(optionsAutocompleteMotifs);

    function selectMotif(idMotif, nomMotif) {
        if (nomMotif != '') {
            $('#options-recherche').hide();
            $('#validation-recherche, #avis-recherche').fadeIn('slow');
            $('#nom-motif-selectionne').text(nomMotif);
            $('#id-motif-selectionne').val(idMotif);

            if (!idMotif) {
                $('#categorie-motif').show();
            }
        }
    }

    function resetMotif() {
        $('#options-recherche').fadeIn('slow');
        $('#validation-recherche, #avis-recherche').hide();
        $('#categorie-motif').hide();
        $('#nom-motif-selectionne').text('');
        $('#id-motif-selectionne').val('');
        $('#motif-aat-input').val('');
    }

    $('#recherche-button').click(function () {
        selectMotif(false, $('#motif-aat-input').val());
    });

    $('#motif-aat-input').keyup(function (e) {
        if (e.keyCode == 13) {
            selectMotif(false, $('#motif-aat-input').val());
            return false;
        }
    });

    $('[data-nom-motif]').click(function () {
        selectMotif($(this).data('id-motif'), $(this).data('nom-motif'));
    });

    $('#reset-motif, #btn-nuovelle-recherche').click(function () {
        resetMotif();
    });

    $('.capsule').click(function () {
        var parent = $(this).data('parent');
        if (parent) {
            $($(this).data('parent') + ' .capsule').removeClass('active');
            $(this).addClass('active');
        }
    });

});
