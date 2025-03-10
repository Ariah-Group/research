/* 
 * Copyright 2015-2016 The Ariah Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var propCoordId = "document.developmentProposalList[0].proposalCoordinatorPrincipalName".replace(/(:|\.|\[|\])/g, '\\$1');
var relativeUrl = "ariah/proposalCoordinatorsForUnit/";
var $j = jQuery.noConflict();

/**
 * update the proposal coordinators drop down with coordinators for hierarchy starting at
 *  the unit number
 * @param unitNumber - the unit number
 */
function updateProposalCoordinators(unitNumber) {
	var getUrl = relativeUrl + unitNumber;
	$j.getJSON(getUrl, function( data ) {
		clearExistingProposalCoordinators();
		populateProposalCoordinators(data);
	});
};

/**
 * check if proposal coordinator exists in the drop down
 * @returns true if the coordinator exists, false otherwise
 */
function proposalCoordinatorsExists(principalName) {
	var exists = false;
	options = $j('#' + propCoordId + ' > option');
	for (i=0; i<options.length; i++) {
		if ($j(options[i]).val() == principalName) {
			exists = true;
			break;
		}
	}
	
	return exists;
};

/**
 * clear the existing entries in the proposal coordinators drop down
 */
function clearExistingProposalCoordinators() {
	$j('#' + propCoordId).empty();
};

/**
 * clear existing options and set newly fetched entries on the proposal coordinator drop down 
 * @param coordinators - an array in json format of coordinator info i.e:
 * no coordinators - [{"key":"","value":"select"}]
 * 1 coordinator found - [{"key":"","value":"select"}, {"key":"mos01","value":"Mos Munga"}]
 */
function populateProposalCoordinators(coordinators) {
	clearExistingProposalCoordinators();
	var options = [];
	for (i=0; i<coordinators.length; i++) {
		options.push("<option value=\"" + coordinators[i].key + "\">" + coordinators[i].value + "</option>");
	}
	$j('#' + propCoordId).html(options.join(""));
};





