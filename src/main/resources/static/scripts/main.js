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
    /**
     * La fonction qui permet de créer dynamiquement la popIn
     */
    function createPopIn(data) {
    	/*
    	 * L'alphabet. 
    	 */
    	var lettreAlphabet = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
    	
    	for(var j = 0; j < 26; j++){
    		
    		var lettreEncours = lettreAlphabet[j];
    		
    		if(data[lettreEncours].length > 0){
    	    	var listMotif = data[lettreEncours];
    	    	var taille = data[lettreEncours].length;
    	    	elmt2 = document.createElement("a");
    	    	elmt2.setAttribute("class", "lettre active");
//    	    	elmt2.setAttribute("onClick","elementClick(this)");
    	    	elmt2.innerHTML=lettreEncours;
    			
    	    	$("#liste-motifs-modal div.alphabet").append(elmt2);
    	    	
    	    	/* Remplissage des libellés*/
    			divModalBody = $("#liste-motifs-modal div.modal-body.liste-motifs");
    			
    	    	ulLettre = document.createElement("ul");
    	    	ulLettre.setAttribute("id", lettreEncours);
    	    	
    	    	liCurrentLettre = document.createElement("li");
    	    	liCurrentLettre.setAttribute("class","lettre");
    	    	
    	    	liCurrentLettre.innerHTML=lettreEncours;
    	    	ulLettre.appendChild(liCurrentLettre);
    	    	
    	    	//---------------- foreach block remplissage des libelle de la lettre courante-------------------------------------//		            	
    	    	for(var i=0; i<taille; i++){
    	    		
    				liLettre = document.createElement("li");
    				liLettre.setAttribute("data-dismiss","modal");
    				liLettre.setAttribute("data-id-motif",listMotif[i].label);
    				liLettre.setAttribute("data-nom-motif",listMotif[i].value);

    				text = listMotif[i].value;
    	        	liLettre.innerHTML=text;
    	        	
    	        	ulLettre.appendChild(liLettre);
    	    	}
    	    	//--------------- Fin block foreach ---------------------------------------//
    	    	
    	    	divModalBody.append(ulLettre);
    	    	
    		}
    		else {
    	    	elt = document.createElement("a");
    	    	elt.setAttribute("class", "lettre");
    	    	elt.innerHTML=lettreEncours;    		
    	    	$("#liste-motifs-modal div.alphabet").append(elt);
    		}
    	}
    }
    
    //afficheListeMotifs
