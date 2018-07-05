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
        
        // alimentation cookie
        var idValue = $(this).attr('id');
        
        if(idValue === "smiles-positif"){        	
        	$('#resultat-recherche').val("OUI");
        }
        
        if(idValue === "smiles-negatif"){        	
        	$('#resultat-recherche').val("NON");
        }
        
        if(idValue === "smiles-passif"){        	
        	$('#resultat-recherche').val("MOYEN");
        }
        
        
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
        $('#clic-liste-complete').val("OUI");
    });
    
    $('#recherche-commentaire').on('keyup change', function () {
    	 $('#recherche-commentaire').val($(this).val());
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
    
    $('#motifs-en-ce-moment .capsule').click(function () {    	    	
    	// ALIMENTATION PARAM REQUEST
    	$('#clic-en-ce-moment').val("OUI");
    	$('#motif-origine').val("EN_CE_MOMENT");
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
        $('#motif-complement').val($(this).val());
    });
    

       
    // ALIMENTATION PARAM REQUEST
    $('#motif-origine').val("LISTE_COMPLETE");
    $('#clic-en-ce-moment').val("NON");
    $('#clic-liste-complete').val("NON");
    $('#resultat-recherche').val("");
    
    var codeSelection=null;
    var libelleSelection=null;
    
	
    function autofocus(code, libelle){
    	codeSelection = code;
    	libelleSelection = libelle;
    };
    
//    function caratereValide(txt){
//    	
//    	// ?./§*%$£¤}]=+@^&#"~"'{([-|_\ 
//    	
//    	if(!txt.match(/[A-Za-z0-9àâäéèêëîôöùûïÄÀÂÇÉÈÊÔÖÎÏÛÙ -']/)) 
//        {  
//    	   console.log(txt);
//    	   var texteValide = txt.replace(/[A-Za-z0-9àâäéèêëîôöùûïÄÀÂÇÉÈÊÔÖÎÏÛÙ -']/g,'');
//    	       		   
//    		for (var i = 0; i < txt.length; i++) {
//				if (txt.indexOf('?') > -1){
//					txt.replace('?','');
//				}
//			}
//      	   return txt;
//        } 	
//    };
	
    var optionsAutocompleteMotifs = {
            url: function(){
            	
            	return "/aat/motif?param="+ $("#motif-aat-input").val();
            },
            getValue: "value",
            adjustWidth: false,
            minCharNumber: 2,
            preparePostData: function(data, inputPhrase) {
            	data = [];
            	return data;
            },
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
                    
                	$('#motif-origine').val("SAISIE");
                },
            }
    		
    };