$j(function( jq, undefined ) {
   jq.widget( "ui.combobox", {
      version: "@VERSION",
      widgetEventPrefix: "combobox",
      uiCombo: null,
      uiInput: null,
      _wasOpen: false,
      _create: function() {
         var self = this,
             select = this.element.hide(),
             input, wrapper;

         input = this.uiInput =
                  jq( "<input />" )
                      .insertAfter(select)
                      .addClass("ui-widget ui-widget-content ui-corner-left ui-combobox-input")
                      .val( select.children(':selected').text() )
                      .attr('tabindex', select.attr( 'tabindex'))
                      .width(jq(this.element).width());

         wrapper = this.uiCombo =
            input.wrap( '<span>' )
               .parent()
               .addClass( 'ui-combobox' )
               .insertAfter( select );

         input
          .autocomplete({

             delay: 0,
             minLength: 0,

             appendTo: wrapper,
             source: jq.proxy( this, "_linkSelectList" ),
             select: function(event, ui) {
               //var selectedObj = ui.item;              
               jq(this).attr('title', ui.item.value);
           }
          });

         jq( "<button>" )
            .attr( "tabIndex", -1 )
            .attr( "type", "button" )
            .insertAfter( input )
            .button({
               icons: {
                  primary: "ui-icon-triangle-1-s"
               },
               text: false
            })
            .removeClass( "ui-corner-all" )
            .addClass( "ui-corner-right ui-button-icon ui-combobox-button" );


         // Our items have HTML tags.  The default rendering uses text()
         // to set the content of the <a> tag.  We need html().
         input.data( "ui-autocomplete" )._renderItem = function( ul, item ) {

               return jq( "<li>" )
                           .attr('class', item.option.className)
                           .append( jq( "<a>" ).html( item.label ) )
                           .appendTo( ul );

            };

         this._on( this._events );

      },


      _linkSelectList: function( request, response ) {

         var matcher = new RegExp( jq.ui.autocomplete.escapeRegex(request.term), 'i' );
         //response( this.element.children('option').map(function() {
         response( this.element.children('option:not([style*="display: none"])').map(function() {           
                  var text = jq( this ).text();

                  if ( this.value && ( !request.term || matcher.test(text) ) ) {
                     var optionData = {
                         label: text,
                         value: text,
                         option: this
                     };
                     if (request.term) {
                        optionData.label = text.replace(
                           new RegExp(
                              "(?![^&;]+;)(?!<[^<>]*)(" +
                              jq.ui.autocomplete.escapeRegex(request.term) +
                              ")(?![^<>]*>)(?![^&;]+;)", "gi"),
                              "<strong>$1</strong>");
                    }
                    return optionData;
                  }
              })
           );
      },

      _events: {

         "autocompletechange input" : function(event, ui) {

            var $el = jq(event.currentTarget);
            var changedOption = ui.item ? ui.item.option : null;
            if ( !ui.item ) {

               var matcher = new RegExp( "^" + jq.ui.autocomplete.escapeRegex( $el.val() ) + "$", "i" ),
               valid = false,
               matchContains = null,
               iContains = 0,
               iSelectCtr = -1,
               iSelected = -1,
               optContains = null;
               if (this.options.autofillsinglematch) {
                  matchContains = new RegExp(jq.ui.autocomplete.escapeRegex($el.val()), "i");
               }


               this.element.children( "option" ).each(function() {
                     var t = jq(this).text();
                     if ( t.match( matcher ) ) {
                        this.selected = valid = true;
                        return false;
                     }
                     if (matchContains) {
                        // look for items containing the value
                        iSelectCtr++;
                        if (t.match(matchContains)) {
                           iContains++;
                           optContains = jq(this);
                           iSelected = iSelectCtr;
                        }
                     }
                  });

                if ( !valid ) {
                   // autofill option:  if there is just one match, then select the matched option
                   if (iContains == 1) {
                      changedOption = optContains[0];
                      changedOption.selected = true;
                      var t2 = optContains.text();
                      $el.val(t2);
                      $el.data('ui-autocomplete').term = t2;
                      this.element.prop('selectedIndex', iSelected);
                      console.log("Found single match with '" + t2 + "'");
                   } else {

                      // remove invalid value, as it didn't match anything
                      $el.val( '' );

                      // Internally, term must change before another search is performed
                      // if the same search is performed again, the menu won't be shown
                      // because the value didn't actually change via a keyboard event
                      $el.data( 'ui-autocomplete' ).term = '';

                      this.element.prop('selectedIndex', -1);
                   }
                }
            }

            this._trigger( "change", event, {
                  item: changedOption
                });

         },

         "autocompleteselect input": function( event, ui ) {

            ui.item.option.selected = true;
            this._trigger( "select", event, {
                  item: ui.item.option
               });

         },

         "autocompleteopen input": function ( event, ui ) {

            this.uiCombo.children('.ui-autocomplete')
               .outerWidth(this.uiCombo.outerWidth(true));
         },

         "mousedown .ui-combobox-button" : function ( event ) {
            this._wasOpen = this.uiInput.autocomplete("widget").is(":visible");
         },

         "click .ui-combobox-button" : function( event ) {

            this.uiInput.focus();

            // close if already visible
            if (this._wasOpen)
               return;

            // pass empty string as value to search for, displaying all results
            this.uiInput.autocomplete("search", "");

         }

      },

      value: function ( newVal ) {
         var select = this.element,
             valid = false,
             selected;

         if ( !arguments.length ) {
            selected = select.children( ":selected" );
            return selected.length > 0 ? selected.val() : null;
         }

         select.prop('selectedIndex', -1);
         select.children('option').each(function() {
               if ( this.value == newVal ) {
                  this.selected = valid = true;
                  return false;
               }
            });

         if ( valid ) {
            this.uiInput.val(select.children(':selected').text());
            this.uiInput.attr('title', select.children(':selected').text())
         } else {
            this.uiInput.val( "" );
            this.element.prop('selectedIndex', -1);
         }

      },

      _destroy: function () {
         this.element.show();
         this.uiCombo.replaceWith( this.element );
      },

      widget: function () {
         return this.uiCombo;
      },

      _getCreateEventData: function() {

         return {
            select: this.element,
            combo: this.uiCombo,
            input: this.uiInput
         };
      }

    });


}(jQuery));