//    $(function (){
//		$.ajax({
//			type: 'GET',
//			url: '/aat/motif/liste/',
//			dataType : 'json',
//            success: function(data) {
//            	$("#liste-motifs-modal div.alphabet").html("");
//            	$("#liste-motifs-modal div.modal-body.liste-motifs").html("");
//            	createPopIn(data);
//            },
//            error: function() {
//	              alert('La requête n\'a pas abouti'); 
//			}
//        });	
//	});
    
    $('.alphabet .lettre.active').click(function () {
//        var position = $('#' + $(this).text()).offset().top - $('.liste-motifs').offset().top + $('.liste-motifs').scrollTop();
//        $('.liste-motifs').animate({scrollTop: position}, 50);

        $('.alphabet .lettre.active').removeClass('selected'),
                $(this).addClass('selected');
    });

    /* $('#motifs-plus-frequents .capsule, #motifs-en-ce-moment .capsule').hover(
            function () {
                $(this).html($(this).data('nom-long-motif'));
            },
            function () {
                $(this).html($(this).data('nom-motif'));
            }
    ); */

    $('#motifs-plus-frequents .capsule, #motifs-en-ce-moment .capsule').click(function () {
    	alert('motifs-plus-frequents');
    });
    
    $('#motifs-en-ce-moment .capsule').click(function () {
    	alert('en ce moment');
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

//    var optionsAutocompleteMotifs = {
//        url: "ressources/motifs.json",
//        getValue: "motif",
//        adjustWidth: false,
//        list: {
//            match: {
//                enabled: true
//            },
//            onChooseEvent: function () {
//                var idMotif = $("#motif-aat-input").getSelectedItemData().id;
//                var nomMotif = $("#motif-aat-input").getSelectedItemData().motif;
//                selectMotif(idMotif, nomMotif);
//            }
//        }
//    };
    
    var codeSelection=null;
    var libelleSelection=null;
    

	
    function autofocus(code, libelle){
    	codeSelection = code;
    	libelleSelection = libelle;
    }
	
    var optionsAutocompleteMotifs = {
            url: function(){
            	return "/aat/motif?param="+ $("#motif-aat-input").val();
            },
            getValue: "value",
            adjustWidth: false,
            minCharNumber: 2,            
            list: {
            	maxNumberOfElements: 10,
            	match: {
                    enabled: false
                },
                
                onShowListEvent: function() {
                	
                	var selectedItem = $("#motif-aat-input").getSelectedItemIndex();
                	var resultList = $("#motif-aat-input").getItems();
                	
                	if (selectedItem == -1 && resultList.length > 0){
                		var selectedData = resultList[0];
                		autofocus(selectedData.label, selectedData.value);
                	}
                },
                
                onChooseEvent: function () {
                    var idMotif = $("#motif-aat-input").getSelectedItemData().label;
                    var nomMotif = $("#motif-aat-input").getSelectedItemData().value;
                    selectMotif(idMotif, nomMotif);
                },
            }
    		
    };

    $("#motif-aat-input").easyAutocomplete(optionsAutocompleteMotifs);
    
    $('#motif-aat-input').on('keypress', function (event) {
    	var isEntry = event.which;
    	if(isEntry == 13){
    		if(codeSelection && libelleSelection){
    			selectMotif(codeSelection, libelleSelection);
    		}
    	}
        
    });
	
//    $("#motif-aat-input").autocomplete({
//
//		source: function(requete, reponse){ // les deux arguments représentent les données nécessaires au plugin
//			$.ajax({
//				type: 'GET',
//				url: '/aat/motif?param='+$('#motif-aat-input').val(),
//				dataType : 'json',
//	            success: function(data) {
//	                reponse($.map(data, function(objet){
//	                    return objet;
//	                }));
//	            },
//	            error: function() {
//		              alert('La requête n\'a pas abouti'); 
//				}
//	        });
//	    },			    		    
//		minLength: 3,
//		delay : 0,
//	    select : function(event,ui){
//	    	
//	    	if(event.keyCode != 9){
//	    		motifSelectionne = document.getElementById('#motif-aat-input').value;
//		    	if(ui.item.value.length > 0){
//					motifSelectionne = ui.item.value;
//					codeLibelle = ui.item.label;
//					ui.item.value = "";
//		    	}
//		    	validationSaisie();
//	    	}
//	    },
//
//		<!-- gestion du no resultat -->
//		response : function(event,ui){
//			var resultSize = ui.content.length;
//			if(resultSize == 0){
//				motifSelectionne = "";
//			}
//		}
//	});
    
    $("#motif-aat-input").keyup(
            function () {
                codeSelection=null;
                libelleSelection=null;
                if ($(this).val().length >= 2) {
                    $('#recherche-button').addClass('focus');
                } else {
                    $('#recherche-button').removeClass('focus');
                }
            }
    );

    function selectMotif(idMotif, nomMotif) {
        if (nomMotif != '') {
            $('#options-recherche').hide();
            $('#validation-recherche, #avis-recherche').fadeIn('slow');
            $('#nom-motif-selectionne').text(nomMotif);
            $('#id-motif-selectionne').val(idMotif);

            if (!idMotif) {
                $('#categorie-motif').show();
            }
            
            setCookie("libelle", nomMotif.replace(/ /g,"_"), 1);
        }
    }

    function resetMotif() {
        $('#options-recherche').fadeIn('slow');
        $('#validation-recherche, #avis-recherche').hide();
        $('#categorie-motif').hide();
        $('#nom-motif-selectionne').text('');
        $('#id-motif-selectionne').val('');
        $('#motif-aat-input').val('');
        $('#recherche-button').removeClass('focus');
        codeSelection=null;
        libelleSelection=null;
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
            var parentId = $(this).parent().attr('id');
            var reponse = $(this).text(); 
            if(parentId === "options-avis-recherche-1"){
            	setCookie("avis-reponse1", reponse, 1);
//            	document.cookie="avis-reponse1=John;expires=Wed, 18 Dec 2023 12:00:00 GMT";
            }else{
            	setCookie("avis-reponse2", reponse, 1);
//            	document.cookie="avis-reponse2=Smith;expires=Wed, 18 Dec 2023 12:00:00 GMT";
            }
        }
    });

    function checkCookie() {
        var username = getCookie("libelle");
        if (username != "") {
            alert("Welcome again " + username);
        } else {
            username = prompt("Please enter your libelle:", "");
            if (username != "" && username != null) {
                setCookie("libelle", username, 365);
            }
        }
    }
    
    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }
    
    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires="+d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }
    
    function finish(element) {
    	alert(getCookie("avis-reponse1") + " " + getCookie("avis-reponse2"));
    	if(!getCookie("avis-reponse1")){
    		return false;
    	} else if (getCookie("avis-reponse2")) {
    		return false;
		} else {
			alert("Je vais envoyer les data");
			 $(element).attr("href", "/remerciements");
		}
    }
    
    $("#avis-envoie").click(function(event) {        
        if(getCookie("avis-reponse1") && getCookie("avis-reponse2")){        	
        	var comment  = $("#avis-commentaire").val();
        	setCookie("avis-commentaire", comment.replace(/ /g,"_"), 1);
        	return true
        } else {
//        	 $('#exemple-modal').modal('hide');
        	confirm("Merci de saisir vos réponse avant l'envoie");
        	 return false;
		}
    });
    
});