//    $("#motif-aat-input").easyAutocomplete(optionsAutocompleteMotifs);
     
	$.ui.autocomplete.prototype._renderItem = function (ul, item) {
		item.propDuree = item.value;
		item.propDuree = item.propDuree.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(this.term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
		return $("<li></li>")
			.data("item.autocomplete", item)
			.append("<a>" + item.propDuree + "</a>")
			.appendTo(ul);
	};
	
    /**
     * Plugin de gestion d'autocomplétion.
     */
     $("#motif-aat-input").autocomplete({
    
    		source: 
    			function(requete, reponse){
    			$.ajax({
    				type: 'GET',
    				url: '/aat/motif?param='+$("#motif-aat-input").val(),
    				dataType : 'json',
    	            success: function(data) {
    	                reponse($.map(data, function(objet){
    	                    return objet;
    	                }));
    	            },
    	            error: function() {
    		              alert('La requête n\'a pas abouti'); 
    				}
    	        });
    	    },	    		    
    		minLength: 2,
    		delay : 0,
    	    select : function(event,ui){
    	    	
    	    	if(event.keyCode != 9){
    	    		libelleSelection = $(this).val();
    		    	if(ui.item.value.length > 0){
    		    		libelleSelection = ui.item.value;
    		    		codeSelection = ui.item.label;
    					ui.item.value = "";
    		    	}
    		    	selectMotif(codeSelection, libelleSelection);
    	    	}
    	    },
    		response : function(event,ui){
    			var resultSize = ui.content.length;
    			if(resultSize == 0){
    				libelleSelection = "";
    			}
    		}
    	});
    
    $('#motif-aat-input').keyup(function (e) {
    	
    	var txt = e.key;
    	if(!txt.match(/[A-Za-z0-9àâäéèêëîôöùûïÄÀÂÇÉÈÊÔÖÎÏÛÙ -']/)) 
        {
        	e.preventDefault();
        }
    	
        if ($(this).val().length >= 2) {
            $('#recherche-button').addClass('focus');
        } else {
            $('#recherche-button').removeClass('focus');
        }
        
        if (e.keyCode == 13) {
        	if ($(this).val().length >= 2) {
        		
        		if(codeSelection && libelleSelection){
        			selectMotif(codeSelection, libelleSelection)
        		}else {    			 
        			selectMotif(false, $('#motif-aat-input').val());
        		}
        		$('#motif-origine').val("SAISIE");
        		return false;
        	} else {
        		return false;
        	}
        }
    });
    
    $('#motif-aat-input').keypress(function (e) {
    	
    	var txt = e.key;
    	if(!txt.match(/[A-Za-z0-9àâäéèêëîôöùûïÄÀÂÇÉÈÊÔÖÎÏÛÙ -']/)) 
        {
        	e.preventDefault();
        }
        if ($(this).val().length >= 2) {
            $('#recherche-button').addClass('focus');
        } else {
            $('#recherche-button').removeClass('focus');
        }
        
        if (e.keyCode == 13) {
        	if ($(this).val().length >= 2) {
        		
        		if(codeSelection && libelleSelection){
        			selectMotif(codeSelection, libelleSelection)
        		}else {    			 
        			selectMotif(false, $('#motif-aat-input').val());
        		}
        		$('#motif-origine').val("SAISIE");
        		return false;
        	} else {
        		return false;
        	}
        }
    });    
    
    function selectMotif(idMotif, nomMotif) {
        if (nomMotif != '') {
            $('#options-recherche').hide();
            $('#validation-recherche, #avis-recherche').fadeIn('slow');
            $('#nom-motif-selectionne').text(nomMotif);
            $('#id-motif-selectionne').val(idMotif);

            if (!idMotif) {
            	$('#motif-origine').val("SAISIE");
                $('#categorie-motif').show();
            }
            // ALIMENTATION PARAM REQUEST
            $('#libelle').val(nomMotif);
        }
    }

    
    function resetMotif() {
        $('#options-recherche').fadeIn('slow');
        $('#validation-recherche, #avis-recherche').hide();
        $('#categorie-motif').hide();
        $('#nom-motif-selectionne').text('');
        $('#id-motif-selectionne').val('');
        $('#motif-aat-input').val('');
        $('#complement-info-motif').val('');
        document.getElementsByClassName("filter-option pull-left")[0].innerHTML="-- Catégorie de pathologies --";
        $('#recherche-button').removeClass('focus');        
        
        codeSelection=null;
        libelleSelection=null;
        
        
        $('#motif-origine').val("LISTE_COMPLETE");
        $('#clic-en-ce-moment').val("NON");
        $('#clic-liste-complete').val("NON");
    }

    $('#recherche-button').click(function (e) {
        selectMotif(false, $('#motif-aat-input').val());
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
            	$('#avis-reponse1').val(reponse);
            }else{
            	$('#avis-reponse2').val(reponse);
            }
        }
    });

 
//    $("#someButton").click(function() {
//    	$("#myForm").submit();
//    });

    $("#avis-envoie").click(function(event) {        
        if($('#avis-reponse1').val() && $('#avis-reponse2').val()){        	
        	var comment  = $("#avis-commentaire").val();
        	$('#avis-commentaire').val(comment);
        	return true
        } else {
        	 $('#exemple-modal').modal('show');
        	 return false;
		}
    });
});
