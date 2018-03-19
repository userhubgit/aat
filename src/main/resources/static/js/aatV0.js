	
		var codeLibelle = null;		
		var motifSelectionne = "";
		var tempsComplet = true;
		var grossessePhatologie = false;
		var focusMotif = "";
		var focusMotifCode = "";
		
		$(document).ready(
			function construireListeCIM10() {
			$.ajax({
				type: 'GET',
				url: '/aatV0/motif/cim10',
				dataType : 'json',
	            success: function(data) {
	            	var taille = data.length;
	            	$('#categorie').append($('<option>',
	           			     {
	           			        value: 0,
	           			        text : "Choisir dans la liste" 
	           			}));
		            	for(i=1; i< taille; i++){
		            		$('#categorie').append($('<option>',
		            			     {
		            			        value: i,
		            			        text : data[i]
		            		}));
		            	}
	            },
	            error: function() {
		              alert('La requête n\'a pas abouti'); 
				}
	        });
		});
		
		<!-- Gestion de l'autocomplétion -->
		$(function(){ 
			$("#pdf").attr("href", "");
			$("#message_ocre").hide();
			document.getElementById("message_ocre").style.display="none";
			document.getElementById("listeCategorie").style.display="none";
			document.getElementById("tableauDurees").style.display="none";
			document.getElementById("blocktableauDurees").style.display="none";
// 			document.getElementById("complOblig").style.display="none";
			document.getElementById('resultat').style.display="none";
			document.getElementById('dureeArret').value="";
			document.getElementById('debutArret').value= new Date().ddmmyyyy();
			document.getElementById('dateSortie').value= new Date().ddmmyyyy();
			
			/*
			* Gestion de la touche entrée
			*/
			$("#champMotif").keypress(function(e) {
				if(e.keyCode == 13)
				{
				   e.preventDefault();
				   validationSaisie();
				   $(this).autocomplete('close');
				}
			});
			
			/*
			* Gestion de la touche TAB
			*/			
			$("#champMotif").keydown(function(e) {
				var code = (e || window.event).keyCode;
				if(code == 9)
				{
// 					e.preventDefault();
// 					validationSaisie();
// 					$(this).autocomplete('close');					
				}
			});
			
			$.ui.autocomplete.prototype._renderItem = function (ul, item) {
				item.propDuree = item.value;
				item.propDuree = item.propDuree.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(this.term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
				return $("<li></li>")
					.data("item.autocomplete", item)
					.append("<a>" + item.propDuree + "</a>")
					.appendTo(ul);
			};		
			$("#champMotif").autocomplete({

				source: function(requete, reponse){ // les deux arguments représentent les données nécessaires au plugin
					$.ajax({
						type: 'GET',
						url: '/aatV0/motif?param='+$('#champMotif').val(),
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
// 			    focus: function( event, ui) {
// 			    	focusMotif = ui.item.value;
// 			    	focusMotifCode = ui.item.label;
// 			    },			    
				minLength: 3,
				delay : 300,
			    select : function(event,ui){
			    	
			    	if(event.keyCode != 9){
			    		motifSelectionne = document.getElementById('champMotif').value;
				    	if(ui.item.value.length > 0){
							motifSelectionne = ui.item.value;
							codeLibelle = ui.item.label;
							ui.item.value = "";
				    	}
				    	validationSaisie();
			    	}
			    },

				<!-- gestion du no resultat -->
				response : function(event,ui){
					var resultSize = ui.content.length;
					if(resultSize == 0){
						motifSelectionne = "";
					}
				}
			});
		});	
		/**
		* Validation du champ saisie.
		*/
		function validationSaisie(){
			
			/** Gestion du bouton TAB*/
// 			if (focusMotif === document.getElementById('champMotif').value
// 					&& motifSelectionne =="") // Saisie libre motif connu.
// 			{
// 				motifSelectionne = focusMotif;
// 				codeLibelle = focusMotifCode;
// 			}
			
			if(motifSelectionne==""){
				if (document.getElementById('champMotif').value.length < 3) {
					$("#motifSelectionne").html("Veuillez saisir au moins 3 caractères");
					$("#motifSelectionne").show();
				} else {
					$("#motifSelectionne").html(document.getElementById('champMotif').value);
					//gestion saisi libre
					gererSaisieLibre();
					document.getElementById("categorie").focus();
//	 				Event.observe(obj,'change', choisirCategorie());
					fireEvent(document.getElementById("categorie"),'click');
					
// 					var evt = document.createEvent("MouseEvents");
// 					evt.initMouseEvent("click", true, true, window,0, 0, 0, 0, 0, false, false, false, false, 0, null);
// 					document.getElementById("categorie").dispatchEvent(evt);
				}
				activerZonePeriodeArret();
			}else{
				$("#champTexteLong").html('');
				$("#motifSelectionne").html(motifSelectionne);
				document.getElementById("champTexteLong").focus();
				afficherTableauDuree();
				activerZonePeriodeArret();
			}
			// 1 - Reset du champ de saisi
			document.getElementById('champMotif').value="";
			document.getElementById('champMotif').title="";
			inputPlaceholder(document.getElementById('champMotif'),"gray");

			//	remplacement champ de sasi par le label.
			$("#champSaisie").hide();
			$("#idListeMotif").hide();
			$("#apres").show();
			document.getElementById('resultat').style.display="inline-block";
		}
		
		function activerZonePeriodeArret() {
			document.getElementById("resultatDuree").setAttribute("class", "resultatDuree");
			document.getElementById("debutArret").disabled=false;
			document.getElementById("finArret").disabled=false;
			document.getElementById("dureeArret").disabled=false;
		}
		
		function desactiverZonePeriodeArret() {
			document.getElementById("resultatDuree").setAttribute("class", "resultatDuree inactif");
			document.getElementById("debutArret").disabled=true;
			document.getElementById("finArret").disabled=true;
			document.getElementById("dureeArret").disabled=true;
		}
		
		function afficherSaisieCompl() {
			//On cache le crayon
			document.getElementById("crayonCompl").style.display="none";
			// On affiche la zone de saisie complement d'information
			document.getElementById("saisiComplInfo").style.display="inline-block";
			document.getElementById("champTexteLong").style.display="block";
		}
		
		function fireEvent(element,event){
		    if (document.createEventObject){
		    // dispatch for IE
		    var evt = document.createEventObject();
		    return element.fireEvent('on'+event,evt)
		    }
		    else{
		    // dispatch for firefox + others
		    var evt = document.createEvent("HTMLEvents");
		    evt.initEvent(event, true, true ); // event type,bubbling,cancelable
		    return !element.dispatchEvent(evt);
		    }
		}
		
		/**
		*
		*/
		function afficherTableauDuree() {
			if (tempsComplet && !grossessePhatologie) {
				$.ajax({
					type: 'GET',
					url: '/RESTFullMotif/rest/motif/htm/'+codeLibelle,
					dataType : 'html',
		            success: function(data) {
		            	if(data && data.length > 0){
		            		document.getElementById('blocktableauDurees').style.display="block";	
		            		$("#tableauDurees").html(data);
		           			$("#tableauDurees").show();
		           			$("#pdf").attr("href", "/RESTFullMotif/rest/motif/pdf/"+codeLibelle);
		           		}
		            },
		            error: function() {
			              //alert('La requête n\'a pas abouti'); 
					}
		        });	
			}			
		}
		
		/**
		 * Cacher le tableau de durées indicatives
		 */
		function cacherTableauDuree() {
			document.getElementById('blocktableauDurees').style.display="block";
		}
		
		function onClicktableauDuree(radio){
			document.getElementById('dureeArret').value=radio.value;
			calculerDateFin(radio.value);
		}
	   
	   /**
		*
		*/
		Date.prototype.mmddyyyy = function() {
			   var yyyy = this.getFullYear();
			   var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); 
			   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
			   return "".concat(mm).concat("/").concat(dd).concat("/").concat(yyyy);
		};
		
		/**
		*
		*/
		Date.prototype.ddmmyyyy = function() {
			   var yyyy = this.getFullYear();
			   var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); 
			   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
			   return "".concat(dd).concat("/").concat(mm).concat("/").concat(yyyy);
		};
				
		<!-- gestion de la saisie libre -->
		function gererSaisieLibre(){
			// On cache le tableau des durées
			document.getElementById("tableauDurees").style.display="none";
			// On enleve la durée à vide
			document.getElementById('dureeArret').value="";
			document.getElementById('finArret').value="";
			
			//affichage du block catégorie
			if(motifSelectionne != document.getElementById('champMotif').value){
				$("#message_ocre").show();
			}
			document.getElementById('categorie').selectedIndex = 0;
			document.getElementById('listeCategorie').style.display="block";
			
			//
			$("#motifSelectionne").html(document.getElementById('champMotif').value);
		}
		
		<!-- gestion du choix d'une catégorie ou CIM10  -->
		function choisirCategorie(){
			var categorie = document.getElementById('categorie').value;
		}
		
		
		<!--Ajouter des jours à une date -->
		function calculerDateFin(duree){
			var dateFin = new Date();
			dateFin.setDate(dateFin.getDate()+parseInt(duree));
			var day = dateFin.getDate();
			var month = dateFin.getMonth() + 1;
			var year = dateFin.getFullYear();
			// format du jour.
			if(day<10){ 
				day = '0'+ day;
			}
			//format du mois.
			if(month<10){ 
				month = '0'+ month;
			}
			document.getElementById('finArret').value= day + "/" + month+"/"+year;
			return dateFin;
		}
				
		<!-- Actions avec le bouton Reset du champ -->		
		function reInitChamp() {
			motifSelectionne = "";
			focusMotif = "";
			codeLibelle = null;
			document.getElementById("listeCategorie").style.display="none";
			document.getElementById("tableauDurees").style.display="none";
			document.getElementById("message_ocre").style.display="none";
			document.getElementById("blocktableauDurees").style.display="none";
			document.getElementById('resultat').style.display="none";
			document.getElementById("saisiComplInfo").style.display="none";
			document.getElementById("crayonCompl").style.display="inline-block";
			document.getElementById('dureeArret').value="";
			document.getElementById('finArret').value="";
			$("#motifSelectionne").html('');
			$("#champTexteLong").val('');
			document.getElementById('champMotif').value="";
			document.getElementById('champMotif').title="";
			afficherZoneSaisieMotif();
			initEltOrdreMedical();

			if (navigator.userAgent.indexOf('MSIE') != -1){
				inputPlaceholder(document.getElementById('champMotif'));
				document.getElementById('champMotif').style.color='#888'
			}
			
			// affichage du champ de saisie
			$("#champSaisie").show();
			$("#apres").hide();
			$("#motifSelectionne").show();
			$("#idListeMotif").show();
			desactiverZonePeriodeArret();
		}
		
		<!-- Action sur le lien "Liste des motifs" -->
		function afficheListeMotifs(){
			$.ajax({
				type: 'GET',
				url: '/aatV0/motif/liste/',
				dataType : 'json',
	            success: function(data) {
	            	document.getElementById('blocMotifs').innerHTML = "";
	            	document.getElementById('alphabet').innerHTML = "";
	            	createPopIn(data);
	            },
	            error: function() {
		              alert('La requête n\'a pas abouti'); 
				}
	        });
			
			document.getElementById("popIn_listeMotifs").style.display="block";			
		}
		
		<!-- Action sur le bouton croix de la popIn -->
		function cacheListeMotifs(){ 
			document.getElementById("popIn_listeMotifs").style.display="none";
		}			
		
		<!-- Paramétrages du scrolling dans la liste des motifs -->
		jQuery(function( $ ){ 
			$.localScroll.defaults.axis = 'y';
			$.localScroll({
				target:'#blocMotifs',
				queue:true,
				duration:1200,
				hash:true
			});
		});			
		
		<!-- Actions sur la sélection d'un motif dans la liste -->
		function selectionMotif(valeur){ 
			var motif = valeur.textContent;
			var maxLength = 49;
			if(motif.length > maxLength){
				document.getElementById('champMotif').title=motif;
				if (navigator.userAgent.indexOf('MSIE') != -1){					
					motif=motif.substr(0,maxLength);				
					motif=motif + " ..."
				}
			}
			motifSelectionne=motif;
			codeLibelle = valeur.id;
			validationSaisie();
// 			document.getElementById('champMotif').value=motif;
// 			document.getElementById('champMotif').style.color='#000'
			cacheListeMotifs();
			document.getElementById("listeCategorie").style.display="none";
		}
		
	   	/**
		* Choix de la nature de l'arrêt.
		*/
		function choisirNatureArret(natureArret) {

			if (natureArret.value == "complet") {
				tempsComplet = true;
				document.getElementById("divGrossesse").style.display="block";
				document.getElementById("titreDuree").innerHTML = "durée de l'arrêt";
				document.getElementById("titreMotif").innerHTML = "Motif de l'arrêt de travail"+'<span class="obligatoire"></span>:</label>';
				if (codeLibelle != null) {
					document.getElementById('blocktableauDurees').style.display="block";	
				}
				document.getElementById("blocModalitePartiel").style.display="none";
				document.getElementById("blocModaliteComplet").style.display="block";
				reInitChamp();
			} else {
				tempsComplet = false;
				grossessePhatologie = false;
				document.getElementById("divGrossesse").style.display="none";
				document.getElementById("titreDuree").innerHTML = "durée de la reprise à temps partiel";
				document.getElementById("titreMotif").innerHTML = "Motif de la reprise"+'<span class="obligatoire"></span>:</label>';
				document.getElementById('blocktableauDurees').style.display="none";
				document.getElementById("blocModalitePartiel").style.display="block";
				document.getElementById("blocModaliteComplet").style.display="none";
				reInitChamp();
			}
		}
	   	
	   /*
	   	* FIX pour l'evenement onchange sur IE8
	   	*/
		function radioClick()
		{
		 	if (navigator.userAgent.indexOf('MSIE') != -1){
				this.blur();  
		 		this.focus();  
			}  
		}
	   
	   /**
	   	*
	   	*/
	    function changerRapportGrossesse() {
	    	var test = document.getElementById("rapportGrossesse").innerHTML.toLowerCase()=="L'arrêt <span>n'est pas en rapport</span> avec un état pathologique résultant de la grossesse".toLowerCase();
	    	if (test) {
	    		grossessePhatologie = true;
	    		document.getElementById("rapportGrossesse").innerHTML=
	    			"L'arrêt <span><b>est en rapport</b></span> avec un état pathologique résultant de la grossesse";
		    		
			} else {
				grossessePhatologie = false;
				document.getElementById("rapportGrossesse").innerHTML=
	    			"L'arrêt <span>n'est pas en rapport</span> avec un état pathologique résultant de la grossesse";
			}
	    	if(grossessePhatologie){
	    		cacherZoneSaisieMotif();
	    		//Cacher la zone durée indicative.
	    		document.getElementById("blocktableauDurees").style.display="none";
	    		
	    	}else {
	    		afficherZoneSaisieMotif();
	    		//Afficher la zone durée indicative Si possible.
	    		if (codeLibelle != null && tempsComplet) {
	    			document.getElementById("blocktableauDurees").style.display="block";					
				}
			}
		}
	   
	   /**
	   	*
	   	*/
	    function changerRapportAffection() {
	    	var test = document.getElementById("rapportAffection").innerHTML.toLowerCase()=="L'arrêt <span>n'est pas en rapport</span> avec une affection L324-1 et R613-69 du code de la sécurité sociale".toLowerCase();
	    	if (test) {
	    		document.getElementById("rapportAffection").innerHTML=
	    			"L'arrêt <span><b>est en rapport</b></span> avec une affection L324-1 et R613-69 du code de la sécurité sociale";
		    		
			} else {
				document.getElementById("rapportAffection").innerHTML=
	    			"L'arrêt <span>n'est pas en rapport</span> avec une affection L324-1 et R613-69 du code de la sécurité sociale";
			}
	    	
	    	if(grossessePhatologie){
	    		cacherZoneSaisieMotif();
	    		//Cacher la zone durée indicative.
	    		document.getElementById("blocktableauDurees").style.display="none";
	    		
	    	}else {
	    		afficherZoneSaisieMotif();
	    		//Afficher la zone durée indicative Si possible.
	    		if (codeLibelle != null && tempsComplet) {
	    			document.getElementById("blocktableauDurees").style.display="block";					
				}
			}
		}
	    
	    /**
	     * Cacher la zone de saisie du motif.
	     */
	    function cacherZoneSaisieMotif() {
	    	document.getElementById("divSaisieMotif").style.display="none";
		}
	    
	    /**
	     * Cacher la zone de saisie du motif.
	     */
	    function afficherZoneSaisieMotif() {
	    	document.getElementById("divSaisieMotif").style.display="block";
		}
	    
	    function initEltOrdreMedical() {
	    	document.getElementById("rapportAffection").innerHTML=
    			"L'arrêt <span>n'est pas en rapport</span> avec une affection L324-1 et R613-69 du code de la sécurité sociale";
	    	document.getElementById("rapportGrossesse").innerHTML=
    			"L'arrêt <span>n'est pas en rapport</span> avec un état pathologique résultant de la grossesse";
		}